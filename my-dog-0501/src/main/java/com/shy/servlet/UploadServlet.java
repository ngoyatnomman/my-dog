package com.shy.servlet;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servlet.Servlet;

import java.io.IOException;

@WebServlet("/upload.do")
public class UploadServlet extends Servlet {

    @Override
    public void doGet(Request request, Response response) throws IOException {

    }

    @Override
    public void doPost(Request request, Response response) throws IOException {
        System.out.println("aaa");
        System.out.println(request.getHeader("Content-Type"));
    }
}
