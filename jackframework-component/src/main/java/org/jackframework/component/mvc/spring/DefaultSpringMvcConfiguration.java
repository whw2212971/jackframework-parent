package org.jackframework.component.mvc.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class DefaultSpringMvcConfiguration {

    @Bean
    public ServiceMappingHandlerMapping serviceMappingHandlerMapping() {
        ServiceMappingHandlerMapping serviceMappingHandlerMapping = new ServiceMappingHandlerMapping();
        serviceMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return serviceMappingHandlerMapping;
    }

    @Bean
    public ServiceMappingHandlerAdapter serviceMappingHandlerAdapter() {
        ServiceMappingHandlerAdapter ServiceMappingHandlerAdapter = new ServiceMappingHandlerAdapter();
        ServiceMappingHandlerAdapter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return ServiceMappingHandlerAdapter;
    }

}
