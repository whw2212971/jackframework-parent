package org.jackframework.component.spring;

import org.jackframework.common.exceptions.RunningException;

public class ServiceException extends RunningException {

    protected int errorCode;

    public ServiceException(int errorCode, String message, Object... arguments) {
        super(message, arguments);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, Throwable cause, String message, Object... arguments) {
        super(cause, message, arguments);
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return message == null ?
                getClass().getName() + ": " + getErrorCode() :
                getClass().getName() + ": " + getErrorCode() + ", " + message;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
