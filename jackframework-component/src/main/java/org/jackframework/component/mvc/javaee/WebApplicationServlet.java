package org.jackframework.component.mvc.javaee;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebApplicationServlet extends DispatcherServlet {

    public WebApplicationServlet() {
    }

    public WebApplicationServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            super.doService(request, response);
        } catch (ServiceServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServiceServletException(e);
        }
    }

}
