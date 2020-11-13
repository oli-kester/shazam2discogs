package net.olikester.shazam2discogs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.olikester.shazam2discogs.dao.ConsumerTokenDao;
import net.olikester.shazam2discogs.dao.SessionDataDao;
import net.olikester.shazam2discogs.dao.TagDao;
import net.olikester.shazam2discogs.json.ShazamTagsDeserializer;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.SessionData;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.service.DiscogsService;

@Controller
public class MainController {

    @Value("${shazam2discogs.oauth-bypass}")
    private boolean OAUTH_BYPASS; // TODO add some sort of display that this is active

    @Autowired
    private TagDao tagDao;
    @Autowired
    private SessionDataDao sessionDataDao;
    @Autowired
    private ConsumerTokenDao tokenDao;
    @Autowired
    private DiscogsService discogsService;

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {
	ModelAndView mv = new ModelAndView("home");

	// if we're in OAUTH_BYPASS mode, add test keys to database
	if (OAUTH_BYPASS) {
	    JpaOAuthConsumerToken testToken = discogsService.createTestAccessToken(session.getId());
	    tokenDao.save(testToken);
	}

	return mv;
    }

    @PostMapping("/")
    public RedirectView handleFileUpload(@RequestParam("shazam-json-file") MultipartFile file, RedirectAttributes ra,
	    HttpSession session) {

	String fileContents = "";

	try {
	    fileContents = new String(file.getBytes());
	    ra.addFlashAttribute("ioSuccess", true);
	} catch (IOException e) {
	    ra.addFlashAttribute("ioSuccess", false);
	    e.printStackTrace();
	}

	ObjectMapper mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("ShazamTagsDeserializer", new Version(1, 0, 0, null, null, null));
	ArrayList<Tag> tags = new ArrayList<Tag>();
	module.addDeserializer(ArrayList.class, new ShazamTagsDeserializer());
	mapper.registerModule(module);

	try {
	    tags = mapper.readValue(fileContents, new TypeReference<ArrayList<Tag>>() {
	    });
	    ra.addFlashAttribute("parseSuccess", true);
	} catch (JsonProcessingException e) {
	    ra.addFlashAttribute("parseSuccess", false);
	    e.printStackTrace();
	}

	SessionData sessionData = new SessionData();
	sessionData.setSessionId(session.getId());

	sessionDataDao.save(sessionData);

	tags.stream().forEach(currTag -> {
	    currTag.setSession(sessionData);
	    tagDao.save(currTag);
	});

	RedirectView rv = new RedirectView("jsonSumbit");
	return rv;
    }

    @GetMapping("/jsonSumbit")
    public ModelAndView jsonSumbit(HttpServletRequest request) {
	Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

	ModelAndView mv = new ModelAndView();

	if (inputFlashMap != null) {
	    boolean ioSuccess = (boolean) inputFlashMap.get("ioSuccess");
	    boolean parseSuccess = (boolean) inputFlashMap.get("parseSuccess");
	    mv.addAllObjects(inputFlashMap);

	    if (ioSuccess && parseSuccess) {
		if (OAUTH_BYPASS) {
		    mv.setViewName("search");
		} else {
		    mv.setViewName("linkDiscogs");
		}
	    } else {
		// TODO better error messages in JSON Error document
		mv.setViewName("jsonError");
	    }
	} else {
	    // TODO make error page - no input received
	    mv.setViewName("error");
	}
	return mv;
    }
}