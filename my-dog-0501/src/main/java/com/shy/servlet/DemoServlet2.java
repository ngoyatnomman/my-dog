package com.shy.servlet;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servlet.Servlet;
import com.shy.mydogcode.session.Session;

import java.io.IOException;

@WebServlet("/demo2.do")
public class DemoServlet2 extends Servlet {
    @Override
    public void doGet(Request request, Response response) throws IOException {
        Session session = request.getSession(false);
        System.out.println(session.getId());
//        session.invalidate();
        System.out.println(session.getId());
        request.getRequestDispatcher("/index2.html").forward(request,response);
    }

    @Override
    public void doPost(Request request, Response response) {

    }
}
