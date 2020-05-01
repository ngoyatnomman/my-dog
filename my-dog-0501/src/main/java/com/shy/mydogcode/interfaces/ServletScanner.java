package com.shy.mydogcode.interfaces;

import com.shy.mydogcode.servletmapping.ServletMapping;
import java.util.List;

/**
 * 扫描公共接口
 * */
public interface ServletScanner {

    List<ServletMapping> getServletMappingList();

}
