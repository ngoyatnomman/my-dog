package com.shy.mydogcode.session;

import com.shy.mydogcode.interfaces.DomainObject;

/**
 * session接口
 * */
public interface Session extends DomainObject {

    String getId();

    long getCreationTime() ;

    long getLastAccessedTime() ;

    String toString();

    int getMaxInactiveInterval();

    void setMaxInactiveInterval(int maxInactiveInterval);

    void invalidate();

}
