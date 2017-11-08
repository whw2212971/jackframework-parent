package org.jackframework.service.component;

import javax.servlet.ServletException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ServiceServletException extends ServletException {

    public ServiceServletException(Throwable cause) {
        super(cause);
    }

    protected Throwable getRealCause() {
        return super.getCause();
    }

    @Override
    public String toString() {
        return getRealCause().toString();
    }

    @Override
    public String getMessage() {
        return getRealCause().getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return getRealCause().getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return getRealCause().getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return getRealCause().initCause(cause);
    }

    @Override
    public void printStackTrace() {
        getRealCause().printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        getRealCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        getRealCause().printStackTrace(s);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return getRealCause().getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        getRealCause().setStackTrace(stackTrace);
    }

    @Override
    public int hashCode() {
        return getRealCause().hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return getRealCause().equals(obj);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
