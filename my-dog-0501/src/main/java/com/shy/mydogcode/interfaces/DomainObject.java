package com.shy.mydogcode.interfaces;

/**
 * 域对象公共接口
 * */
public interface DomainObject {

    /**
     * 取值
     * @param key String类型键名
     * */
    Object getAttribute(String key);

    /**
     * 存值
     * @param key String类型键名
     * @param value Object类型键值
     * */
    void setAttribute(String key, Object value);

    /**
     * 删除值
     * @param key String类型键名
     * */
    void removeAttribute(String key);
}
