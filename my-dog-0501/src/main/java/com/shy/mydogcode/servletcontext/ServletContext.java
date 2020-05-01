package com.shy.mydogcode.servletcontext;

import com.shy.mydogcode.interfaces.DomainObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 就是application域对象
 * */
public class ServletContext implements DomainObject {

    private static ServletContext servletContext = new ServletContext();
    private final Map<String,Object> attributeMap = new HashMap<>();//域对象Map

    private ServletContext() {
    }

    @Override
    public void setAttribute(String key, Object value){
        this.attributeMap.put(key,value);
    }

    @Override
    public Object getAttribute(String key){
        return attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        this.attributeMap.remove(key);
    }

    public static ServletContext newInstance(){
        return servletContext;
    }

    @Override
    public String toString() {
        return "ServletContext{" +
                "attributeMap=" + attributeMap +
                '}';
    }
}
