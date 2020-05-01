package com.shy.mydogcode.util;

import java.io.*;
import java.util.Properties;

/**
 * IO操作工具类
 * */
public class IOUtil {

    public static void readFile(String path){
        InputStream inputStream = IOUtil.class.getClassLoader().getResourceAsStream(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine() )!= null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] fileToByteArray(String path) throws IOException {
        InputStream inputStream = IOUtil.class.getClassLoader().getResourceAsStream(path);
        if(inputStream == null){
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while ((n = inputStream.read(buffer))!=-1){
            output.write(buffer,0,n);
        }
        return output.toByteArray();
    }

    public static byte[] copyBytes(byte[] headerBytes,byte[] bodyBytes){
        byte[] responseByte = new byte[bodyBytes.length+headerBytes.length];
        int headerLength = headerBytes.length;
        System.arraycopy(headerBytes,0,responseByte,0,headerLength);
        System.arraycopy(bodyBytes,0,responseByte,headerLength,bodyBytes.length);
        return responseByte;
    }

    public static Properties loadProperties(String path){
        Properties properties = new Properties();
        try {
            InputStream inputStream = IOUtil.class.getClassLoader().getResourceAsStream(path);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * 数组扩容方法
     * */
    public static String[] extendArray(String[] oldValues, String value) {
        String[] newValues = new String[oldValues.length+1];
        System.arraycopy(oldValues,0,newValues,0,oldValues.length);
        newValues[oldValues.length] = value;
        return newValues;
    }
}
