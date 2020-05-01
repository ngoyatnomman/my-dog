package com.shy.mydogcode.request;

import com.shy.mydogcode.config.Config;
import com.shy.mydogcode.cookie.Cookie;
import com.shy.mydogcode.servletcontext.ServletContext;
import com.shy.mydogcode.session.HttpSession;
import com.shy.mydogcode.session.Session;
import com.shy.mydogcode.util.IOUtil;
import com.shy.mydogcode.util.RandomUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/***
 * 封装请求对象
 */
public class HttpRequest implements Request {
    private String url;//这个url就是请求报文中的url，可能包含参数
    private final String contextPath;//根路径
    private String servletPath;//demo3.do不包含根路径
    private String requestURI;//demo/demo3.do包含根路径
    private String requestURL;//http://localhost:8080/demo/demo3.do
    private String queryString;//地址参数
    private String method;//请求方式
    private String protocol;//协议名
    private final InputStream inputStream;//Socket输入流
    private final Map<String,String> headerMap = new HashMap<>();//请求头Map
    private final Map<String,String[]> parameterMap = new HashMap<>();//请求参数Map
    private final Map<String,Object> attributeMap = new HashMap<>();//域对象Map
    private final Config config;//全局配置类
    private Session session;//session对象
    private boolean sendSessionId;//发送sessionId响应标志位
    private String characterEncoding = "UTF8";//请求参数编码方式,默认为UTF8
    private String requestedSessionId;//来自客户端的sessionId
    private String requestHeaderStr;//请求头部分字符串
    private byte[] requestBodyBytes;//请求体部分字节数组

    public HttpRequest(InputStream inputStream, Config config){//构造函数接收输入流
        this.inputStream = inputStream;
        this.config = config;
        this.contextPath = config.getContextPath();
        this.generateRequestContent();//读取输入流，并转为请求报文
        this.parseRequestLine();//处理请求行
        this.parseHeaders();//读取headers
        generatePaths();//生成一些路径
    }

    /*==============================私有的方法===================================*/
    /**
     * 读取输入流，并转为请求报文
     * */
    private void generateRequestContent(){
        try {
            int count = 0;
            while(count == 0){//确保inputStream中内容有效
                count = inputStream.available();
            }
            byte[] httpRequestBytes =new byte[count];//根据流大小创建缓存字节数组
            int read = inputStream.read(httpRequestBytes);//内容读入字节数组
            if(read != count){
                throw new IOException("socket流有问题");
            }
            parseRequestContent(httpRequestBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将请求报文字节数组分割为请求头字符串和请求头体字节数组
     * */
    private void parseRequestContent(byte[] requestBytes){
        int index = -1;
        for (int i = 0; i < requestBytes.length; i++) {
            if(requestBytes[i] == 13 & requestBytes[i+1] == 10 & requestBytes[i+2] == 13 & requestBytes[i+3] == 10 ){//\r\n\r\n
                index = i;
                break;
            }
        }
        byte[] requestHeaderBytes = new byte[index];
        int bodyLength = requestBytes.length-index-4;
        byte[] requestBodyBytes = new byte[bodyLength];
        System.arraycopy(requestBytes,0,requestHeaderBytes,0,index);
        System.arraycopy(requestBytes,index+4,requestBodyBytes,0,bodyLength);
        this.requestHeaderStr = new String(requestHeaderBytes);
        this.requestBodyBytes = requestBodyBytes;
    }

    /**
     * 解析请求行
     * */
    private void parseRequestLine(){
        int index = requestHeaderStr.indexOf("\r\n");
        String requestLine = requestHeaderStr.substring(0,index);//报文第一行为请求头
        String[] words = requestLine.split("\\s");//按空格分割
        method = words[0];//第0个单词为请求方式
        url = words[1];//第1个单词为请求URL，可能带有参数
        protocol = words[2];//第2个单词为请求协议名
    }

    /**
     * 从原始的请求路径生成一堆路径
     * */
    private void generatePaths(){
        int index = url.indexOf('?');
        if(index == -1){
            requestURI = url;
        }else {
            requestURI = url.substring(0,index);//requestURI是?号前面的部分
            queryString = url.substring(index+1);//queryString是?号后面的参数部分
        }
        servletPath = requestURI.replaceFirst(contextPath,"");
        requestURL = getHeader("Origin:")+requestURI;
    }

    /**
     * 解析请求头
     * */
    private void parseHeaders(){
        String[] headers = requestHeaderStr.split("\r\n");//按照空格分割请求头部分
        int index = -1;
        for (int i = 1; i < headers.length; i++) {//排除第一行，因为第一行是请求状态行
            String header = headers[i];
            index = header.indexOf(":");
            String name = header.substring(0,index);
            String value = header.substring(index+1).trim();
            headerMap.put(name,value);
        }
    }

    /**
     * 分析请求报文，处理默认ContentType格式的参数
     * */
    private  void handParam(String paramStr) {
        int index = -1;//变量尽量不要在循环中声明
        String key = null;
        String value = null;
        if(paramStr == null || paramStr.length() == 0){
            return;
        }
        String[] params = paramStr.split("&");//按照&分割参数
        for (String param : params) {
            index = param.indexOf('=');//这里不用split的原因是如果=是最后一个字符，那么数组长度会为1，然后下标越界
            key = param.substring(0,index);//参数名是=号前面的部分，默认按照UTF8解码，以绝后患
            value = param.substring(index+1);//这个值可能为空。参数值是=号后面的部分
            if(parameterMap.containsKey(key)){//如果map中对应key已经存在
                String[] oldValues = parameterMap.get(key);//获取原有的数组
                String[] newValues = IOUtil.extendArray(oldValues,value);//数组长度+1，并且把最后一位置为value。
                parameterMap.put(key,newValues);
            }else {//key不存在，new一个数组直接放进去
                parameterMap.put(key,new String[]{value});
            }
        }
    }

    /**
     * 查看Cookie中的JSESSIONID,没有则返回空
     * */
    private void getSessionIdFrmCookie(){
        if(requestedSessionId==null){
            Cookie[] cookies = this.getCookies();//获取本次请求中的Cookie列表
            if(cookies == null){
                this.requestedSessionId = null;
            }else {
                for (Cookie cookie : cookies) {
                    if("JSESSIONID".equals(cookie.getName())){
                        this.requestedSessionId = cookie.getValue();
                        return;
                    }
                }
                this.requestedSessionId = null;
            }
        }
    }

    /**
     * 创建Session
     * */
    private void generateSession(boolean b){
        Map<String, Session> sessionMap = config.getSessionMap();//获取config对象中保存的sessionMap对象
        if(requestedSessionId == null){//cookie中没有sessionId
            if(b){
                do {
                    requestedSessionId = RandomUtil.generateRandomStr(40);//通过随机工具类生成40位的sessionId
                }while(sessionMap.containsKey(requestedSessionId));//如果sessionId已经存在，继续生成，这一步是为了防止sessionId重复
                session = new HttpSession(requestedSessionId);//根据sessionId创建一个Session对象
                sessionMap.put(requestedSessionId,session);//将该对象放到map中
                sendSessionId = true;//需要在响应中发送cookie
            }else {
                session = null;
                sendSessionId = false;//不需要在响应中发送cookie
            }
        }else {//cookie中有sessionId
            if(sessionMap.containsKey(requestedSessionId)){//sessionId在map中的key中
                HttpSession session = (HttpSession)sessionMap.get(requestedSessionId);//从map中取出对应的session
                if(session.isValid()){//map中的session没过期
                    this.session = sessionMap.get(requestedSessionId);//从map中取出对应的session对象
                }else {//map中的session过期了
                    if(b){//过期新建一个session
                        this.session = new HttpSession(requestedSessionId);
                        sessionMap.put(requestedSessionId, this.session);
                    }else {//过期返回null
                        this.session = null;
                    }
                }
            }else {//map的key不包含sessionId
                if(b) {
                    this.session = new HttpSession(requestedSessionId);//根据sessionId创建一个Session对象
                    sessionMap.put(requestedSessionId,this.session);//将该对象放到map中
                }else {
                    session = null;
                }
            }
            this.sendSessionId = false;//不需要在响应中发送cookie
        }
    }

    //推迟参数的处理
    private void parseParam(){
        if(this.requestBodyBytes.length > 0){
            String temp = new String(requestBodyBytes);
            if(temp.length() > 0){//说明请求体有参数
                this.handParam(temp);//空行后面是参数部分
            }
        }
        this.handParam(queryString);//?号后面是参数部分
    }

    /*==============================提供给外界的方法===================================*/
    /**
     * @return cookie数组
     * */
    @Override
    public Cookie[] getCookies(){
        String cookieString = this.getHeader("Cookie");//从headers中获取cookie头
        if(cookieString == null){
            return null;
        }
        String[] cookieStrs = cookieString.split(";");//按照分号分割
        Cookie[] cookies = new Cookie[cookieStrs.length];
        int index = -1;
        for (int i = 0; i < cookieStrs.length; i++) {
            String cookieStr = cookieStrs[i];
            index = cookieStrs[i].indexOf('=');//cookie键值对字符串按照=分割
            String cookieKey = cookieStr.substring(0,index).trim();//前后可能有空格，需要trim
            String cookieValue = cookieStr.substring(index+1).trim();//前后可能有空格，需要trim
            cookies[i] = new Cookie(cookieKey.trim(),cookieValue.trim());//存入数组
        }
        return cookies;
    }

    /**
     * 将新的requestURI转发给分发器
     * @return RequestDispatcher对象
     * */
    @Override
    public RequestDispatcher getRequestDispatcher(String servletPath) {
        return new RequestDispatcher(config,servletPath);
    }

    /**
     * 获取session
     * */
    @Override
    public Session getSession() {
        return getSession(true);
    }

    /***
     * 获取session，有一个标志位
     */
    @Override
    public Session getSession(boolean b){
        getSessionIdFrmCookie();
        generateSession(b);
        return session;
    }

    /**
     * 根据header的name值获取对应的value
     * @param name header的name值
     * */
    @Override
    public String getHeader(String name){
        return headerMap.get(name);
    }

    /*====================获取参数的方法=============================*/
    /**
     * 获取参数map
     * */
    @Override
    public Map<String, String[]> getParameterMap(){
        parseParam();
        Map<String,String[]> resultMap = new HashMap<>();
        try {
            for (Map.Entry<String,String[]> entry:parameterMap.entrySet()){//遍历map，重新解码
                String key = URLDecoder.decode(entry.getKey(),characterEncoding);
                String[] values = entry.getValue();
                for (int i = 0; i < values.length; i++) {
                    values[i] = URLDecoder.decode(values[i], characterEncoding);
                }
                resultMap.put(key,values);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 获取单个参数值
     * */
    @Override
    public String getParameter(String name){
        parseParam();
        String param = null;
        try {
            String newName = URLEncoder.encode(name,characterEncoding);//参数进行编码
            String[] params = parameterMap.get(newName);
            if(params != null){
                param = URLDecoder.decode(params[0],characterEncoding);//结果进行解码
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * @return name值迭代器
     * 获取所有参数的name值，并返回迭代器
     * */
    @Override
    public Iterator<String> getParameterNames(){
        parseParam();
        Set<String> resultSet = new HashSet<>();
        try {
            Set<String> keySet = parameterMap.keySet();
            for (String paramName : keySet) {
                resultSet.add(URLDecoder.decode(paramName,characterEncoding));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultSet.iterator();
    }

    /**
     * 获取多个参数值
     * */
    @Override
    public String[] getParameterValues(String key){
        String[] values = parameterMap.get(key);
        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = URLDecoder.decode(values[i], characterEncoding);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return values;
    }

    /*====================域对象的操作方法=============================*/
    @Override
    public void setAttribute(String key,Object value){
        this.attributeMap.put(key,value);
    }

    @Override
    public Object getAttribute(String key){
        return this.attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key){
        this.attributeMap.remove(key);
    }

    /*==============================getter和setter===================================*/

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public boolean isSendSessionId() {
        return sendSessionId;
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    @Override
    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setSendSessionId(boolean sendSessionId) {
        this.sendSessionId = sendSessionId;
    }

    public String getRequestHeaderStr() {
        return requestHeaderStr;
    }

    public void setRequestHeaderStr(String requestHeaderStr) {
        this.requestHeaderStr = requestHeaderStr;
    }

    public byte[] getRequestBodyBytes() {
        return requestBodyBytes;
    }

    public void setRequestBodyBytes(byte[] requestBodyBytes) {
        this.requestBodyBytes = requestBodyBytes;
    }

    @Override
    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    @Override
    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    @Override
    public ServletContext getServletContext() {
        return ServletContext.newInstance();
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
