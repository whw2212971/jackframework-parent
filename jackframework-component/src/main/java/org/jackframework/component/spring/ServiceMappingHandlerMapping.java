package org.jackframework.component.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class ServiceMappingHandlerMapping extends AbstractHandlerMapping implements InitializingBean {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceMappingHandlerMapping.class);

    protected static final Pattern MERGE_PATH_SEPARATOR_PATTERN = Pattern.compile("[/\\\\]+");

    protected ApplicationContext applicationContext;

    protected ServiceTypeConverterFactory typeConverterFactory;

    protected boolean detectHandlerMethodsInAncestorContexts;

    protected Map<String, ServiceMappingHandler> handlerMap = new HashMap<String, ServiceMappingHandler>();

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        ServiceMappingHandler handler = handlerMap.get(lookupPath);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up handler method for path {}", lookupPath);
            if (handler != null) {
                LOGGER.debug("Returning handler method [{}]", handler.getServiceMethod().getMethod().toGenericString());
            } else {
                LOGGER.debug("Did not find handler method for [{}]", lookupPath);
            }
        }
        return handler;
    }

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext = getApplicationContext();
        if (typeConverterFactory == null) {
            Collection<ServiceTypeConverterFactory> typeConverterFactories =
                    getApplicationContext().getBeansOfType(ServiceTypeConverterFactory.class, false, true).values();
            if (typeConverterFactories.isEmpty()) {
                typeConverterFactory = new FastjsonTypeConverterFactory();
            } else if (typeConverterFactories.size() == 1) {
                typeConverterFactory = typeConverterFactories.iterator().next();
            } else {
                List<ServiceTypeConverterFactory> list =
                        new ArrayList<ServiceTypeConverterFactory>(typeConverterFactories);
                list.sort(OrderComparator.INSTANCE);
                LOGGER.warn("Found {} matching '{}' beans, use the {}.",
                            list.size(), ServiceTypeConverterFactory.class.getName(), typeConverterFactory);
            }
        }
        initHandlers();
    }

    protected void initHandlers() {
        String[] beanNames = detectHandlerMethodsInAncestorContexts ?
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, Object.class) :
                applicationContext.getBeanNamesForType(Object.class);

        for (String beanName : beanNames) {
            Class<?> beanType = null;
            try {
                beanType = getApplicationContext().getType(beanName);
            } catch (Throwable ex) {
                // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Could not resolve target class for bean with name '{}'", beanName, ex);
                }
            }
            if (beanType != null && isHandler(beanType)) {
                detectHandlerMethods(beanName, beanType);
            }
        }
    }

    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Surface.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, ServiceMapping.class);
    }

    protected void detectHandlerMethods(final String beanName, Class<?> handlerType) {
        final Class<?> userType = ClassUtils.getUserClass(handlerType);
        final String pathPrefix = getServiceMappingPath(userType);

        Collection<ServiceMappingHandler> handlers = MethodIntrospector.selectMethods(
                userType,
                new MethodIntrospector.MetadataLookup<ServiceMappingHandler>() {
                    @Override
                    public ServiceMappingHandler inspect(Method method) {
                        try {
                            return getHandlerForMethod(beanName, userType, pathPrefix, method);
                        } catch (Throwable ex) {
                            throw new IllegalStateException(
                                    "Invalid mapping on handler class [" +
                                            userType.getName() + "]: " + method.toGenericString(), ex);
                        }
                    }
                }).values();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} request handler methods found on {}: {}", handlers.size(), userType, handlers);
        }

        for (ServiceMappingHandler handler : handlers) {
            registerHandler(handler);
        }
    }

    protected String getServiceMappingPath(AnnotatedElement element) {
        ServiceMapping serviceMapping = AnnotatedElementUtils.getMergedAnnotation(element, ServiceMapping.class);
        if (serviceMapping != null) {
            return tolerantPath(serviceMapping.value());
        }
        return "";
    }

    protected String tolerantPath(String path) {
        path = MERGE_PATH_SEPARATOR_PATTERN.matcher(path).replaceAll("/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        return path;
    }

    protected ServiceMappingHandler getHandlerForMethod(String beanName, Class<?> userType,
                                                        String pathPrefix, Method method) {
        ServiceMapping serviceMapping = AnnotatedElementUtils.findMergedAnnotation(method, ServiceMapping.class);
        if (serviceMapping != null) {
            return new ServiceMappingHandler(applicationContext, beanName, userType, method,
                                             pathPrefix + getServiceMappingPath(method), typeConverterFactory);
        }
        return null;
    }

    protected void registerHandler(ServiceMappingHandler serviceMappingHandler) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Mapped '{}' onto {}", serviceMappingHandler.getHandlerPath(), serviceMappingHandler);
        }
        ServiceMappingHandler old = handlerMap.put(serviceMappingHandler.getHandlerPath(), serviceMappingHandler);
        if (old != null) {
            throw new IllegalStateException(
                    "Invalid mapping on handler [" +
                            serviceMappingHandler.getServiceMethod().getMethod().toGenericString() + "]" +
                            ", handler path '" + serviceMappingHandler.getHandlerPath() + "' was repeated with [" +
                            old.getServiceMethod().getMethod().toGenericString() + "]");
        }
    }

    public ServiceTypeConverterFactory getTypeConverterFactory() {
        return typeConverterFactory;
    }

    public void setTypeConverterFactory(ServiceTypeConverterFactory typeConverterFactory) {
        this.typeConverterFactory = typeConverterFactory;
    }

    public boolean isDetectHandlerMethodsInAncestorContexts() {
        return detectHandlerMethodsInAncestorContexts;
    }

    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }
}
