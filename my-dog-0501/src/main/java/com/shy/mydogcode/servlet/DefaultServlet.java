package com.shy.mydogcode.servlet;

import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.response.ResponseHandler;
import java.io.IOException;

/**
 * 默认的Servlet，拦截所有请求，除了jsp
* */
public class DefaultServlet extends Servlet{

    @Override
    public void doGet(Request request, Response response) throws IOException{
        String servletPath = request.getServletPath();
        int index = servletPath.lastIndexOf('.');
        String suffix = servletPath.substring(index+1).toLowerCase();//请求url的后缀，注意要转成小写
        if(suffix.length() > 0){//有后缀
            String contentType = request.getConfig().getMimeProp().getProperty(suffix);//从ContentType表中，通过后缀查找对应的ContentType
            if(contentType == null){
                ResponseHandler.writeErrorPage(response);//不存在，则返回404页面
            }else{
                response.setContentType(contentType);
                ResponseHandler.writeDataSource(servletPath,response);
            }
        }else {//url不支持
            ResponseHandler.writeErrorPage(response);
        }
    }

    @Override
    public void doPost(Request request, Response response)  throws IOException{
        this.doGet(request, response);
    }
}
