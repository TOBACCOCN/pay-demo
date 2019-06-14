package com.pay.example.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JasyptUtilTests {

    private static Logger logger = LoggerFactory.getLogger(JasyptUtilTests.class);

    @Test
    public void encrypt() {
        String content = "test";
        String salt = "test";
        logger.info(">>>>> {}: {}", content, JasyptUtil.encrypt(content, salt));
    }

    @Test
    public void decrypt() {
        String encrypt = "tFKs7gO+mFpEXBqWwAUq0AMBPDMNvdUh";
        String salt = "test";
        logger.info(">>>>> {}: {}", encrypt, JasyptUtil.decrypt(encrypt, salt));
    }

}
