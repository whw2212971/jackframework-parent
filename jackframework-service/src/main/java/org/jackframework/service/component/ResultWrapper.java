package org.jackframework.service.component;

import org.jackframework.service.component.HttpProcessContext;

public interface ResultWrapper {

    Object wrapResult(HttpProcessContext processContext, Object result);

}
