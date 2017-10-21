package org.jackframework.servicetest.service;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.PublishApi;

@EndService
@PublishApi("/api/normalService")
public class NormalService {

    @PublishApi("/hello")
    public String hello() {
        return "Hello";
    }

}
