package org.jackframework.service.component;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ServiceMethodHandler {

    protected static final DefaultParameterNameDiscoverer PARAMETER_DISCOVERER = new DefaultParameterNameDiscoverer();

    protected ApplicationContext applicationContext;

    protected boolean isSingleton;

    protected String beanName;

    protected Object beanObject;

    protected FastMethod serviceMethod;

    protected String handlerPath;

    protected int paramCount;

    protected Class<?>[] parameterTypes;

    protected Type[] genericParameterTypes;

    protected String[] parameterNames;

    protected int[] requiredParameterIndexes;

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

        Class<?>[] paramTypes = method.getParameterTypes();
        int        paramCount = paramTypes.length;

        this.parameterTypes = paramTypes;
        this.paramCount = paramCount;
        this.genericParameterTypes = method.getGenericParameterTypes();
        this.parameterNames = PARAMETER_DISCOVERER.getParameterNames(method);

        int[] indexes       = new int[paramCount];
        int   requiredCount = 0;
        for (int i = 0; i < paramCount; i++) {
            if (paramTypes[i].isPrimitive()) {
                indexes[requiredCount++] = i;
            }
        }
        this.requiredParameterIndexes = Arrays.copyOf(indexes, requiredCount);
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBeanObject() {
        return beanObject;
    }

    public FastMethod getServiceMethod() {
        return serviceMethod;
    }

    public String getHandlerPath() {
        return handlerPath;
    }

    public int getParamCount() {
        return paramCount;
    }

    public Class<?>[] getParameterTypes() {
        return Arrays.copyOf(parameterTypes, parameterTypes.length);
    }

    public Type[] getGenericParameterTypes() {
        return Arrays.copyOf(genericParameterTypes, genericParameterTypes.length);
    }

    public String[] getParameterNames() {
        return Arrays.copyOf(parameterNames, parameterNames.length);
    }

    public void checkParameter(Object... args) {
        if (args == null) {
            throw new IllegalArgumentException("Arguments can not be null.");
        }
        if (args.length != paramCount) {
            throw new IllegalArgumentException(CaptainTools.formatMessage(
                    "Arguments length does not match, expect {}, actual {}.", paramCount, args.length));
        }
        for (int index : requiredParameterIndexes) {
            if (args[index] == null) {
                throw new IllegalStateException(CaptainTools.formatMessage(
                        "Optional int parameter '{}' is present but cannot be translated into a null value due to being declared as a primitive type. Consider declaring it as object wrapper for the corresponding primitive type.",
                        parameterNames[index]));
            }
        }
    }

    public Object invoke(Object... args) {
        checkParameter(args);
        return serviceMethod.invoke(getBeanObject(), args);
    }

    @Override
    public String toString() {
        return CaptainTools.formatMessage("{}: {}",
                ServiceMethodHandler.class.getSimpleName(), serviceMethod.getMethod().toGenericString());
    }

}
