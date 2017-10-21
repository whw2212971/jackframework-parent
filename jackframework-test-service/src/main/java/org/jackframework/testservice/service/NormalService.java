package org.jackframework.testservice.service;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.Publish;

@EndService
@Publish("/api/normal")
public class NormalService {

    @Publish("/hello")
    public String hello() {
        return "Hello Normal Service";
    }

}
