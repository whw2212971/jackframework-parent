package org.jackframework.service.converter;

import org.jackframework.service.core.HttpProcessContext;
import org.jackframework.service.spring.ServiceMethodHandler;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;

public class JsonParamConverter implements HttpParamConverter {

    @Override
    public void init(ServiceMethodHandler handler) {

    }

    @Override
    public Object[] convertParam(HttpProcessContext processContext) {
        return new Object[0];
    }

}
