package org.jackframework.service.component;

import org.jackframework.common.exceptions.WrappedRunningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServiceSessionHolder {

    protected static final ThreadLocal<SessionLocal> SESSION_CONTEXT_LOCAL = new ThreadLocal<SessionLocal>();

    public static ServiceSession getSession() {
        return getSession(true);
    }

    public static ServiceSession getSession(boolean create) {
        return SESSION_CONTEXT_LOCAL.get().getSession(create);
    }

    protected static void setCurrentLocal(HttpServletRequest request) {
        SESSION_CONTEXT_LOCAL.set(new SessionLocal(request));
    }

    protected static class SessionLocal {

        protected HttpServletRequest request;

        protected ServiceSession currentSession;

        public SessionLocal(HttpServletRequest request) {
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
                return currentSession = new ServiceSession(session);
            } catch (Throwable e) {
                throw new WrappedRunningException(e);
            }
        }

    }

}
