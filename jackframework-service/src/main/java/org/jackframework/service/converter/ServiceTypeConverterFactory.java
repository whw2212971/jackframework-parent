package org.jackframework.service.converter;

import java.lang.reflect.Method;

public interface ServiceTypeConverterFactory {

    ServiceTypeConverter createServiceTypeConverter(Method handlerMethod);

}
