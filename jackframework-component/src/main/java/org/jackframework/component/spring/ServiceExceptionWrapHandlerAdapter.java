package org.jackframework.component.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceExceptionWrapHandlerAdapter
        implements HandlerAdapter, Ordered, ApplicationContextAware, InitializingBean {

    protected int order = Ordered.LOWEST_PRECEDENCE - 1;

    protected ApplicationContext applicationContext;

    protected HandlerAdapter targetHandlerAdapter;

    protected Class<? extends HandlerAdapter> targetHandlerAdapterType;

    @Override
    public boolean supports(Object handler) {
        return targetHandlerAdapter.supports(handler);
    }

    @Override
    public ModelAndView handle(HttpServletRequest request,
                               HttpServletResponse response, Object handler) throws Exception {
        try {
            return targetHandlerAdapter.handle(request, response, handler);
        } catch (Throwable e) {
            throw new ServiceServletException(e);
        }
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return targetHandlerAdapter.getLastModified(request, handler);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTargetHandlerAdapterType(Class<? extends HandlerAdapter> targetHandlerAdapterType) {
        this.targetHandlerAdapterType = targetHandlerAdapterType;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        targetHandlerAdapter = applicationContext.getBean(targetHandlerAdapterType);
    }

}
