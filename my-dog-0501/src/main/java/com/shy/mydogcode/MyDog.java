package com.shy.mydogcode;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.scanner.PackageScanner;
import com.shy.mydogcode.servlet.Servlet;
import com.shy.mydogcode.servletmapping.ServletMapping;
import com.shy.mydogcode.scanner.XmlFileScanner;
import com.shy.mydogcode.thread.ClientThread;
import com.shy.mydogcode.util.IOUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

/**
 * 主入口类
 * */
public class MyDog {
    private int port = 8088;//端口号,默认8088
    private final Config config = new Config();

    public MyDog(int port){
        this.port = port;
        initConfig();
    }

    public MyDog(){
        initConfig();
    }

    /**
     *初始化Servlet映射关系，并读取配置文件
     * */
    private void initConfig(){
        Properties mimeProp = IOUtil.loadProperties("mime.properties");
        config.setMimeProp(mimeProp);
        Properties statusProp = IOUtil.loadProperties("status.properties");
        config.setStatusProp(statusProp);
        Properties contextProp = IOUtil.loadProperties("context.properties");
        String port = contextProp.getProperty("port");
        if(port != null){
            this.port = Integer.parseInt(port);
        }
        String contextPath = contextProp.getProperty("contextPath");
        List<ServletMapping> servletMappingList = new XmlFileScanner().getServletMappingList();//来自xml文件
        List<ServletMapping> servletMappingList2 = new PackageScanner().getServletMappingList();//来自注解
        if(servletMappingList2.size() > 0){
            servletMappingList.addAll(servletMappingList2);
        }
        Map<String, Class<Servlet>> urlServletMap = config.getUrlServletMap();
        for (ServletMapping servletMapping : servletMappingList) {
            Class<Servlet> servletClass = servletMapping.getServletClass();
            String[] urlPatterns = null;
            urlPatterns = servletMapping.getUrlPatterns();//获取xml中的数组值
            for (String urlPattern : urlPatterns) {
                urlServletMap.put(urlPattern,servletClass);//存入map映射中
            }
        }
        config.setContextPath(contextPath);
        config.setPort(this.port);
    }

    /**
     * 开启服务
     * */
    public void start(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("MyDog is starting on port "+port+"...");
            while(true){
                Socket socket = serverSocket.accept();//监听浏览器socket

                ClientThread clientThread = new ClientThread(socket,config);//新建一个线程对象
                clientThread.start();//启动线程

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
