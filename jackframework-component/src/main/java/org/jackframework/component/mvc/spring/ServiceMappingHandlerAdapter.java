package org.jackframework.component.mvc.spring;

import org.jackframework.component.mvc.javaee.ServiceServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;

public class ServiceMappingHandlerAdapter implements HandlerAdapter, Ordered {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceMappingHandlerAdapter.class);

    protected static final Collection<String>
            SUPPORT_METHODS = Collections.singletonList(WebContentGenerator.METHOD_POST);

    protected int order = Ordered.LOWEST_PRECEDENCE - 2;

    @Override
    public boolean supports(Object handler) {
        return handler instanceof ServiceMappingHandler;
    }

    @Override
    public ModelAndView handle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            ServiceMappingHandler serviceHandler = (ServiceMappingHandler) handler;

            checkRequest(request);

            ServiceProcessContext processContext = new ServiceProcessContext();
            processContext.setRequest(request);
            processContext.setResponse(response);

            Object[] arguments;
            try {
                arguments = serviceHandler.convertArguments(processContext);
            } catch (Throwable e) {
                throw new ServiceException(ServiceErrorCodes.INVALID_PARAM, e, "Invalid param");
            }

            serviceHandler.resolveResult(processContext, serviceHandler.invoke(arguments));
            return null;
        } catch (ServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServiceServletException(e);
        }
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    protected void checkRequest(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        if (!WebContentGenerator.METHOD_POST.equals(method)) {
            throw new HttpRequestMethodNotSupportedException(method, SUPPORT_METHODS);
        }
    }

}
