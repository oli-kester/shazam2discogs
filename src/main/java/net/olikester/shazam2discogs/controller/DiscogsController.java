package net.olikester.shazam2discogs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DiscogsController {

    @Value("${shazam2discogs.site-title}")
    private String SITE_TITLE;
    private final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    
}
