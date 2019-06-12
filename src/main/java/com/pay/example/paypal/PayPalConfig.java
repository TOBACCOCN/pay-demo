package com.pay.example.paypal;

import com.paypal.base.rest.APIContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {

    private String clientId;
    private String clientSecret;
    private String mode;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
