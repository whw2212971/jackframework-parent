package org.jackframework.common.exceptions;

public class WrappedRunningException extends RunningException {

    public WrappedRunningException() {
    }

    public WrappedRunningException(String message, Object... arguments) {
        super(message, arguments);
    }

    public WrappedRunningException(Throwable cause, String message, Object... arguments) {
        super(cause, message, arguments);
    }

    public WrappedRunningException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
