package com.shy.servlet;

import com.shy.mydogcode.annotation.WebServlet;
import com.shy.mydogcode.request.Request;
import com.shy.mydogcode.response.Response;
import com.shy.mydogcode.servlet.Servlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/demo8.do")
public class DemoServlet3  extends Servlet {
    @Override
    public void doGet(Request request, Response response) throws IOException {

    }

    @Override
    public void doPost(Request request, Response response) throws IOException {
        request.setCharacterEncoding("UTF8");
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String,String[]> entry:parameterMap.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(Arrays.toString(entry.getValue()));
        }
        response.sendRedirect("/static.html");
    }
}
