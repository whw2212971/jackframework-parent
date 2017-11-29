package org.jackframework.service.component;

import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.Publish;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServiceAnnotationHandlerMapping extends AbstractUrlHandlerMapping implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        for (String beanName : context.getBeanNamesForAnnotation(EndService.class)) {
            Class<?> handlerType    = ClassUtils.getUserClass(context.getType(beanName));
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
        final Map<String, Method> existsMethods = new HashMap<String, Method>();
        ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) {
                Publish methodPublishApi = AnnotationUtils.findAnnotation(method, Publish.class);
                if (methodPublishApi != null) {
                    String methodName   = method.getName();
                    Method existsMethod = existsMethods.get(methodName);
                    if (existsMethod != null &&
                            parameterTypeEquals(existsMethod.getParameterTypes(), method.getParameterTypes())) {
                        return;
                    }
                    // typeUrlPath + methodUrlPath
                    String handlerPath = getPathMatcher().combine(typeUrlPath, getUrlPath(methodPublishApi));
                    // create handler
                    ServiceMethodHandler handler = new ServiceMethodHandler(context, beanName, method, handlerPath);
                    // register handler
                    registerHandler(handlerPath, handler);

                    existsMethods.put(method.getName(), method);
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
    }

    protected static boolean parameterTypeEquals(Class<?>[] params1, Class<?>[] params2) {
        int length = params1.length;
        if (length != params2.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (params1[i].equals(params2[i])) {
                continue;
            }
            return false;
        }
        return true;
    }

    protected static String getUrlPath(Publish typePublishApi) {
        String urlPath = typePublishApi.value();
        if (!urlPath.startsWith("/")) {
            return "/" + urlPath;
        }
        return urlPath;
    }

}
