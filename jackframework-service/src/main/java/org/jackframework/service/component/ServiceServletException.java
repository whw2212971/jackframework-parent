package org.jackframework.service.component;

import javax.servlet.ServletException;
import java.io.PrintStream;
import java.io.PrintWriter;

class ServiceServletException extends ServletException {

    public ServiceServletException(Throwable cause) {
        super(cause);
    }

    public Throwable getRealCause() {
        return super.getCause();
    }

    @Override
    public String getMessage() {
        return super.getCause().getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return super.getCause().getLocalizedMessage();
    }

    @Override
    public String toString() {
        return super.getCause().toString();
    }

    @Override
    public void printStackTrace() {
        super.getCause().printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.getCause().printStackTrace(s);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getCause().getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.getCause().setStackTrace(stackTrace);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause().getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return super.getCause().initCause(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
