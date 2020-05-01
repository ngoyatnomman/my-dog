package com.shy.mydogcode.response;

import com.shy.mydogcode.util.IOUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * 写回响应的工具类
 * */
public class ResponseHandler {

    /**
     * 生成headers字符串
     * */
    private static String generateHeaderString(Response response){
        Map<String, String> headers = response.getHeaders();
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String,String> entry:headers.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append(":");
            sb.append(value);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /**
     * 生成响应体字节数组
     * */
    public static byte[] generateResponseBytes(Response response){
        String responseText = response.getVERSION()+" "+response.getStatus()+" "+response.getMessage()+"\r\n"
                + generateHeaderString(response) + "\r\n";
        byte[] bytes = responseText.getBytes();
        if(response.getBody() != null){
            return IOUtil.copyBytes(bytes, response.getBody());
        }
        return bytes;
    }

    /**
     * 返回静态资源，可以是静态资源或者是二进制数据
     * */
    public static void writeDataSource(String url, Response response){
        try {
            String path = "static"+url;
            System.out.println(path);
            byte[] fileBytes = IOUtil.fileToByteArray(path);
            if(fileBytes == null){
                writeErrorPage(response);
            }else{
                response.setBody(fileBytes);
                byte[] responseBytes = generateResponseBytes(response);
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(responseBytes);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回默认的404页面
     * */
    public static void writeErrorPage(Response response) throws IOException {
        response.setStatus(404);
        response.setContentType("text/html");
        String s = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Error not found</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Error 404 not found</h1>\n" +
                "</body>\n" +
                "</html>";
        response.setBody(s.getBytes());
        byte[] responseBytes = generateResponseBytes(response);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(responseBytes);
        outputStream.close();
    }

}
