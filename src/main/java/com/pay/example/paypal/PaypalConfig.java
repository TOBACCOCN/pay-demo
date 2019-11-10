package com.pay.example.paypal;

import com.paypal.base.rest.APIContext;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * paypal 配置类
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@Configuration
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "paypal")
@Data
public class PaypalConfig {

    private String clientId;
    private String clientSecret;
    private String mode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
