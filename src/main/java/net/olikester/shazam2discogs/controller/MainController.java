package net.olikester.shazam2discogs.controller;

import net.olikester.shazam2discogs.dao.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class MainController {

    private final static String SITE_TITLE = "Shazam2Discogs";

    @Autowired
    private TagDao tagDao;

    @GetMapping ( "/" )
    public String index (Model model) {

        model.addAttribute("SITE_TITLE", SITE_TITLE);
        return "index";
    }

    @PostMapping( "/" )
    public String handleFileUpload (@RequestParam ( "file" ) MultipartFile file,
                                    RedirectAttributes redirectAttributes) {

        /**
         * TODO exceptions for file access errors -
         * https://github.com/spring-guides/gs-uploading-files/blob/master/complete/src/main/java/com/example/uploadingfiles/storage/FileSystemStorageService.java
         **/
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @GetMapping ( "/error" )
    public String error (Model model) {
        model.addAttribute("SITE_TITLE", SITE_TITLE);
        return "error";
    }
}