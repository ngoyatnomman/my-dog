package com.shy.mydogcode.session;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的session类
 * */
public class HttpSession implements Session{

    private final String id;//唯一的sessionID
    private final Map<String,Object> attributeMap = new HashMap<>();//域对象Map
    private final long creationTime = System.currentTimeMillis();//创建时间
    private long lastAccessedTime = System.currentTimeMillis();//最后使用时间
    private int maxInactiveInterval  = 1800;//有效期,默认为1800s，30分钟
    private boolean valid = true;

    public HttpSession(String id) {
        this.id = id;
    }

    /**
     * 验证session是否超时
     * */
    public boolean isValid(){
        if(valid){
            long activeTime = System.currentTimeMillis() - this.lastAccessedTime;//当前时间和原来的最后使用时间的差值
            valid = activeTime <= maxInactiveInterval*1000;
        }
        return valid;
    }

    /**
     * 公共方法，调用session的所有方法都要先执行
     * */
    private void validSession(){
        if(isValid()){//没有失效
            lastAccessedTime = System.currentTimeMillis();//最后访问时间改为当前时间
        }else {//session失效了
            throw new IllegalStateException("session已经失效");
        }
    }

    @Override
    public String getId() {
        validSession();
        return id;
    }

    @Override
    public void setAttribute(String key, Object value){
        validSession();
        this.attributeMap.put(key,value);
    }

    @Override
    public Object getAttribute(String key){
        validSession();
        return attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        validSession();
        this.attributeMap.remove(key);
    }

    @Override
    public long getCreationTime() {
        validSession();
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        validSession();
        return lastAccessedTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        validSession();
        return maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        validSession();
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public void invalidate() {
        valid = false;
    }
}
