package org.jackframework.common.exceptions;

import org.jackframework.common.CaptainTools;

public class RunningException extends RuntimeException {

    public RunningException() {
        super();
    }

    public RunningException(String message, Object... arguments) {
        super(CaptainTools.formatMessage(message, arguments));
    }

    public RunningException(Throwable cause, String message, Object... arguments) {
        super(CaptainTools.formatMessage(message, arguments), cause);
    }

    public RunningException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return message == null ? getClass().getName() :
                getCause() == null ? getClass().getName() + ": " + message : message;
    }

}
