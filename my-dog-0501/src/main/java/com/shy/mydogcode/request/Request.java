package com.shy.mydogcode.request;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.cookie.Cookie;
import com.shy.mydogcode.interfaces.DomainObject;
import com.shy.mydogcode.servletcontext.ServletContext;
import com.shy.mydogcode.session.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/***
 * 封装请求对象
 */
public interface Request extends DomainObject {

    /**
     * @return cookie数组
     * */
    Cookie[] getCookies();

    /**
     * 根据header的name值获取对应的value
     * @param name header的name值
     * */
    String getHeader(String name);

    String getMethod();

    String getContextPath();

    String getRequestedSessionId();

    String getRequestURI();

    String getServletPath();

    /***
     * 获取session，有一个标志位
     */
    Session getSession(boolean b);

    /**
     * 获取session
     * */
    Session getSession();

    String getCharacterEncoding();

    void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException;

    InputStream getInputStream() throws IOException;

    /**
     * 获取单个参数值
     * */
    String getParameter(String name);

    /**
     * @return name值迭代器
     * 获取所有参数的name值，并返回迭代器
     * */
    Iterator<String> getParameterNames();

    /**
     * 获取多个参数值
     * */
    String[] getParameterValues(String key);

    /**
     * 获取参数map
     * */
    Map<String, String[]> getParameterMap();

    /**
     * 将新的url转发给分发器
     * @return RequestDispatcher对象
     * */
    RequestDispatcher getRequestDispatcher(String url);

    ServletContext getServletContext();




    boolean isSendSessionId();

    Map<String, String> getHeaderMap();

    Map<String, Object> getAttributeMap();

    Config getConfig();

    void setRequestedSessionId(String requestedSessionId);

    void setServletPath(String servletPath);

    String getUrl();

    void setUrl(String url);

    void setSession(Session session);

    void setSendSessionId(boolean sendSessionId);

    String getRequestHeaderStr();

    void setRequestHeaderStr(String requestHeaderStr);

    byte[] getRequestBodyBytes();

    void setRequestBodyBytes(byte[] requestBodyBytes);

    void setRequestURI(String requestURI);

    String getRequestURL();

    void setRequestURL(String requestURL);


}
