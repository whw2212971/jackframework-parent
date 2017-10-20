package org.jackframework.service.converter;

import org.jackframework.common.asm.Type;
import org.jackframework.service.core.HttpProcessContext;
import org.jackframework.service.spring.ServiceMethodHandler;

public class UrlParamConverter implements HttpParamConverter {

    protected ParamSerializer[] serializers;

    @Override
    public void init(ServiceMethodHandler handler) {
        Type[] types = (Type[]) handler.getServiceMethod().getMethod().getGenericParameterTypes();
    }

    @Override
    public Object[] convertParam(HttpProcessContext processContext) {
        return new Object[0];
    }

}
