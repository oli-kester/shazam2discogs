package net.olikester.shazam2discogs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.dao.MatchesDao;
import net.olikester.shazam2discogs.dao.ReleaseDao;
import net.olikester.shazam2discogs.model.DiscogsAdditionStatus;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.MediaFormats;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.model.TagReleaseMatch;
import net.olikester.shazam2discogs.service.DiscogsService;

@SuppressWarnings("deprecation")
@Controller
public class DiscogsController {

    private static final String OAUTH_CALLBACK = "/oauthCallback";
    private static final Logger logger = LoggerFactory.getLogger(DiscogsController.class);

    @Autowired
    private DiscogsService discogsService;
    @Autowired
    private ConsumerTokenDao tokenStore;
    @Autowired
    private ReleaseDao releaseDao;
    @Autowired
    private MatchesDao matchesDao;

    @Autowired
    private Set<String> cancelTaskSessionIds;
    @Autowired
    private Map<String, Integer> taskProgress;

    @GetMapping("/login")
    public RedirectView login(HttpSession session) {
	RedirectView rv = new RedirectView();
	String appBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
	OAuthConsumerToken requestToken = discogsService.fetchRequestToken(appBaseUrl + OAUTH_CALLBACK);
	JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), requestToken);
	tokenStore.save(jpaToken);
	rv.setUrl(DiscogsService.AUTHORIZATION_URL + "?oauth_token=" + requestToken.getValue());
	return rv;
    }

    @GetMapping(OAUTH_CALLBACK)
    public ModelAndView oauthCallback(@RequestParam Map<String, String> requestParams, HttpSession session) {
	ModelAndView mv = new ModelAndView();
	if (requestParams.containsKey("denied")) {
	    mv.setViewName("home");
	} else if (requestParams.containsKey("oauth_token") && requestParams.containsKey("oauth_verifier")) {
	    JpaOAuthConsumerToken requestToken = tokenStore.findById(session.getId()).orElseThrow();
	    // TODO check if request token is already an access token (that means OAuth has
	    // been completed).
	    OAuthConsumerToken accessToken = discogsService.fetchAccessToken(requestToken.toOAuthConsumerToken(),
		    requestParams.get("oauth_verifier"));
	    JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), accessToken);
	    jpaToken.setUsername(discogsService.getUserName(jpaToken));
	    tokenStore.save(jpaToken);
	    mv.addObject("numTags", matchesDao.getAllUnmatchedTagsForSession(session.getId()).size());
	    mv.setViewName("search");
	} else {
	    logger.error("Session - " + session.getId() + ", unrecognised OAuth response from Discogs - \n"
		    + requestParams.toString());
	    throw new IllegalArgumentException("Unrecognised OAuth response from Discogs");
	}
	return mv;
    }

    @GetMapping("searchTags")
    @ResponseBody
    public ResponseEntity<String> searchTags(@RequestParam(name = "mediaType") MediaFormats preferredFormat,
	    HttpSession session) {
	String sessionId = session.getId();
	Optional<JpaOAuthConsumerToken> userToken = tokenStore.findById(sessionId);
	taskProgress.put(sessionId, 0);
	final AtomicInteger progressCounter = new AtomicInteger();

	if (authCheck(userToken)) {
	    Set<Tag> userTags = matchesDao.getAllTagsForSession(sessionId);

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
			continue; // if there's still no results found, skip this one.
		    }
		}

		// throw away master releases
		List<Release> nonMasterReleases = discogsSearchResults.stream()
			.filter(release -> release.getReleaseType().equals("release")).collect(Collectors.toList());

		// add best match to release database
		Release bestMatch = Release.selectPreferredReleaseByFormat(nonMasterReleases, preferredFormat);
		TagReleaseMatch currMatchRecord = matchesDao.getByTagAndSessionId(sessionId, currTag.getId());
		currMatchRecord.setRelease(bestMatch);
		bestMatch.addMatch(currMatchRecord);
		currTag.addMatch(currMatchRecord);
		matchesDao.save(currMatchRecord);

		// update search progress so this can be requested by the webpage (casting to
		// avoid rounding errors)
		double progressPercentage = (progressCounter.incrementAndGet() / (double) userTags.size()) * 100;
		taskProgress.put(sessionId, (int) progressPercentage);
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
	return taskProgress.getOrDefault(session.getId(), 100);
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
	mv.addObject("tags", matchesDao.getAllMatchDataForSession(session.getId()));
	return mv;
    }

    @PostMapping("addToDiscogs")
    @ResponseBody
    public ResponseEntity<String> addToDiscogs(@RequestParam Map<String, String> params, HttpSession session) {
	String sessionId = session.getId();
	Optional<JpaOAuthConsumerToken> userToken = tokenStore.findById(sessionId);
	taskProgress.put(sessionId, 0);
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

	    // for loop lets us break stream when the user cancels the search
	    for (Release release : releasesToAdd) {
		if (cancelTaskSessionIds.contains(sessionId)) {
		    break;
		}

		TagReleaseMatch currMatch = matchesDao.getByReleaseAndSessionId(sessionId, release.getId());
		boolean wasAdded = discogsService.addReleaseToUserWantlist(release, userToken.get());

		if (wasAdded) { // save this to the match table
		    currMatch.setDiscogsAdditionStatus(DiscogsAdditionStatus.ADDED);
		} else {
		    currMatch.setDiscogsAdditionStatus(DiscogsAdditionStatus.FAILED);
		}
		matchesDao.save(currMatch);

		double progressPercentage = (progressCounter.incrementAndGet() / (double) releasesToAdd.size()) * 100;
		taskProgress.put(sessionId, (int) progressPercentage);
	    }

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
	String sessionId = session.getId();
	
	int numReleasesAdded = matchesDao.getNumberOfReleasesAddedBySessionId(sessionId);
	Set<Release> failedReleases = matchesDao.getFailedReleasesBySessionId(sessionId);
	int numReleasesFailed = failedReleases.size();
	
	mv.addObject("numReleasesProcessed", numReleasesAdded + numReleasesFailed);
	mv.addObject("numReleasesAdded", numReleasesAdded);
	mv.addObject("numReleasesFailed", numReleasesFailed);
	mv.addObject("failedReleases", matchesDao.getFailedReleasesBySessionId(sessionId));
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
