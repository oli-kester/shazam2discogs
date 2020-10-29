package net.olikester.shazam2discogs.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
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

    @Value("${shazam2discogs.site-title}")
    private String SITE_TITLE;

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
	    // TODO check if request token is already an access token (that means OAuth is
	    // completed).
	    OAuthConsumerToken accessToken = discogsService.fetchAccessToken(requestToken.toOAuthConsumerToken(),
		    requestParams.get("oauth_verifier"));
	    tokenStore.save(new JpaOAuthConsumerToken(session.getId(), accessToken));
	} else {
	    // TODO fail, unknown request.
	}
	return new ModelAndView();
    }
}
