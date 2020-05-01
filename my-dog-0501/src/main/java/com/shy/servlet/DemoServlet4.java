package com.shy.servlet;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servlet.Servlet;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/demo4.do")
public class DemoServlet4 extends Servlet {
    @Override
    public void doGet(Request request, Response response) throws IOException {
        String json2 = "{ \"name\":\"zhangsan\",\"age\":\"12\" }";
        response.setContentType("application/json");
        PrintWriter pw = response.getWriter();
        pw.print(json2);
        pw.flush();
        pw.close();
    }

    @Override
    public void doPost(Request request, Response response) throws IOException {

    }
}
