package com.pay.example.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.InputStream;

@Configuration
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "wxpay")
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

    @Override
    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    @Override
    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
        this.certStream = this.getClass().getClassLoader().getResourceAsStream(certPath);
    }

    @Override
    public InputStream getCertStream() {
        return certStream;
    }

    public void setCertStream(InputStream certStream) {
        this.certStream = certStream;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    public WXPayConstants.SignType getSignType() {
        return signType;
    }

    public void setSignType(WXPayConstants.SignType signType) {
        this.signType = signType;
    }

    public boolean isUseSandbox() {
        return useSandbox;
    }

    public void setUseSandbox(boolean useSandbox) {
        this.useSandbox = useSandbox;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Bean
    public WXPay wxPay() {
        return new WXPay(this, signType, useSandbox);
    }
}
