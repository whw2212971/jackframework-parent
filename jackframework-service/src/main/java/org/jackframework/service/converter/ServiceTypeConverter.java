package org.jackframework.service.converter;

import org.jackframework.service.component.HttpProcessContext;

public interface ServiceTypeConverter {

    Object[] convertArguments(HttpProcessContext processContext) throws Throwable;

    void resolveResult(HttpProcessContext processContext, Object result) throws Throwable;

}
