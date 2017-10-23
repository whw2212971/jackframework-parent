package org.jackframework.service.component;

public interface ServiceTypeConverter {

    Object[] convertArguments(HttpProcessContext processContext) throws Exception;

    void resolveResult(HttpProcessContext processContext, Object result) throws Exception;

}
