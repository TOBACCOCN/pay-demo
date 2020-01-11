package com.pay.example.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.pay.example.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付单元测试
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class WXPayTest {

    // private static Logger logger = LoggerFactory.getLogger(WXPayTests.class);

    @Autowired
    private WXPay wxPay;
    @Autowired
    private WXPayConfig wxPayConfig;

    // 统一下单
    // https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
    @Test
    public void unifiedOrder() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        String outTradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        reqData.put(WXPayConstant.OUT_TRADE_NO, outTradeNo);
        reqData.put(WXPayConstant.TOTAL_FEE, "1");
        reqData.put(WXPayConstant.BODY, "Ground Coffee 40 oz * 1");
        reqData.put(WXPayConstant.SPBILL_CREATE_IP, "127.0.0.1");
        reqData.put(WXPayConstant.NOTIFY_URL, wxPayConfig.getNotifyUrl());
        reqData.put(WXPayConstant.TRADE_TYPE, WXPayConstant.TRADE_TYPE_NATIVE);
        Map<String, String> map = wxPay.unifiedOrder(reqData);
        log.info(">>>>> RESPONSE: [{}]", map);

        QRCodeUtil.encode(map.get(WXPayConstant.CODE_URL), 258, 258,
                "png", "D:\\" + outTradeNo + ".png");
    }

    // 查询订单
    // https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_2
    @Test
    public void orderQuery() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put(WXPayConstant.OUT_TRADE_NO, "21a4b60d6e534b3ea865b554b3297f6a");
        Map<String, String> map = wxPay.orderQuery(reqData);
        log.info(">>>>> RESPONSE: [{}]", map);
    }

    // 关闭订单
    // https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_3
    @Test
    public void closeOrder() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put(WXPayConstant.OUT_TRADE_NO, "21a4b60d6e534b3ea865b554b3297f6a");
        Map<String, String> map = wxPay.closeOrder(reqData);
        log.info(">>>>> RESPONSE: [{}]", map);
    }

    // 申请退款
    // https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_4
    @Test
    public void refund() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put(WXPayConstant.OUT_TRADE_NO, "71def6a33f55403090c36a9396a260bb");
        reqData.put(WXPayConstant.OUT_REFUND_NO, "71def6a33f55403090c36a9396a260bb");
        reqData.put(WXPayConstant.TOTAL_FEE, "1");
        reqData.put(WXPayConstant.REFUND_FEE, "1");
        Map<String, String> map = wxPay.refund(reqData);
        log.info(">>>>> RESPONSE: [{}]", map);
    }

    // 查询退款
    // https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_5
    @Test
    public void refundQuery() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put(WXPayConstant.OUT_TRADE_NO, "21a4b60d6e534b3ea865b554b3297f6a");
        Map<String, String> map = wxPay.refundQuery(reqData);
        log.info(">>>>> RESPONSE: [{}]", map);
    }

}
