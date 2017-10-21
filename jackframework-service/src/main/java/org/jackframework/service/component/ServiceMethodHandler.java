package org.jackframework.service.component;

import org.jackframework.common.reflect.FastMethod;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class ServiceMethodHandler {

    protected ApplicationContext applicationContext;

    protected boolean isSingleton;

    protected String beanName;

    protected Object beanObject;

    protected FastMethod serviceMethod;

    protected String handlerPath;

    public ServiceMethodHandler(
            ApplicationContext applicationContext, String beanName, Method method, String handlerPath) {
        this.applicationContext = applicationContext;
        this.isSingleton = applicationContext.isSingleton(beanName);
        this.beanName = beanName;
        if (isSingleton) {
            this.beanObject = applicationContext.getBean(beanName);
        }
        this.serviceMethod = FastMethod.getFastMethod(method);
        this.handlerPath = handlerPath;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBeanObject() {
        return isSingleton ? beanObject : applicationContext.getBean(beanName);
    }

    public FastMethod getServiceMethod() {
        return serviceMethod;
    }

    public String getHandlerPath() {
        return handlerPath;
    }

    public Object invoke(Object... args) {
        return serviceMethod.invoke(getBeanObject(), args);
    }

}
