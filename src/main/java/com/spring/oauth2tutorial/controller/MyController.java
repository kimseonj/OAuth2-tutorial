package com.spring.oauth2tutorial.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @GetMapping("/my")
    public String myAPI() {
        return "My route";
    }
}
