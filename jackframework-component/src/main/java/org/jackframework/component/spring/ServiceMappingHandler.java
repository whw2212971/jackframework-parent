package org.jackframework.component.spring;

import org.jackframework.common.CaptainTools;
import org.jackframework.common.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ServiceMappingHandler {

    protected static final DefaultParameterNameDiscoverer PARAMETER_DISCOVERER = new DefaultParameterNameDiscoverer();

    protected ApplicationContext applicationContext;

    protected boolean isSingleton;

    protected String beanName;

    protected Class<?> beanType;

    protected Object beanObject;

    protected FastMethod serviceMethod;

    protected String handlerPath;

    protected int paramCount;

    protected Class<?>[] parameterTypes;

    protected Type[] genericParameterTypes;

    protected String[] parameterNames;

    protected int[] requiredParameterIndexes;

    protected ServiceTypeConverter typeConverter;

    public ServiceMappingHandler(ApplicationContext applicationContext,
                                 String beanName, Class<?> beanType, Method method, String handlerPath,
                                 ServiceTypeConverterFactory typeConverterFactory) {
        this.applicationContext = applicationContext;
        this.isSingleton = applicationContext.isSingleton(beanName);
        this.beanName = beanName;
        this.beanType = beanType;
        if (isSingleton) {
            this.beanObject = applicationContext.getBean(beanName);
        }
        this.serviceMethod = FastMethod.getFastMethod(method);
        this.handlerPath = handlerPath;

        Class<?>[] paramTypes = method.getParameterTypes();
        int paramCount = paramTypes.length;

        this.parameterTypes = paramTypes;
        this.paramCount = paramCount;
        this.genericParameterTypes = method.getGenericParameterTypes();
        this.parameterNames = PARAMETER_DISCOVERER.getParameterNames(method);

        int[] indexes = new int[paramCount];
        int requiredCount = 0;
        for (int i = 0; i < paramCount; i++) {
            if (paramTypes[i].isPrimitive()) {
                indexes[requiredCount++] = i;
            }
        }
        this.requiredParameterIndexes = Arrays.copyOf(indexes, requiredCount);
        this.typeConverter = typeConverterFactory.createServiceTypeConverter(this);
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
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

    public int getParamCount() {
        return paramCount;
    }

    public Class<?>[] getParameterTypes() {
        if (parameterTypes == null) {
            return new Class<?>[0];
        }
        return Arrays.copyOf(parameterTypes, parameterTypes.length);
    }

    public Type[] getGenericParameterTypes() {
        if (genericParameterTypes == null) {
            return new Type[0];
        }
        return Arrays.copyOf(genericParameterTypes, genericParameterTypes.length);
    }

    public String[] getParameterNames() {
        if (parameterNames == null) {
            return new String[0];
        }
        return Arrays.copyOf(parameterNames, parameterNames.length);
    }

    public ServiceTypeConverter getTypeConverter() {
        return typeConverter;
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
                        "Optional parameter '{}' is present but cannot be translated into " +
                                "a null value due to being declared as a primitive type. " +
                                "Consider declaring it as object wrapper for the corresponding primitive type.",
                        parameterNames[index]));
            }
        }
    }

    public Object[] convertArguments(HttpProcessContext processContext) throws Exception {
        return typeConverter.convertArguments(processContext);
    }

    public void resolveResult(HttpProcessContext processContext, Object result) throws Exception {
        typeConverter.resolveResult(processContext, result);
    }

    public Object invoke(Object... args) {
        checkParameter(args);
        return serviceMethod.invoke(getBeanObject(), args);
    }

    @Override
    public String toString() {
        return CaptainTools.formatMessage(
                "{}: {}", ServiceMappingHandler.class.getSimpleName(), serviceMethod.getMethod().toGenericString());
    }

}
