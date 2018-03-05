package org.jackframework.component.mvc.javaee;

import org.jackframework.component.mvc.spring.ServiceHolder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ServiceRequestContextFilter extends ServiceHolder implements Filter {

    public static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @SuppressWarnings("unchecked")
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        ServiceHolder.setRequestLocal(httpServletRequest);
        ServiceHolder.setRequestRoute(URL_PATH_HELPER.getLookupPathForRequest(httpServletRequest));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }


}
