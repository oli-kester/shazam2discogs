package net.olikester.shazam2discogs.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.olikester.shazam2discogs.dao.TagDao;
import net.olikester.shazam2discogs.json.ShazamTagsDeserializer;
import net.olikester.shazam2discogs.model.TagList;

@Controller
public class MainController {

    private final static String SITE_TITLE = "Shazam2Discogs";

    @Autowired
    private TagDao tagDao;

    @GetMapping("/")
    public ModelAndView home() {
	ModelAndView mv = new ModelAndView("home");
	mv.addObject("SITE_TITLE", SITE_TITLE);
	return mv;
    }

    @PostMapping("/")
    public RedirectView handleFileUpload(@RequestParam("shazam-json-file") MultipartFile file, RedirectAttributes ra) {

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
	TagList tags = new TagList();
	module.addDeserializer(TagList.class, new ShazamTagsDeserializer());
	mapper.registerModule(module);

	try {
	    tags = mapper.readValue(fileContents, TagList.class);
	    ra.addFlashAttribute("parseSuccess", true);
	} catch (JsonProcessingException e) {
	    ra.addFlashAttribute("parseSuccess", false);
	    e.printStackTrace();
	}

	tags.toArrayList().stream().forEach(currTag -> {
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
	    mv.addObject("SITE_TITLE", SITE_TITLE);
	    mv.addAllObjects(inputFlashMap);

	    if (ioSuccess && parseSuccess) {
		mv.setViewName("linkDiscogs");
	    } else {
		mv.setViewName("jsonError");
	    }
	} else {
	    mv.setViewName("error");
	}
	return mv;
    }

}