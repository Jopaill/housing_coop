package com.jordanpaille.housing.coop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    @GetMapping("/index")
    public String helloWorld() {
        return "index";
    }

    @GetMapping("/membres")
    public String membres(Model model) {
        model.addAttribute("title", "Liste des membres");
        return "in-construction";
    }

    @GetMapping("/organigramme")
    public String organigramme(Model model) {
        model.addAttribute("title", "Organigramme");
        return "in-construction";
    }

    @GetMapping("/politiques")
    public String politiques(Model model) {
        model.addAttribute("title", "Politiques");
        return "in-construction";
    }

    @GetMapping("/comites")
    public String comites(Model model) {
        model.addAttribute("title", "Liste des comités");
        return "in-construction";
    }

    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("title", "Rôles et tâches à faire");
        return "in-construction";
    }
}
