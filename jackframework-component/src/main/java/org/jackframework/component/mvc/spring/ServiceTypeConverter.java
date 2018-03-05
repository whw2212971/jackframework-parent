package org.jackframework.component.mvc.spring;

public interface ServiceTypeConverter {

    Object[] convertArguments(ServiceProcessContext processContext) throws Exception;

    void resolveResult(ServiceProcessContext processContext, Object result) throws Exception;

}
