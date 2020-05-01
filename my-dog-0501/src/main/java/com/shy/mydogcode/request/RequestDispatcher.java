package com.shy.mydogcode.request;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.cookie.Cookie;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.response.ResponseHandler;
import com.shy.mydogcode.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

/**
 * 请求分发器
 * */
public class RequestDispatcher{

    private String servletPath;
    private final Config config;

    public RequestDispatcher(Config config,String servletPath) {
        servletPath = servletPath.startsWith("/") ? servletPath:"/"+servletPath;//相对路径转绝对路径
        this.servletPath = servletPath;
        this.config = config;
    }

    /**
    * 携带请求进行转发操作
    * @param request 请求对象
     * @param response 响应对象
    * */
    public void forward(Request request, Response response) throws IOException {
        if(request.isSendSessionId()){
            response.addCookie(new Cookie("JSESSIONID",request.getRequestedSessionId()));
        }
        if(servletPath != null){
            Map<String, Class<Servlet>> urlServletMap = config.getUrlServletMap();
            Class<Servlet> servletClass = urlServletMap.get(servletPath);
            if(servletClass == null){
                servletClass = urlServletMap.get("/");//交给默认的servlet处理，一般是静态页面的访问
            }
            request.setServletPath(servletPath);//修改request对象的ServletPath属性
            runServlet(servletClass,request,response);//调用servlet
        }else {
            ResponseHandler.writeErrorPage(response);
        }
    }

    /**
     * 调用servlet
     * @param servletClass servletClass对象
     * @param request 请求对象
     * @param  response 响应对象
     * */
    private void runServlet(Class<Servlet> servletClass, Request request, Response response){
        try {
            Map<Class<Servlet>, Servlet> servletBeanMap = config.getServletBeanMap();//获取缓存map
            Servlet servlet;
            if(servletBeanMap.containsKey(servletClass)) {
                servlet = servletBeanMap.get(servletClass);//从缓存中取出servlet实例
            }else {//是第一次加载
                servlet = servletClass.getDeclaredConstructor().newInstance();//实例化servlet。
                servletBeanMap.put(servletClass,servlet);//放入缓存中。
                servlet.init();//只有在第一次加载的时候执行init方法。
            }
            servlet.service(request,response);//执行service方法。
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public Config getConfig() {
        return config;
    }
}
