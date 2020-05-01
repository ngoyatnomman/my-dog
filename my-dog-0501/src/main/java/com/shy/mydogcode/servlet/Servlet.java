package com.shy.mydogcode.servlet;

import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servletcontext.ServletContext;

import java.io.IOException;

/**
 * servlet API
* */
public abstract class Servlet {

    public void init(){

    }

    public void doGet(Request request, Response response) throws IOException {

    }

    public void doPost(Request request, Response response)  throws IOException {

    }

    public void service(Request request, Response response)  throws IOException{//根据不同的方式调用不同的方法
        if(request.getMethod().equalsIgnoreCase("POST")){
            doPost(request, response);
        }else if(request.getMethod().equalsIgnoreCase("GET")){
            doGet(request,response);
        }
    }

    public ServletContext getServletContext(){
        return ServletContext.newInstance();//返回单例的application对象
    }
}
