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

    @GetMapping("/politiques")
    public String politiques(Model model) {
        model.addAttribute("title", "Politiques");
        return "secure/politiques";
    }

}
