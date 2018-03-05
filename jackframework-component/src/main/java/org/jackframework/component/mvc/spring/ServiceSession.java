package org.jackframework.component.mvc.spring;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class ServiceSession {

    protected HttpSession session;

    protected ServiceHolder.RequestLocal requestLocal;

    public ServiceSession(HttpSession session, ServiceHolder.RequestLocal requestLocal) {
        this.session = session;
        this.requestLocal = requestLocal;
    }

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public String getId() {
        return session.getId();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    public void invalidate() {
        session.invalidate();
        requestLocal.removeCurrentSession();
    }

    public boolean isNew() {
        return session.isNew();
    }

}
