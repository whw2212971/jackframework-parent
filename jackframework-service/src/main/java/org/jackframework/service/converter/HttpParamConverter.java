package org.jackframework.service.converter;

import org.jackframework.service.core.HttpProcessContext;
import org.jackframework.service.spring.ServiceMethodHandler;

public interface HttpParamConverter {

    void init(ServiceMethodHandler handler);

    Object[] convertParam(HttpProcessContext processContext);

}
