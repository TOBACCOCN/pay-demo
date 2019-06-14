package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.response.*;
import com.pay.example.util.QRCodeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlipayTradeTests {

    private static Logger logger = LoggerFactory.getLogger(AlipayTradeTests.class);

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private AlipayTrade alipayTrade;

    @Test
    public void tradePrecreate() throws Exception {
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject("支付宝扫码支付测试");
        model.setTotalAmount("0.01");
        model.setStoreId(alipayConfig.storeId);
        model.setTimeoutExpress("5m");
        String outTradeNo = UUID.randomUUID().toString().replace("-", "");
        logger.info("OUT_TRADE_NO: {}", outTradeNo);
        model.setOutTradeNo(outTradeNo);
        AlipayTradePrecreateResponse response = alipayTrade.tradePrecreate(model);
        logger.info("RESPONSE_BODY: {}", response.getBody());
        // QR_CODE：当前预下单请求生成的二维码码串，可以用二维码生成工具根据该码串值生成对应的二维码
        logger.info("QR_CODE: {}", response.getQrCode());

        QRCodeUtil.encode(response.getQrCode(), 258, 258, "D:\\" + outTradeNo + ".png", "png");
    }

    @Test
    public void tradeQuery() throws AlipayApiException {
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo("b31267bbb4c54d07a179caff73e164d4");
        AlipayTradeQueryResponse response = alipayTrade.tradeQuery(model);
        logger.info("RESPONSE_BODY: {}", response.getBody());
        // TRADE_STATUS
        // 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
        // TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
        logger.info("TRADE_STATUS: {}", response.getTradeStatus());
    }

    @Test
    public void tradeCancel() throws AlipayApiException {
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo("80ba8696c3d04ee3b8d65dbd57512446");
        AlipayTradeCancelResponse response = alipayTrade.tradeCancel(model);
        logger.info("RESPONSE_BODY: {}", response.getBody());
        // ACTION：本次撤销触发的交易动作（close：关闭交易，无退款；refund：产生了退款）
        logger.info("ACTION: {}", response.getAction());
    }

    @Test
    public void tradeRefund() throws AlipayApiException {
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo("80ba8696c3d04ee3b8d65dbd57512446");
        model.setRefundAmount("0.01");
        AlipayTradeRefundResponse response = alipayTrade.tradeRefund(model);
        logger.info("RESPONSE_BODY: {}", response.getBody());
        // 异常示例
        //		{
        //		    "alipay_trade_refund_response": {
        //		        "code": "20000",
        //		        "msg": "Service Currently Unavailable",
        //		        "sub_code": "isp.unknow-error",
        //		        "sub_msg": "系统繁忙"
        //		    },
        //		    "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
        //		}
        logger.info("OUT_TRADE_NO: {}", response.getOutTradeNo());
    }

    @Test
    public void tradeRefundQuery() throws AlipayApiException {
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo("80ba8696c3d04ee3b8d65dbd57512446");
        model.setOutRequestNo("80ba8696c3d04ee3b8d65dbd57512446");
        AlipayTradeFastpayRefundQueryResponse response = alipayTrade.tradeRefundQuery(model);
        logger.info("RESPONSE_BODY: {}", response.getBody());
        // 查询到数据即为退款成功
        // 异常示例
        //		{
        //		    "alipay_trade_fastpay_refund_query_response": {
        //		        "code": "20000",
        //		        "msg": "Service Currently Unavailable",
        //		        "sub_code": "isp.unknow-error",
        //		        "sub_msg": "系统繁忙"
        //		    },
        //		    "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
        //		}
        logger.info("OUT_TRADE_NO: {}", response.getOutTradeNo());
    }

}
