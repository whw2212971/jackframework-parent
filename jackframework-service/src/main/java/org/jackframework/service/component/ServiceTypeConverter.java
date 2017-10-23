package org.jackframework.service.component;

import org.jackframework.service.component.HttpProcessContext;

public interface ServiceTypeConverter {

    Object[] convertArguments(HttpProcessContext processContext) throws Throwable;

    void resolveResult(HttpProcessContext processContext, Object result) throws Throwable;

}
