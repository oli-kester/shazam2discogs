package net.olikester.shazam2discogs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    private final static String SITE_TITLE = "Shazam2Discogs";

//    @Autowired
//    private TagDao tagDao;

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
//            file.

	redirectAttributes.addFlashAttribute("message",
		"You successfully uploaded " + file.getOriginalFilename() + "!");

	return "redirect:/";
    }
}