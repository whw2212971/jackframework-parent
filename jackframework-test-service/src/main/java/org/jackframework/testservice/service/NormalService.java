package org.jackframework.testservice.service;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.Publish;

import java.util.List;
import java.util.Map;

@EndService
@Publish("/api/normal")
public class NormalService {

    @Publish("/hello")
    public String hello() {
        return "Hello";
    }

    @Publish("/post")
    public List<Map<String, Object>> post(List<Map<String, Object>> param) {
        return param;
    }

}
