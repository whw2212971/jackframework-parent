package org.jackframework.service.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.jackframework.common.CaptainTools;
import org.jackframework.common.CharsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class ExceptionHandleFilter implements Filter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandleFilter.class);

    protected String errorPage;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.errorPage = filterConfig.getInitParameter("errorPage");
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable e) {
            handleException((HttpServletRequest) request, (HttpServletResponse) response, e);
        }
    }

    protected void handleException(HttpServletRequest request,
                                   HttpServletResponse response, Throwable exception) throws ServiceServletException {
        boolean     logged = false;
        PrintWriter out    = null;
        try {
            if (exception instanceof ServiceServletException) {
                exception = ((ServiceServletException) exception).getRealCause();
            }

            ServiceException serviceException = null;
            if (exception instanceof ServiceException) {
                serviceException = (ServiceException) exception;
                LOGGER.error("Service exception", exception);
            } else {
                LOGGER.error("Internal server error{}", getRequestContent(request), exception);
            }

            logged = true;

            if (response.isCommitted()) {
                return;
            }

            String contentType = request.getContentType();
            String accept      = request.getHeader("Accept");
            if ((contentType != null && contentType.contains("json")) ||
                    (accept != null && accept.contains("json"))) {
                int    errorCode;
                String errorMessage;
                if (serviceException == null) {
                    errorCode = ServiceErrorCodes.INTERNAL_ERROR;
                    errorMessage = "Internal server error";
                } else {
                    errorCode = serviceException.getErrorCode();
                    errorMessage = serviceException.getMessage();
                }

                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("errorCode", errorCode);
                result.put("errorMessage", errorMessage);

                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=utf-8");

                JSON.writeJSONString(out = response.getWriter(), result);
            }

            if (errorPage != null) {
                response.setStatus(500);
                request.setAttribute("exception", exception);
                request.getRequestDispatcher(errorPage).forward(request, response);
                return;
            }

            throw new ServiceServletException(exception);
        } catch (ServiceServletException wrap) {
            throw wrap;
        } catch (Throwable other) {
            if (!logged) {
                LOGGER.error("Internal server error.", exception);
            }
            LOGGER.error("Handle exception error.", other);
        } finally {
            CaptainTools.close(out);
        }
    }

    protected String getRequestContent(HttpServletRequest request) {
        try {
            CharsWriter writer = new CharsWriter()
                    .append("\nRequest Headers: ").append(getRequestHeaders(request))
                    .append("\nRequest Parameters: ").append(getRequestParameters(request))
                    .append("\nSession Attributes: ").append(getSessionAttributes(request));

            String body = getRequestBody();

            if (body != null) {
                writer.append("\nRequest Body: ").write(body);
            }

            return writer.closeToString();
        } catch (Throwable e) {
            LOGGER.error("Get request content error", e);
            return null;
        }
    }

    protected String getRequestHeaders(HttpServletRequest request) {
        try {
            Enumeration<String> names = request.getHeaderNames();
            if (names == null) {
                return null;
            }
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            while (names.hasMoreElements()) {
                String              name    = names.nextElement();
                Enumeration<String> headers = request.getHeaders(name);
                if (headers == null) {
                    continue;
                }
                String header;
                if (headers.hasMoreElements()) {
                    header = headers.nextElement();
                    if (headers.hasMoreElements()) {
                        JSONArray list = new JSONArray();
                        list.add(header);
                        list.add(headers.nextElement());
                        while (headers.hasMoreElements()) {
                            list.add(headers.nextElement());
                        }
                        result.put(name, list);
                    } else {
                        result.put(name, header);
                    }
                }
            }
            return JSON.toJSONString(result);
        } catch (Throwable e) {
            LOGGER.error("Get request headers error", e);
            return null;
        }
    }

    protected String getRequestParameters(HttpServletRequest request) {
        try {
            Enumeration<String> names = request.getParameterNames();
            if (names == null) {
                return null;
            }
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            while (names.hasMoreElements()) {
                String   name   = names.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    result.put(name, values[0]);
                } else {
                    result.put(name, values);
                }
            }
            return JSON.toJSONString(result);
        } catch (Throwable e) {
            LOGGER.error("Get request parameters error", e);
            return null;
        }
    }

    protected String getRequestBody() {
        try {
            return ServiceHolder.getRequestBody();
        } catch (Throwable e) {
            LOGGER.error("Get request body error", e);
            return null;
        }
    }

    protected String getSessionAttributes(HttpServletRequest request) {
        try {
            ServiceSession      session     = ServiceHolder.getSession(false);
            HttpSession         httpSession = request.getSession(false);
            Enumeration<String> names;
            if (session == null) {
                if (httpSession == null) {
                    return null;
                }
                names = httpSession.getAttributeNames();
            } else {
                names = session.getAttributeNames();
            }
            if (names == null) {
                return null;
            }
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                result.put(name, session == null ? httpSession.getAttribute(name) : session.getAttribute(name));
            }
            return JSON.toJSONString(result);
        } catch (Throwable e) {
            LOGGER.error("Get session attributes error", e);
            return null;
        }
    }

    @Override
    public void destroy() {

    }

}
