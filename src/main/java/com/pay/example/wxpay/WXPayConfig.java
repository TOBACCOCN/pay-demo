package com.pay.example.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 微信支付配置类
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@Component
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "wxpay")
@Data
public class WXPayConfig implements com.github.wxpay.sdk.WXPayConfig {

    private String appID;
    private String mchID;
    private String key;
    private String certPath;
    private InputStream certStream;
    private int httpConnectTimeoutMs;
    private int httpReadTimeoutMs;
    private WXPayConstants.SignType signType;
    private boolean useSandbox;
    private String notifyUrl;

    public void setCertPath(String certPath) {
        this.certPath = certPath;
        this.certStream = this.getClass().getClassLoader().getResourceAsStream(certPath);
    }

    @Bean
    public WXPay wxPay() {
        return new WXPay(this, signType, useSandbox);
    }
}
