package net.olikester.shazam2discogs.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String handleFileUpload(@RequestParam("shazam-json-file") MultipartFile file,
	    RedirectAttributes redirectAttributes) {

	/**
	 * TODO exceptions for file access errors -
	 * https://github.com/spring-guides/gs-uploading-files/blob/master/complete/src/main/java/com/example/uploadingfiles/storage/FileSystemStorageService.java
	 **/

	String fileContents = "";

	try {
	    fileContents = new String(file.getBytes());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	ObjectMapper mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("ShazamTagsDeserializer", new Version(1, 0, 0, null, null, null));
	TagList tags = new TagList();
	module.addDeserializer(TagList.class, new ShazamTagsDeserializer());
	mapper.registerModule(module);

	try {
	    tags = mapper.readValue(fileContents, TagList.class);
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	tags.toArrayList().stream().forEach(currTag -> {
	    tagDao.save(currTag);
	});

	redirectAttributes.addFlashAttribute("message",
		"You successfully uploaded " + file.getOriginalFilename() + "!");

	return "redirect:/";
    }
}