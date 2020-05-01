package com.shy.servlet;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.cookie.Cookie;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servlet.Servlet;

import java.io.IOException;

@WebServlet({"/demo1.do","/demo3.do"})
public class DemoServlet extends Servlet {
    @Override
    public void init() {
    }

    @Override
    public void doGet(Request request, Response response) throws IOException {
        Cookie[] cookies = request.getCookies();

        request.getRequestDispatcher("/index2.html").forward(request,response);
    }

    @Override
    public void doPost(Request request, Response response) {

    }
}
