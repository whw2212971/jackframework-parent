package org.jackframework.component.mvc.spring;

import org.jackframework.component.utils.AppConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebMvc
public class DefaultWebMvcConfiguration implements ApplicationContextAware {

    protected WebApplicationContext applicationContext;

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

    @Bean
    public DefaultWebMvcConfigurer defaultWebMvcConfigurer() {
        return new DefaultWebMvcConfigurer();
    }

    @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(AppConfig.getInt("commonsMultipartResolver.maxUploadSize", -1));
        return commonsMultipartResolver;
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix(
                AppConfig.getString("internalResourceViewResolver.prefix", "/WEB-INF/jsp/"));
        internalResourceViewResolver.setSuffix(
                AppConfig.getString("internalResourceViewResolver.suffix", ".jsp"));
        return internalResourceViewResolver;
    }

    @Bean
    public ResourceHttpRequestHandler staticResourceHttpRequestHandler() {
        ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
        resourceHttpRequestHandler.setLocations(toResourceList(
                Arrays.asList(StringUtils.commaDelimitedListToStringArray(
                        AppConfig.getString("staticResource.locations", "/static/")))));
        return resourceHttpRequestHandler;
    }

    @Bean
    public SimpleUrlHandlerMapping staticResourceHandlerMapping(
            ResourceHttpRequestHandler staticResourceHttpRequestHandler) {
        Map<String, Object> urlMap = new ManagedMap<String, Object>();
        urlMap.put(AppConfig.getString("staticResource.mapping", "/static/**"), staticResourceHttpRequestHandler);

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap(urlMap);

        return simpleUrlHandlerMapping;
    }

    protected List<Resource> toResourceList(List<String> locations) {
        List<Resource> resources = new ArrayList<Resource>();
        for (String location : locations) {
            resources.add(applicationContext.getResource(location));
        }
        return resources;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (WebApplicationContext) applicationContext;
    }

}
