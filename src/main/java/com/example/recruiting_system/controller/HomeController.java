package com.example.recruiting_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to Recruiting System");
        model.addAttribute("message", "Your comprehensive recruitment management platform");
        return "home";
    }
}
