package org.jackframework.webtest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/normal")
public class NormalController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello";
    }

}
