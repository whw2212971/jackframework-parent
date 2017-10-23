package org.jackframework.service.component;

import org.jackframework.common.exceptions.RunningException;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ServiceSessionFilter implements Filter {

    protected SessionRepository<? extends ExpiringSession> sessionRepository;

    @SuppressWarnings("unchecked")
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext applicationContext =
                WebApplicationContextUtils.findWebApplicationContext(filterConfig.getServletContext());
        if (applicationContext == null) {
            throw new RunningException("Could not found the {} instance.", WebApplicationContext.class.getName());
        }
        this.sessionRepository =
                (SessionRepository<? extends ExpiringSession>) applicationContext.getBean(SessionRepository.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServiceSessionHolder.setCurrentLocal((HttpServletRequest) request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }


}
