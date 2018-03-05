package org.jackframework.component.mvc.javaee;

import org.jackframework.component.mvc.spring.DefaultSpringMvcConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.*;
import java.util.EnumSet;

public abstract class WebApplicationServletInitializer implements WebApplicationInitializer {

    public static final String WEB_APPLICATION_SERVLET_NAME = WebApplicationServlet.class.getSimpleName();

    /**
     * Configurable items:<br/>
     *
     * @param servletContext the {@code ServletContext} to initialize
     * @throws ServletException if any call against the given {@code ServletContext} throws a {@code ServletException}
     * @see WebApplicationInitializer
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // create an applicationContext
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

        // register default configuration
        applicationContext.register(DefaultSpringMvcConfiguration.class);

        // register custom configuration
        applicationContext.register(this.getClass());

        // default listeners
        servletContext.addListener(new ContextLoaderListener(applicationContext));
        servletContext.addListener(new RequestContextListener());

        // default servlet
        WebApplicationServlet webApplicationServlet = new WebApplicationServlet(applicationContext);
        ServletRegistration.Dynamic servletRegistration =
                servletContext.addServlet(WEB_APPLICATION_SERVLET_NAME, webApplicationServlet);
        servletRegistration.setLoadOnStartup(0);
        servletRegistration.addMapping("/");

        // exception handle filter
        registerPriorityFilter(servletContext, new ServiceExceptionHandleFilter());

        // encoding filter
        registerPriorityFilter(servletContext, new CharacterEncodingFilter("UTF-8", true));

        // service request context filter
        registerPriorityFilter(servletContext, new ServiceRequestContextFilter());

        // do custom init
        doCustomStartup(applicationContext, servletContext);
    }

    protected void doCustomStartup(AnnotationConfigWebApplicationContext applicationContext,
                                   ServletContext servletContext) {
    }

    protected static void registerPriorityFilter(ServletContext servletContext, Filter filter) {
        servletContext
                .addFilter(filter.getClass().getSimpleName(), filter)
                .addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, WEB_APPLICATION_SERVLET_NAME);
    }

}
