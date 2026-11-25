package com.jordanpaille.housing.coop.controller;

import com.jordanpaille.housing.coop.services.announcement.AnnouncementService;
import com.jordanpaille.housing.coop.services.memberlist.MemberListService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
public class MainPageController {

    private final MemberListService memberListService;
    private final AnnouncementService announcementService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String helloWorld(Model model, Authentication authentication) {
        model.addAttribute("announcements", announcementService.getAnnouncements());
        model.addAttribute("canPublishPost", announcementService.canPublishPost(authentication.getName()));
        return "secure/home";
    }

    @PostMapping("/home/announcements")
    public String publishAnnouncement(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "bodyMarkdown", required = false) String bodyMarkdown,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            announcementService.publishPost(title, bodyMarkdown, authentication.getName());
            redirectAttributes.addFlashAttribute("announcementSuccess", "Votre annonce a été publiée.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("announcementError", exception.getMessage());
            redirectAttributes.addFlashAttribute("draftTitle", truncate(title, 40));
            redirectAttributes.addFlashAttribute("draftBody", truncate(bodyMarkdown, 4000));
            redirectAttributes.addFlashAttribute("openAnnouncementDialog", true);
        }

        return "redirect:/home";
    }

    @PostMapping(path = "/home/announcements/preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> previewAnnouncement(
            @RequestParam(name = "bodyMarkdown", required = false) String bodyMarkdown,
            Authentication authentication) {
        if (!announcementService.canPublishPost(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Votre compte n'est pas associé à un membre.");
        }

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(announcementService.renderMarkdownPreview(bodyMarkdown));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/membres")
    public String membres(Model model) {
        model.addAttribute("title", "Liste des membres");
        model.addAttribute("members", memberListService.getMemberList());
        return "secure/members";
    }

    @GetMapping("/politiques")
    public String politiques(Model model) {
        model.addAttribute("title", "Politiques");
        return "secure/politiques";
    }

    private String truncate(String value, int maximumLength) {
        if (value == null) {
            return "";
        }
        return value.substring(0, Math.min(value.length(), maximumLength));
    }

}
