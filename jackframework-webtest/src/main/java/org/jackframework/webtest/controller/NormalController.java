package org.jackframework.webtest.controller;

import com.alibaba.fastjson.JSON;
import org.jackframework.common.reflect.AsmTools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class NormalController<E, T extends Map> {

    public String name;

    public int age;

    public Map<Map<E, Map<T, ? extends Map<String, ? super ArrayList<?>>[][]>>, List<Object>>[] list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<Map<E, Map<T, ? extends Map<String, ? super ArrayList<?>>[][]>>, List<Object>>[] getList() {
        return list;
    }

    public void setList(Map<Map<E, Map<T, ? extends Map<String, ? super ArrayList<?>>[][]>>, List<Object>>[] list) {
        this.list = list;
    }

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
        System.out.println(NormalController.class.getField("list").getGenericType().toString());
        System.out.println(AsmTools.getSignature(NormalController.class.getField("list").getGenericType()));
    }

}
