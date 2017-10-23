package org.jackframework.service.component;

public interface ServiceTypeConverterFactory {

    ServiceTypeConverter createServiceTypeConverter(ServiceMethodHandler handler);

}
