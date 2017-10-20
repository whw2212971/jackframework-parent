package org.jackframework.service.spring;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceMethodHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof ServiceMethodHandler;
    }

    @Override
    public ModelAndView handle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ServiceMethodHandler serviceHandler = (ServiceMethodHandler) handler;
        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1;
    }

}
