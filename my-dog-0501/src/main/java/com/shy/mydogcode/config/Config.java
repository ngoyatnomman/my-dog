package com.shy.mydogcode.config;

import com.shy.mydogcode.servlet.Servlet;
import com.shy.mydogcode.session.Session;
import java.util.*;

/**
 * 全局配置文件
 * */
public final class Config {

    private String contextPath;//项目根目录
    private int port = 8088;//端口号
    private Properties mimeProp = new Properties();//后缀映射表
    private Properties statusProp = new Properties();//状态码和状态信息的映射
    private final Map<String,Class<Servlet>> urlServletMap = new HashMap<>();//servlet映射表，key url路径，value Servlet类class
    private final Map<String, Session>  sessionMap = new HashMap<>();//保存所有的session映射关系，key sessionId，value Session。
    private final Map<Class<Servlet>,Servlet> servletBeanMap = new HashMap<>();//servlet实例缓存map

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, Class<Servlet>> getUrlServletMap() {
        return urlServletMap;
    }

    public Properties getMimeProp() {
        return mimeProp;
    }

    public void setMimeProp(Properties mimeProp) {
        this.mimeProp = mimeProp;
    }

    public Map<String, Session> getSessionMap() {
        return sessionMap;
    }

    public Map<Class<Servlet>, Servlet> getServletBeanMap() {
        return servletBeanMap;
    }

    public Properties getStatusProp() {
        return statusProp;
    }

    public void setStatusProp(Properties statusProp) {
        this.statusProp = statusProp;
    }
}
