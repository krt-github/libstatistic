package com.quncao.core.statistics;

import java.security.MessageDigest;

/**
 * Created by zehong.tang on 2016/12/6.
 * 百灵鸟签名工具，与后台统一
 */

class LarkSignUtils {
    private static final String APP_SECRET_KEY = "quanyan2016";

    static String getSign(String source) {
        if(null == source || source.length() == 0)
            return "";

        return MD5(source + APP_SECRET_KEY);
    }

    private static String MD5(String source) {
        if (null == source || source.length() == 0) {
            return "";
        }

        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for(byte b : digest.digest()){
                stringBuilder.append(String.format("%02x", b & 0xFF));
            }
            return stringBuilder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
