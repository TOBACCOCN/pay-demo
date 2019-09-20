package com.pay.example.util;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Jasypt 配置文件属性加解密工具类
 *
 * @author zhangyonghong
 * @date 2019.6.14
 */
public class JasyptUtil {

    public static String encrypt(String content, String salt) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(salt);
        return textEncryptor.encrypt(content);
    }

    public static String decrypt(String encrypt, String salt) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(salt);
        return textEncryptor.decrypt(encrypt);
    }

}
