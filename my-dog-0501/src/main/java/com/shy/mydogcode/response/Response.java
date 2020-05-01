package com.shy.mydogcode.response;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.cookie.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 封装响应对象
 */
public interface Response {
        /*==============================提供给外界的方法===================================*/
    /**
     * 添加header
     * @param key header名
     * @param value header 值
     * */
    void addHeader(String key, String value);

    /**
     * 设置contentType
     * @param contentType 字符串
     * */
    void setContentType(String contentType) ;

    /**
     * 设置编码格式
     * @param encoding 编码格式字符串
     * */
    void setCharacterEncoding(String encoding);

    /**
     * headers中添加cookie
     * @param cookie cookie对象
     * */
    void addCookie(Cookie cookie);

    /**
    * 按照不同的ContentType写出去,注意header的传递
    * */
    PrintWriter getWriter();

    /**
     * 重定向
     * @param url 重定向目标url
     */
    void sendRedirect(String url) throws IOException ;

    /**
     * 设置状态，内部查找并设置状态描述
     * */
    void setStatus(int status) ;

    /*==============================getter和setter===================================*/
    OutputStream getOutputStream() ;

    Config getConfig();

    Map<String, String> getHeaders();

    String getVERSION();

    int getStatus();

    String getMessage();

    void setMessage(String message);

    byte[] getBody();

    void setBody(byte[] bodyBytes);
}
