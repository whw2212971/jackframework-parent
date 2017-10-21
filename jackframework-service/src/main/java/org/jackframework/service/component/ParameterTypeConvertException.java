package org.jackframework.service.component;

import org.jackframework.common.exceptions.RunningException;

public class ParameterTypeConvertException extends RunningException {

    public ParameterTypeConvertException() {
    }

    public ParameterTypeConvertException(String message, Object... arguments) {
        super(message, arguments);
    }

    public ParameterTypeConvertException(Throwable cause, String message, Object... arguments) {
        super(cause, message, arguments);
    }

    public ParameterTypeConvertException(Throwable cause) {
        super(cause);
    }

}
