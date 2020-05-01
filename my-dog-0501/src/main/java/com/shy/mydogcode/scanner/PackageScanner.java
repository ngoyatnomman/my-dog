package com.shy.mydogcode.scanner;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.interfaces.ServletScanner;
import com.shy.mydogcode.servlet.Servlet;
import com.shy.mydogcode.servletmapping.ServletMapping;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描classPath下所有类，并写入集合
 * */
public class PackageScanner implements ServletScanner {

    private final File baseFile = new File(PackageScanner.class.getClassLoader().getResource("").getFile());
    private  final List<ServletMapping> servletMappingList = new ArrayList<>();

    public PackageScanner() {
        try {
            this.doScan(baseFile);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void doScan(File baseFile) throws ClassNotFoundException {
        for (File listFile : baseFile.listFiles()) {
            if(listFile.isDirectory()){
                doScan(listFile);
            }else if(listFile.isFile()){
                String absolutePath = listFile.getAbsolutePath();
                if(absolutePath.endsWith(".class")){
                    int classPathLength = this.baseFile.getAbsolutePath().length();
                    int absolutePathLength = absolutePath.length();
                    String className = absolutePath.substring(classPathLength + 1,absolutePathLength-6).replace("\\",".");
                    Class clazz = Class.forName(className);
                    if(Servlet.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(WebServlet.class)){
                        WebServlet annotation = (WebServlet) clazz.getAnnotation(WebServlet.class);
                        ServletMapping servletMapping = new ServletMapping(clazz,annotation.value());
                        this.servletMappingList.add(servletMapping);
                    }
                }
            }
        }
    }

    @Override
    public List<ServletMapping> getServletMappingList() {
        return servletMappingList;
    }
}
