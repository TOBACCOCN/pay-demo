package com.pay.example.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    // serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType

    // 支付宝网关
    private String serverUrl;

    // 应用ID，您的APPID，收款账号即是您的APPID对应支付宝账号
    private String appId;

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String privateKey;

    // 字符串格式
    private String format;

    // 编码格式
    public String charset;

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥
    public String alipayPublicKey;

    // 签名方式
    public String signType;

    // 支付后回调地址
    public String notifyUrl;

    // 商户 ID
    public String storeId;

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "AlipayConfig [serverUrl=" + serverUrl + ", appId=" + appId + ", privateKey=" + privateKey + ", format="
                + format + ", charset=" + charset + ", alipayPublicKey=" + alipayPublicKey + ", signType=" + signType
                + ", notifyUrl=" + notifyUrl + ", storeId=" + storeId + "]";
    }

    @Bean
    public AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
    }

}
