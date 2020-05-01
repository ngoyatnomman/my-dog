package com.shy.mydogcode.servletmapping;

import com.shy.mydogcode.servlet.Servlet;

/**
 * Servlet 映射类，对应web.xml中的配置
 * */
public class ServletMapping {
    private Class<Servlet> servletClass;//servlet类
    private String[] urlPatterns;//映射到的url，可能有多个

    public ServletMapping(Class<Servlet> servletClass, String[] urlPatterns) {
        this.servletClass = servletClass;
        this.urlPatterns = urlPatterns;
    }

    public ServletMapping() {
    }

    public Class<Servlet> getServletClass() {
        return servletClass;
    }

    public void setServletClass(Class<Servlet> servletClass) {
        this.servletClass = servletClass;
    }

    public String[] getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String[] urlPatterns) {
        this.urlPatterns = urlPatterns;
    }
}
