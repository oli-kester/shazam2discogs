package com.olikester.shazam2discogs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.olikester.shazam2discogs.dao.ConsumerTokenDao;
import com.olikester.shazam2discogs.dao.MatchesDao;
import com.olikester.shazam2discogs.dao.TagDao;
import com.olikester.shazam2discogs.json.ShazamTagsDeserializer;
import com.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import com.olikester.shazam2discogs.model.Tag;
import com.olikester.shazam2discogs.model.TagReleaseMatch;
import com.olikester.shazam2discogs.service.DiscogsService;

@Controller
public class MainController {

    @Value("${shazam2discogs.oauth-bypass}")
    private boolean OAUTH_BYPASS;

    @Autowired
    private TagDao tagDao;
    @Autowired
    private ConsumerTokenDao tokenDao;
    @Autowired
    private DiscogsService discogsService;
    @Autowired
    private MatchesDao matchesDao;

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {
	ModelAndView mv = new ModelAndView("home");

	// if we're in OAUTH_BYPASS mode, add test keys to database
	if (OAUTH_BYPASS) {
	    JpaOAuthConsumerToken testToken = discogsService.createTestAccessToken(session.getId());
	    tokenDao.save(testToken);
	}

	mv.addObject("oauthBypassActive", OAUTH_BYPASS);
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
	    ra.addFlashAttribute("numTags", tags.size());
	} catch (JsonProcessingException e) {
	    ra.addFlashAttribute("parseSuccess", false);
	    e.printStackTrace();
	}

	tags.stream().forEach(currTag -> {
	    TagReleaseMatch match = new TagReleaseMatch();
	    match.setSessionId(session.getId());
	    match.setTag(currTag);
	    match.generateId();
	    tagDao.save(currTag);
	    matchesDao.save(match);
	});

	RedirectView rv = new RedirectView("jsonSumbit");
	return rv;
    }

    @GetMapping("/jsonSumbit")
    public ModelAndView jsonSumbit(HttpSession session, HttpServletRequest request) {
	Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	ModelAndView mv = new ModelAndView();

	if (inputFlashMap != null) {
	    boolean ioSuccess = (boolean) inputFlashMap.get("ioSuccess");
	    boolean parseSuccess = (boolean) inputFlashMap.get("parseSuccess");
	    mv.addObject("numTags", matchesDao.getAllUnmatchedTagsForSession(session.getId()).size());

	    if (ioSuccess && parseSuccess) {
		if (OAUTH_BYPASS) {
		    mv.setViewName("search");
		} else {
		    mv.setViewName("linkDiscogs");
		}
	    } else {
		// TODO better error messages in JSON Error document
		mv.addAllObjects(inputFlashMap);
		mv.setViewName("jsonError");
	    }
	} else {
	    // Shazam tags not parsed in previous request. Let's still try and find them in
	    // the database
	    int numTags = matchesDao.getAllTagsForSession(session.getId()).size();
	    if (numTags > 0) {
		mv.addObject("numTags", numTags);
		if (OAUTH_BYPASS) {
		    mv.setViewName("search");
		} else {
		    mv.setViewName("linkDiscogs");
		}
	    } else { // No tags parsed for this user, they shouldn't be here.
		mv.setViewName("home");
	    }
	}
	return mv;
    }

    /**
     * Export a CSV file of all the Shazam tags that weren't added to Discogs. 
     * 
     * @param response
     * @throws IOException
     */
    @GetMapping("csvExport")
    public void exportUnmatchedTags(HttpServletResponse response, HttpSession session) throws IOException {
	response.setContentType("text/csv");
	String headerKey = "Content-Disposition";
	String headerValue = "attachment; filename=s2d_unmatched_discogs_releases.csv";
	response.setHeader(headerKey, headerValue);

	Set<Tag> unmatchedTags = matchesDao.getAllUnmatchedTagsForSession(session.getId());

	ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
	String[] csvHeader = { "Tag ID", "Track Title", "Artist", "Album", "Label", "Release Year", "Image URL" };
	String[] nameMapping = { "id", "trackTitle", "artist", "album", "label", "releaseYear", "imageUrl" };

	csvWriter.writeHeader(csvHeader);

	for (Tag tag : unmatchedTags) {
	    csvWriter.write(tag, nameMapping);
	}

	csvWriter.close();
    }

    @GetMapping("exit")
    public ModelAndView exit(HttpSession session) {
	String sessionId = session.getId();
	ModelAndView mv = new ModelAndView();

	tokenDao.deleteById(sessionId);
	matchesDao.deleteAll(matchesDao.getAllMatchDataForSession(sessionId));

	mv.setViewName("goodbye");
	return mv;
    }
}