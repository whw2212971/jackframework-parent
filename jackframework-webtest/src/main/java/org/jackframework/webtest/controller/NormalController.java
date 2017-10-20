package org.jackframework.webtest.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class NormalController {

    public String name;

    public int age;

    public List<?> list;

    public Object[] getArguments() {
        Object[] result = new Object[100];
        result[0] = name;
        result[1] = age;
        result[2] = list;
        return result;
    }

    @RequestMapping("hello")
    @ResponseBody
    public String hello() {
        return "Hello";
    }

    public static void main(String[] args) throws NoSuchFieldException {
        NormalController result = JSON.parseObject("{name:'Hello',age:1}", NormalController.class);
        System.out.println(result.name);
        System.out.println(result.age);
        System.out.println(result.list);
        System.out.println(NormalController.class.getField("list").getGenericType().toString());
    }

}
