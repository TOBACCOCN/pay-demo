package com.pay.example.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 支付宝配置类
 *
 * @author zhangyonghong
 * @date 2019.5.30
 */
@Configuration
@PropertySource("classpath:pay.properties")
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayConfig {

    // serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType
    // 支付宝网关
    private String serverUrl;
    // 应用 ID，您的 APPID，收款账号即是您的 APPID 对应支付宝账号
    private String appId;
    // 商户私钥，您的 PKCS8 格式 RSA2 私钥
    private String privateKey;
    // 字符串格式
    private String format;
    // 编码格式
    private String charset;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应 APPID 下的支付宝公钥
    private String alipayPublicKey;
    // 签名方式
    private String signType;
    // 该笔订单允许的最晚付款时间，逾期将关闭交易
    private String timeoutExpress;
    // 支付后回调地址
    private String notifyUrl;
    // 商户 ID
    private String storeId;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
    }

}
