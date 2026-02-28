package com.example.ihas.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RequestController {
    @GetMapping("/register")
    public RedirectView redirectToRegisterPage() {
        return new RedirectView("/auth/register.html");
    }

    @GetMapping("/login")
    public RedirectView redirectToLoginPage() {
        return new RedirectView("/auth/login.html");
    }

    @GetMapping("/api/test")
    public String testJwt() {
        return "You're authorized!";
    }
}
