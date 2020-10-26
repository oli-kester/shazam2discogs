package net.olikester.shazam2discogs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import net.olikester.shazam2discogs.service.DiscogsService;
import net.olikester.shazam2discogs.service.OauthRequestToken;

@Controller
public class DiscogsController {

    @Autowired
    private DiscogsService discogsService;

    @Value("${shazam2discogs.site-title}")
    private String SITE_TITLE;

    @GetMapping("/login")
    public ModelAndView login() {
	ModelAndView mv = new ModelAndView();
	OauthRequestToken accessToken = discogsService.fetchRequestToken();
	return mv;
    }

}
