package org.jackframework.service.component;

import org.jackframework.service.converter.FastjsonTypeConverterFactory;
import org.jackframework.service.converter.ServiceTypeConverter;
import org.jackframework.service.converter.ServiceTypeConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceMethodHandlerAdapter implements HandlerAdapter, ApplicationContextAware {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceMethodHandlerAdapter.class);

    protected static final Collection<String>
            SUPPORT_METHODS = Collections.singletonList(WebContentGenerator.METHOD_POST);


    protected Map<ServiceMethodHandler, ServiceTypeConverter>
            converterCache = new ConcurrentHashMap<ServiceMethodHandler, ServiceTypeConverter>();

    protected ServiceTypeConverterFactory typeConverterFactory;

    @Override
    public boolean supports(Object handler) {
        return handler instanceof ServiceMethodHandler;
    }

    @Override
    public ModelAndView handle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ServiceMethodHandler serviceHandler = (ServiceMethodHandler) handler;
        ServiceTypeConverter typeConverter  = converterCache.get(serviceHandler);
        if (typeConverter == null) {
            synchronized (this) {
                typeConverter = converterCache.get(serviceHandler);
                if (typeConverter == null) {
                    typeConverter = typeConverterFactory.createServiceTypeConverter(serviceHandler);
                    converterCache.put(serviceHandler, typeConverter);
                }
            }
        }

        checkRequest(request);

        HttpProcessContext processContext = new HttpProcessContext();
        processContext.setRequest(request);
        processContext.setResponse(response);

        Object[] arguments;
        try {
            arguments = typeConverter.convertArguments(processContext);
        } catch (Throwable e) {
            throw new ParameterTypeConvertException(e);
        }

        Object result = serviceHandler.invoke(arguments);
        try {
            typeConverter.resolveResult(processContext, result);
        } catch (Throwable e) {
            throw new ResultTypeConvertException(e);
        }

        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1;
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Collection<ServiceTypeConverterFactory> typeConverterFactories =
                context.getBeansOfType(ServiceTypeConverterFactory.class, false, true).values();
        if (typeConverterFactories.isEmpty()) {
            typeConverterFactory = new FastjsonTypeConverterFactory();
        } else if (typeConverterFactories.size() == 1) {
            typeConverterFactory = typeConverterFactories.iterator().next();
        } else {
            typeConverterFactory = typeConverterFactories.iterator().next();
            LOGGER.warn("More than one bean of type '{}' was found, only use the first one {}.",
                    ServiceTypeConverterFactory.class.getName(), typeConverterFactory);
        }
    }

    protected void checkRequest(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        if (!WebContentGenerator.METHOD_POST.equals(method)) {
            throw new HttpRequestMethodNotSupportedException(method, SUPPORT_METHODS);
        }
    }

}
