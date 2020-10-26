package net.olikester.shazam2discogs.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.service.DiscogsService;

@SuppressWarnings("deprecation")
@Controller
public class DiscogsController {

    // TODO change this for production
    private static final String OAUTH_CALLBACK_URL = "http://127.0.0.1:8080/oauthCallback";
    
    @Autowired
    private DiscogsService discogsService;
    @Autowired
    private ConsumerTokenDao tokenStore;

    @Value("${shazam2discogs.site-title}")
    private String SITE_TITLE;

    @GetMapping("/login")
    public RedirectView login(HttpSession session) {
	RedirectView rv = new RedirectView();
	OAuthConsumerToken accessToken = discogsService.fetchRequestToken(OAUTH_CALLBACK_URL);
	JpaOAuthConsumerToken jpaToken = new JpaOAuthConsumerToken(session.getId(), accessToken);
	tokenStore.save(jpaToken);
	rv.setUrl(DiscogsService.AUTHORIZATION_URL + "?oauth_token=" + accessToken.getValue());
	return rv;
    }

    @GetMapping(OAUTH_CALLBACK_URL)
    public ModelAndView oauthCallback() {
	// TODO check if the request was approved. 
	return new ModelAndView();
    }
}
