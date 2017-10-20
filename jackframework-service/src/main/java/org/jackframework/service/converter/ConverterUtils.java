package org.jackframework.service.converter;

import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ConverterUtils {

    protected static final DefaultParameterNameDiscoverer PARAMETER_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static String[] getParameterNames(Method method) {
        return PARAMETER_DISCOVERER.getParameterNames(method);
    }

    public static String[] getParameterNames(Constructor<?> ctor) {
        return PARAMETER_DISCOVERER.getParameterNames(ctor);
    }


}
