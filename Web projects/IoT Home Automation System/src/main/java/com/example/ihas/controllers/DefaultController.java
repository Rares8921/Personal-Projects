package com.example.ihas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DefaultController {

    @GetMapping("/")
    public RedirectView defaultPage() {
        return new RedirectView("/dashboard");
    }
}
