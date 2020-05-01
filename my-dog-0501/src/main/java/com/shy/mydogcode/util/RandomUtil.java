package com.shy.mydogcode.util;

import java.util.Random;

/**
 * 随机数工具类
 * */
public class RandomUtil {

    /**
     * 生成指定长度的随机字符串，ascii范围[A,z)
     * */
    public static String generateRandomStr(int length){
        Random random = new Random();
        int temp = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            temp = random.nextInt(58)+65;//生成[65,123)范围的随机int值
            sb.append((char)temp);//按照ascii码转换为char，并写入缓存
        }
        return sb.toString();
    }
}
