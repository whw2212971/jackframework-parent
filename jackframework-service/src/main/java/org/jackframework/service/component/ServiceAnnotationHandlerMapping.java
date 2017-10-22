package org.jackframework.service.component;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.Publish;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import java.lang.reflect.Method;

public class ServiceAnnotationHandlerMapping extends AbstractUrlHandlerMapping implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        for (String beanName : context.getBeanNamesForAnnotation(EndService.class)) {
            Class<?> handlerType    = context.getType(beanName);
            Publish  typePublishApi = context.findAnnotationOnBean(beanName, Publish.class);
            String   typeUrlPath    = "/";
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
                Publish methodPublishApi = AnnotationUtils.findAnnotation(method, Publish.class);
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

    protected static String getUrlPath(Publish typePublishApi) {
        String urlPath = typePublishApi.value();
        if (!urlPath.startsWith("/")) {
            return "/" + urlPath;
        }
        return urlPath;
    }

}
