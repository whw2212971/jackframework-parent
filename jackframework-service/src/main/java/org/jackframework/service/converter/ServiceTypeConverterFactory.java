package org.jackframework.service.converter;

import org.jackframework.service.component.ServiceMethodHandler;

import java.lang.reflect.Method;

public interface ServiceTypeConverterFactory {

    ServiceTypeConverter createServiceTypeConverter(ServiceMethodHandler handler);

}
