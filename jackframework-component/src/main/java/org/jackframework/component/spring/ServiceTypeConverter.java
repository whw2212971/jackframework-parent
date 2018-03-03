package org.jackframework.component.spring;

public interface ServiceTypeConverter {

    Object[] convertArguments(HttpProcessContext processContext) throws Exception;

    void resolveResult(HttpProcessContext processContext, Object result) throws Exception;

}
