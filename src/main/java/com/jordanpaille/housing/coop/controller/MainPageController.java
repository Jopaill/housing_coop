package com.jordanpaille.housing.coop.controller;

import com.jordanpaille.housing.coop.dao.memberlist.MemberListDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class MainPageController {

    private final MemberListDao memberListDao;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String helloWorld() {
        return "secure/home";
    }

    @GetMapping("/membres")
    public String membres(Model model) {
        model.addAttribute("title", "Liste des membres");
        model.addAttribute("members", memberListDao.getMemberList());
        return "secure/members";
    }

    @GetMapping("/organigramme")
    public String organigramme(Model model) {
        model.addAttribute("title", "Organigramme");
        return "secure/in-construction";
    }

    @GetMapping("/politiques")
    public String politiques(Model model) {
        model.addAttribute("title", "Politiques");
        return "secure/in-construction";
    }

    @GetMapping("/calendrier")
    public String calendrier(Model model) {
        model.addAttribute("title", "Calendrier");
        return "secure/in-construction";
    }

    @GetMapping("/comites")
    public String comites(Model model) {
        model.addAttribute("title", "Liste des comités");
        return "secure/in-construction";
    }

    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("title", "Rôles et tâches à faire");
        return "secure/in-construction";
    }
}
