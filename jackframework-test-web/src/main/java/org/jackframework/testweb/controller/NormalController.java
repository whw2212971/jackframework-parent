package org.jackframework.testweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/normal")
public class NormalController {

    @RequestMapping("/hello")
    public String hello() {
        return "/testweb/hello";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String post() {
        return "/testweb/hello";
    }

}
