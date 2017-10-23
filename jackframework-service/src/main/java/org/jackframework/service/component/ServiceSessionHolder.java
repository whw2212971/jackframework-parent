package org.jackframework.service.component;

import org.jackframework.common.exceptions.WrappedRunningException;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

public class ServiceSessionHolder {

    protected static final ThreadLocal<SessionContext> SESSION_CONTEXT_LOCAL = new ThreadLocal<SessionContext>();

    protected static final Method GET_SESSION_METHOD;

    static {
        try {
            GET_SESSION_METHOD = Class.forName(
                    "org.springframework.session.web.http.ExpiringSessionHttpSession").getMethod("getSession");
            GET_SESSION_METHOD.setAccessible(true);
        } catch (Throwable e) {
            throw new WrappedRunningException(e);
        }
    }

    public static ExpiringSession getSession() {
        return getSession(true);
    }

    public static ExpiringSession getSession(boolean create) {
        return SESSION_CONTEXT_LOCAL.get().getSession(create);
    }

    protected static void setCurrentLocal(HttpServletRequest request,
                                          SessionRepository<? extends ExpiringSession> sessionRepository) {
        SESSION_CONTEXT_LOCAL.set(new SessionContext(request, sessionRepository));
    }

    protected static class SessionContext {

        protected HttpServletRequest request;

        protected SessionRepository<? extends ExpiringSession> sessionRepository;

        public SessionContext(HttpServletRequest request,
                              SessionRepository<? extends ExpiringSession> sessionRepository) {
            this.request = request;
            this.sessionRepository = sessionRepository;
        }

        public ExpiringSession getSession(boolean create) {
            HttpSession session = request.getSession(create);
            if (session == null) {
                return null;
            }
            try {
                return (ExpiringSession) GET_SESSION_METHOD.invoke(session);
            } catch (Throwable e) {
                throw new WrappedRunningException(e);
            }
        }

    }

}
