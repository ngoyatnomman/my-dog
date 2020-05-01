package com.shy.mydogcode.thread;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.request.HttpRequest;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.request.RequestDispatcher;
import com.shy.mydogcode.response.HttpResponse;
import com.shy.mydogcode.response.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 线程类：处理浏览器请求
 * */
public class ClientThread extends Thread {
    private Socket socket;
    private final Config config;

    public ClientThread(Socket socket, Config config) {
        this.socket = socket;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            Request request = new HttpRequest(inputStream,config);
            Response response = new HttpResponse(outputStream,config,request);

            String servletPath = request.getServletPath();
            RequestDispatcher dispatcher = request.getRequestDispatcher(servletPath);
            dispatcher.forward(request,response);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
