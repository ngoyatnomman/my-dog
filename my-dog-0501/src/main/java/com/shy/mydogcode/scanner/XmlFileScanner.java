package com.shy.mydogcode.scanner;

import com.shy.mydogcode.interfaces.ServletScanner;
import com.shy.mydogcode.servletmapping.ServletMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/***
 * 解析web.xml，并封装到集合中
 */
public class XmlFileScanner implements ServletScanner {
    private final List<ServletMapping> servletMappingList = new ArrayList<>();

     {
        try {
            InputStream inputStream = XmlFileScanner.class.getClassLoader().getResourceAsStream("web.xml");
            SAXReader reader = new SAXReader();
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);//这一步是跳过dtd验证带来的报错
            Document document = reader.read(inputStream);
            List<Element> servlets = document.getRootElement().elements();
            for (Element servlet  : servlets) {
                String servletClassStr = servlet.element("servlet-class").getText();
                List<Element> urlPatternList = servlet.elements("url-pattern");
                String[] urlPatterns = new String[urlPatternList.size()];
                for (int i = 0; i < urlPatternList.size(); i++) {
                    urlPatterns[i] = urlPatternList.get(i).getText();
                }
                Class servletClass = Class.forName(servletClassStr);
                servletMappingList.add(new ServletMapping(servletClass,urlPatterns));
            }
        } catch (SAXException | DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        }
     }

     @Override
    public List<ServletMapping> getServletMappingList() {
        return servletMappingList;
    }
}
