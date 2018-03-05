package org.jackframework.component.mvc.spring;

import org.springframework.core.Ordered;

public interface ServiceTypeConverterFactory extends Ordered {

    ServiceTypeConverter createServiceTypeConverter(ServiceMappingHandler handler);

}
