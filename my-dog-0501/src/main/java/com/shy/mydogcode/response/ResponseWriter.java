package com.shy.mydogcode.response;

import java.io.*;

public class ResponseWriter extends PrintWriter {

    private HttpResponse response;

    public ResponseWriter(OutputStream outputStream, HttpResponse response) {
        super(outputStream);
        this.response = response;
    }

    /**
     * 写出json响应
     * */
    @Override
    public void write(String s) {
        response.setBody(s.getBytes());
        byte[] responseBytes = ResponseHandler.generateResponseBytes(response);
        super.write(new String(responseBytes));//调用父类的写方法
    }

    @Override
    public void print(String s) {
        this.write(s);
    }
}
