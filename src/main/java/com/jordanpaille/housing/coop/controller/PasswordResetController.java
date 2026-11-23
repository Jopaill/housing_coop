package com.jordanpaille.housing.coop.controller;

import com.jordanpaille.housing.coop.services.passwordreset.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
@RequestMapping("/public")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "public/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String username, RedirectAttributes attributes) { // username = email
        username = username.toLowerCase();

        passwordResetService.initiatePasswordReset(username);

        attributes.addAttribute("sent", true);
        attributes.addFlashAttribute("username", username);
        return "redirect:/public/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "public/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String token,
                                      @RequestParam String newPassword) {
        boolean success = passwordResetService.resetPassword(token, newPassword);
        return success ? "redirect:/public/login?passwordReset" : "redirect:/public/reset-password?error&token=" + token;
    }

    @GetMapping("/create-account")
    public String createAccountPage() {
        return "public/create-account";
    }

    @PostMapping("/create-account")
    public String handleCreateAccountPage(@RequestParam String username, RedirectAttributes attributes) {
        username = username.toLowerCase();

        passwordResetService.initiatePasswordReset(username);

        attributes.addAttribute("sent", true);
        attributes.addFlashAttribute("username", username);
        return "redirect:/public/create-account";
    }
}
