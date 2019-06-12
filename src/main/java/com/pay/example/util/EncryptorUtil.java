package com.pay.example.util;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptorUtil {

    private static Logger logger = LoggerFactory.getLogger(EncryptorUtil.class);

    public static void encrypt(String content, String salt) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(salt);
        logger.info(">>>>> {}: {}", content, textEncryptor.encrypt(content));
    }

    public static void decrypt(String encrypt, String salt) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(salt);
        logger.info(">>>>> {}: {}", encrypt, textEncryptor.decrypt(encrypt));
    }


    public static void main(String[] args) {
    }

}
