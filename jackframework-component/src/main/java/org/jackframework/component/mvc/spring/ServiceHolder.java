package org.jackframework.component.mvc.spring;

import org.jackframework.common.exceptions.RunningException;
import org.jackframework.common.exceptions.WrappedException;
import org.jackframework.component.mvc.javaee.ServiceRequestContextFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServiceHolder {

    protected static final ThreadLocal<RequestLocal> REQUEST_LOCAL       = new ThreadLocal<RequestLocal>();
    protected static final ThreadLocal<String>       REQUEST_BODY_LOCAL  = new ThreadLocal<String>();
    protected static final ThreadLocal<String>       REQUEST_ROUTE_LOCAL = new ThreadLocal<String>();

    public static ServiceSession getSession() {
        return getSession(true);
    }

    public static ServiceSession getSession(boolean create) {
        RequestLocal requestLocal = REQUEST_LOCAL.get();
        if (requestLocal == null) {
            if (create) {
                throw new RunningException("Can't work without filter '{}'.", ServiceRequestContextFilter.class.getName());
            }
            return null;
        }
        return requestLocal.getSession(create);
    }

    public static String getRequestBody() {
        return REQUEST_BODY_LOCAL.get();
    }

    public static String getRequestRoute() {
        return REQUEST_ROUTE_LOCAL.get();
    }

    protected static void setRequestLocal(HttpServletRequest request) {
        REQUEST_LOCAL.set(new RequestLocal(request));
    }

    protected static void setRequestBody(String requestBody) {
        REQUEST_BODY_LOCAL.set(requestBody);
    }

    protected static void setRequestRoute(String requestRoute) {
        REQUEST_ROUTE_LOCAL.set(requestRoute);
    }

    protected static class RequestLocal {

        protected HttpServletRequest request;

        protected ServiceSession currentSession;

        public RequestLocal(HttpServletRequest request) {
            this.request = request;
        }

        public ServiceSession getSession(boolean create) {
            if (currentSession != null) {
                return currentSession;
            }
            HttpSession session = request.getSession(create);
            if (session == null) {
                return null;
            }
            try {
                return currentSession = new ServiceSession(session, this);
            } catch (Throwable e) {
                throw new WrappedException(e);
            }
        }

        public void removeCurrentSession() {
            currentSession = null;
        }

    }

}
