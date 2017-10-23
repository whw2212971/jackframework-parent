package org.jackframework.service.component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ServiceSessionFilter implements Filter {

    @SuppressWarnings("unchecked")
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServiceHolder.setRequestLocal((HttpServletRequest) request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }


}
