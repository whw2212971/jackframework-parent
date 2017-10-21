package org.jackframework.testweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/normal")
public class NormalController {

    @RequestMapping("/hello")
    public String hello() {
        return "/testweb/hello";
    }

}
