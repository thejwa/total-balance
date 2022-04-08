package com.jwa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {
    @RequestMapping({"/", "", "/index", "/home"})
    public String redirectToSwaggerPage(){
        return "redirect:/ui";
    }
}
