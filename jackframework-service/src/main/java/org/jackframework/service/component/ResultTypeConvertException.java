package org.jackframework.service.component;

import org.jackframework.common.exceptions.RunningException;

public class ResultTypeConvertException extends RunningException {

    public ResultTypeConvertException() {
    }

    public ResultTypeConvertException(String message, Object... arguments) {
        super(message, arguments);
    }

    public ResultTypeConvertException(Throwable cause, String message, Object... arguments) {
        super(cause, message, arguments);
    }

    public ResultTypeConvertException(Throwable cause) {
        super(cause);
    }

}
