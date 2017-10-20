package org.jackframework.service.spring;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.PublishApi;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import java.lang.reflect.Method;

public class ServiceAnnotationHandlerMapping extends AbstractUrlHandlerMapping {

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);

        for (String beanName : context.getBeanNamesForAnnotation(EndService.class)) {
            Class<?>   handlerType    = context.getType(beanName);
            PublishApi typePublishApi = context.findAnnotationOnBean(beanName, PublishApi.class);
            String     typeUrlPath    = "/";

            if (typePublishApi != null) {
                typeUrlPath = getUrlPath(typePublishApi);
            }

            registerHandlers(context, beanName, handlerType, typeUrlPath);
        }
    }

    protected void registerHandlers(
            final ApplicationContext context, final String beanName, Class<?> handlerType, final String typeUrlPath) {
        ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) {
                PublishApi methodPublishApi = AnnotationUtils.findAnnotation(method, PublishApi.class);
                if (methodPublishApi != null) {
                    // typeUrlPath + methodUrlPath
                    String handlerPath = getPathMatcher().combine(typeUrlPath, getUrlPath(methodPublishApi));
                    // create handler
                    ServiceMethodHandler handler = new ServiceMethodHandler(context, beanName, method, handlerPath);
                    // register handler
                    registerHandler(handlerPath, handler);
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
    }

    protected static String getUrlPath(PublishApi typePublishApi) {
        String urlPath = typePublishApi.value();
        if (!urlPath.startsWith("/")) {
            return "/" + urlPath;
        }
        return urlPath;
    }

}
