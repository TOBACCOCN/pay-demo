package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.pay.example.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * 支付宝单元测试
 *
 * @author zhangyonghong
 * @date 2019.5.30
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class AlipayTradeTest {

    // private static Logger logger = LoggerFactory.getLogger(AlipayTradeTests.class);

    @Autowired
    private AlipayTrade alipayTrade;

    @Test
    public void tradePrecreate() throws Exception {
        String outTradeNo = UUID.randomUUID().toString().replace("-", "");
        String totalAmount = "0.01";
        String subject = "支付宝扫码支付测试";
        log.info(">>>>> OUT_TRADE_NO: [{}]", outTradeNo);
        AlipayTradePrecreateResponse response = alipayTrade.tradePrecreate(outTradeNo, totalAmount, subject);

        QRCodeUtil.encode(response.getQrCode(), 258, 258,
                "D:\\" + outTradeNo + ".png", "png");
    }

    @Test
    public void tradeQuery() throws AlipayApiException {
        String outTradeNo = "b31267bbb4c54d07a179caff73e164d4";
        alipayTrade.tradeQuery(outTradeNo);
    }

    @Test
    public void tradeClose() throws AlipayApiException {
        String outTradeNo = "80ba8696c3d04ee3b8d65dbd57512446";
        alipayTrade.tradeClose(outTradeNo);
    }

    @Test
    public void tradeCancel() throws AlipayApiException {
        String outTradeNo = "80ba8696c3d04ee3b8d65dbd57512446";
        alipayTrade.tradeCancel(outTradeNo);
    }

    @Test
    public void tradeRefund() throws AlipayApiException {
        String outTradeNo = "80ba8696c3d04ee3b8d65dbd57512446";
        String refundAmount = "0.01";
        alipayTrade.tradeRefund(outTradeNo, refundAmount);
    }

    @Test
    public void tradeRefundQuery() throws AlipayApiException {
        String outTradeNo = "80ba8696c3d04ee3b8d65dbd57512446";
        String outRequestNo = "80ba8696c3d04ee3b8d65dbd57512446";
        alipayTrade.tradeRefundQuery(outTradeNo, outRequestNo);
    }

}
