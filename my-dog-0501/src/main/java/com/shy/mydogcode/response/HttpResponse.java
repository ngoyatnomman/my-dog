package com.shy.mydogcode.response;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.cookie.Cookie;
import com.shy.mydogcode.request.Request;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装响应对象
 */
public class HttpResponse implements Response{
    private final OutputStream outputStream;//输出流
    private final Config config;
    private final String VERSION = "HTTP/1.1";
    private int status = 200;
    private String message = "OK";
    private final Map<String,String> headers = new HashMap<>();
    private byte[] body;
    private Request request;

    /**
     * 构造函数接收输出流
     * @param outputStream socket输出流
     * @param config 全局配置对象
     * */
    public HttpResponse(OutputStream outputStream, Config config, Request request){
        this.outputStream = outputStream;
        this.config = config;
        this.headers.put("content-type","text/html;charset=UTF8");
        this.request = request;
    }

    /*==============================提供给外界的方法===================================*/
    /**
     * 添加header
     * @param key header名
     * @param value header 值
     * */
    @Override
    public void addHeader(String key,String value){
        headers.put(key,value);
    }

    /**
     * 设置contentType
     * @param contentType 字符串
     * */
    @Override
    public void setContentType(String contentType) {
        this.addHeader("content-type",contentType);
    }

    /**
     * 设置编码格式
     * @param encoding 编码格式字符串
     * */
    @Override
    public void setCharacterEncoding(String encoding){
        String contentType = headers.get("content-type");
        int index = contentType.indexOf(";");
        if(index == -1){
            contentType += ";charset="+encoding;
        }else {
            contentType = contentType.substring(0,index)+"charset="+encoding;
        }
        headers.put("content-type",contentType);
    }

    /**
     * headers中添加cookie
     * @param cookie cookie对象
     * */
    @Override
    public void addCookie(Cookie cookie){
        String cookieStr = cookie.getName()+"="+cookie.getValue();
        if(cookie.getPath() != null){
            cookieStr += ";Path="+cookie.getPath();
        }
        if(cookie.getMaxAge() != 0){
            cookieStr += ";Max-Age="+cookie.getMaxAge();
        }
        if(cookie.getDomain() != null){
            cookieStr += ";Domain="+cookie.getDomain();
        }
        this.headers.put("set-cookie",cookieStr);
    }

    /**
    * 按照不同的ContentType写出去,注意header的传递
    * */
    @Override
    public PrintWriter getWriter(){
        return new ResponseWriter(outputStream,this);//实际返回的是ResponseWriter类
    }

    /**
     * 重定向
     * @param url 重定向目标url
     */
    @Override
    public void sendRedirect(String url) throws IOException {
        if(!url.startsWith("/")){//相对路径
            url = config.getContextPath()+"/"+url;
            url = url.replaceFirst("//","");
        }
        this.setStatus(302);
        this.addHeader("Location",url);
        if(this.request.isSendSessionId()){
            this.addCookie(new Cookie("JSESSIONID",request.getSession().getId()));
        }
        byte[] responseBytes = ResponseHandler.generateResponseBytes(this);
        outputStream.write(responseBytes);
        outputStream.close();
    }

    /**
     * 设置状态，内部查找并设置状态描述
     * */
    @Override
    public void setStatus(int status) {
        this.status = status;
        this.message = config.getStatusProp().getProperty(String.valueOf(status));//从状态表中查找状态描述
    }

    /*==============================getter和setter===================================*/
    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getVERSION() {
        return VERSION;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "outputStream=" + outputStream +
                ", config=" + config +
                ", VERSION='" + VERSION + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
