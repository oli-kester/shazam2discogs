package net.olikester.shazam2discogs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.dao.ReleaseDao;
import net.olikester.shazam2discogs.dao.SessionDataDao;
import net.olikester.shazam2discogs.dao.TagDao;
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
	    JpaOAuthConsumerToken requestToken = tokenStore.findById(session.getId()).orElseThrow();
	    // TODO check if request token is already an access token (that means OAuth has
	    // been completed).
	    OAuthConsumerToken accessToken = discogsService.fetchAccessToken(requestToken.toOAuthConsumerToken(),
		    requestParams.get("oauth_verifier"));
	    tokenStore.save(new JpaOAuthConsumerToken(session.getId(), accessToken));
	    mv.setViewName("search");
	} else {
	    mv.setViewName("error");
	}
	return mv;
    }

    @GetMapping("searchTags")
    public ModelAndView searchTags(@RequestParam(name = "media-type") MediaFormats preferredFormat ,HttpSession session) {
	ModelAndView mv = new ModelAndView();
	String sessionId = session.getId();
	Optional<JpaOAuthConsumerToken> userToken = tokenStore.findById(sessionId);

	if (authCheck(userToken)) {
	    SessionData sessionData = sessionDataDao.findById(sessionId).orElseThrow();
	    List<Tag> userTags = sessionData.getTags();

	    userTags.stream().forEach(currTag -> {
		// search Discogs database for best match for each tag
		ArrayList<Release> discogsSearchResults = discogsService.getReleaseList(currTag, userToken.get());
		
		if (discogsSearchResults.isEmpty()) {
		    // if we didn't find any results, try a less specific search.
		    String searchTerm = currTag.getSimpleSearchTerm();
		    discogsSearchResults = discogsService.getReleaseList(searchTerm, userToken.get());

		    if (discogsSearchResults.isEmpty()) {
			System.err.println("No results found for - " + searchTerm);
			return; //if there's still no results found, skip this one. 
		    }
		}

		// add best match to release database
		Release bestMatch = Release.selectPreferredReleaseByFormat(discogsSearchResults, preferredFormat);
		currTag.setLinkedDiscogsRelease(bestMatch);
		bestMatch.getLinkedTags().add(currTag);
		tagDao.save(currTag);
		releaseDao.save(bestMatch);
	    });
	    
	    // create results view
	    mv.setViewName("results");
	    mv.addObject("tags", tagDao.findAll());	    
	}
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
