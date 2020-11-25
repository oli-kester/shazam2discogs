package net.olikester.shazam2discogs.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.dao.DiscogsAdditionStatusDao;
import net.olikester.shazam2discogs.dao.TaskProgressDao;
import net.olikester.shazam2discogs.dao.ReleaseDao;
import net.olikester.shazam2discogs.dao.SessionDataDao;
import net.olikester.shazam2discogs.dao.TagDao;
import net.olikester.shazam2discogs.model.TaskProgress;
import net.olikester.shazam2discogs.model.DiscogsAdditionStatus;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.MediaFormats;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.SessionData;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.service.DiscogsService;

@SuppressWarnings("deprecation")
@Controller
public class DiscogsController {

    // TODO change this for production
    private static final String APP_BASE_URL = "http://127.0.0.1:8080";
    private static final String OAUTH_CALLBACK = "/oauthCallback";

    @Autowired
    private DiscogsService discogsService;
    @Autowired
    private ConsumerTokenDao tokenStore;
    @Autowired
    private ReleaseDao releaseDao;
    @Autowired
    private SessionDataDao sessionDataDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private TaskProgressDao taskProgressDao;
    @Autowired
    private DiscogsAdditionStatusDao discogsAdditionStatusDao;

    @Autowired
    private HashSet<String> cancelTaskSessionIds;

    @GetMapping("/login")
    public RedirectView login(HttpSession session) {
	RedirectView rv = new RedirectView();
	OAuthConsumerToken requestToken = discogsService.fetchRequestToken(APP_BASE_URL + OAUTH_CALLBACK);
	JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), requestToken);
	tokenStore.save(jpaToken);
	rv.setUrl(DiscogsService.AUTHORIZATION_URL + "?oauth_token=" + requestToken.getValue());
	return rv;
    }

    @GetMapping(OAUTH_CALLBACK)
    public ModelAndView oauthCallback(@RequestParam Map<String, String> requestParams, HttpSession session) {
	ModelAndView mv = new ModelAndView();
	if (requestParams.containsKey("denied")) {
	    // TODO request cancelled by user. Give try again option?
	} else if (requestParams.containsKey("oauth_token") && requestParams.containsKey("oauth_verifier")) {
	    System.out.println("Session ID - " + session.getId()); // TODO remove
	    JpaOAuthConsumerToken requestToken = tokenStore.findById(session.getId()).orElseThrow();
	    // TODO check if request token is already an access token (that means OAuth has
	    // been completed).
	    OAuthConsumerToken accessToken = discogsService.fetchAccessToken(requestToken.toOAuthConsumerToken(),
		    requestParams.get("oauth_verifier"));
	    JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), accessToken);
	    jpaToken.setUsername(discogsService.getUserName(jpaToken));
	    tokenStore.save(jpaToken);
	    mv.setViewName("search");
	} else {
	    mv.setViewName("error"); // TODO unrecognised OAuth response from Discogs.
	}
	return mv;
    }

    @GetMapping("searchTags")
    @ResponseBody
    public ResponseEntity<String> searchTags(@RequestParam(name = "mediaType") MediaFormats preferredFormat,
	    HttpSession session) {
	String sessionId = session.getId();
	Optional<JpaOAuthConsumerToken> userToken = tokenStore.findById(sessionId);
	taskProgressDao.save(new TaskProgress(sessionId, 0));
	final AtomicInteger progressCounter = new AtomicInteger();

	if (authCheck(userToken)) {
	    SessionData sessionData = sessionDataDao.findById(sessionId).orElseThrow();
	    List<Tag> userTags = sessionData.getTags();

	    // reset cancellation status
	    cancelTaskSessionIds.remove(sessionId);

	    // for loop lets us break stream when the user cancels the search
	    for (Tag currTag : userTags) {
		if (cancelTaskSessionIds.contains(sessionId)) {
		    break;
		}

		// search Discogs database for best match for each tag
		ArrayList<Release> discogsSearchResults = discogsService.getReleaseList(currTag, userToken.get());

		if (discogsSearchResults.isEmpty()) {
		    // if we didn't find any results, try a less specific search.
		    String searchTerm = currTag.getSimpleSearchTerm();
		    discogsSearchResults = discogsService.getReleaseList(searchTerm, userToken.get());

		    if (discogsSearchResults.isEmpty()) {
			System.err.println("No results found for - " + searchTerm);
			continue; // if there's still no results found, skip this one.
		    }
		}

		// add best match to release database
		Release bestMatch = Release.selectPreferredReleaseByFormat(discogsSearchResults, preferredFormat);
		currTag.setLinkedDiscogsRelease(bestMatch);
		bestMatch.getLinkedTags().add(currTag);
		tagDao.save(currTag);
		releaseDao.save(bestMatch);

		// update search progress so this can be requested by the webpage (casting to
		// avoid rounding errors)
		double progressPercentage = (progressCounter.incrementAndGet() / (double) userTags.size()) * 100;
		taskProgressDao.save(new TaskProgress(sessionId, (int) progressPercentage));
	    }
	    if (cancelTaskSessionIds.contains(sessionId)) {
		return ResponseEntity.status(499).build();
	    }
	    return ResponseEntity.ok().build();
	}
	return ResponseEntity.status(401).build();
    }

    @GetMapping("getProgress")
    @ResponseBody
    public int getDiscogsSearchProgress(HttpSession session) {
	return taskProgressDao.getOne(session.getId()).getSearchProgress();
    }

    @GetMapping("stopSearch")
    @ResponseBody
    public void stopDiscogsSearch(HttpSession session) {
	cancelTaskSessionIds.add(session.getId());
    }

    @GetMapping("searchResults")
    public ModelAndView searchResults(HttpSession session) {
	ModelAndView mv = new ModelAndView();
	mv.setViewName("results");
	mv.addObject("tags", tagDao.findAll()); // TODO need to restrict to just our session.
	return mv;
    }

    @PostMapping("addToDiscogs")
    @ResponseBody
    public ResponseEntity<String> addToDiscogs(@RequestParam Map<String, String> params, HttpSession session) {
	String sessionId = session.getId();
	Optional<JpaOAuthConsumerToken> userToken = tokenStore.findById(sessionId);
	taskProgressDao.save(new TaskProgress(sessionId, 0));
	final AtomicInteger progressCounter = new AtomicInteger();

	// reset cancellation status
	cancelTaskSessionIds.remove(sessionId);

	if (authCheck(userToken)) {
	    List<Release> releasesToAdd = params.entrySet().stream()
		    .filter(e -> e.getValue().equals("true") && e.getKey().startsWith("add-")).map((e) -> {
			String releaseId = e.getKey().substring(4);
			Release release = releaseDao.findById(releaseId).orElseThrow();
			return release;
		    }).collect(Collectors.toList());

	    List<Release> releaseFailedAdditions = new ArrayList<Release>();

	    // for loop lets us break stream when the user cancels the search
	    for (Release release : releasesToAdd) {
		if (cancelTaskSessionIds.contains(sessionId)) {
		    break;
		}

		boolean wasAdded = discogsService.addReleaseToUserWantlist(release, userToken.get());
		if (!wasAdded) {
		    releaseFailedAdditions.add(release);
		}

		double progressPercentage = (progressCounter.incrementAndGet() / (double) releasesToAdd.size()) * 100;
		taskProgressDao.save(new TaskProgress(sessionId, (int) progressPercentage));
	    }

	    DiscogsAdditionStatus additionStatus = new DiscogsAdditionStatus();
	    additionStatus.setSessionId(sessionId);
	    additionStatus.setNumReleasesProcessed(releasesToAdd.size());
	    additionStatus.setNumReleasesAdded(releasesToAdd.size() - releaseFailedAdditions.size());
	    additionStatus.setNumFailedReleases(releaseFailedAdditions.size());
	    additionStatus.setFailedReleases(releaseFailedAdditions);
	    discogsAdditionStatusDao.save(additionStatus);
	    
	    if (cancelTaskSessionIds.contains(sessionId)) {
		return ResponseEntity.status(499).build();
	    }
	    return ResponseEntity.ok().build();
	}
	return ResponseEntity.status(401).build();
    }
    
    @GetMapping("finished")
    public ModelAndView finishedResults(HttpSession session) {
	ModelAndView mv = new ModelAndView();
	mv.addObject("additionStatus", discogsAdditionStatusDao.findById(session.getId()).orElseThrow());
	mv.setViewName("finished");
	return mv;
    }

    /**
     * Check to see if a user is authenticated before we send requests.
     * 
     * @param userToken
     * @return whether the user is authenticated.
     */
    private boolean authCheck(Optional<JpaOAuthConsumerToken> userToken) {
	if (userToken.isPresent()) {
	    if (userToken.get().isAccessToken()) {
		return true;
	    } else {
		throw new IllegalStateException(
			"OAuth process not complete (it was started, but we have no access token)");
	    }
	} else {
	    throw new IllegalStateException("User not found in OAuth data store. ");
	}
    }
}
