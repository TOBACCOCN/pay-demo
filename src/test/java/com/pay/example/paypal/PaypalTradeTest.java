package com.pay.example.paypal;

import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * paypal 单元测试
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class PaypalTradeTest {

    // private static Logger logger = LoggerFactory.getLogger(PayPalTradeTests.class);

    @Autowired
    private PaypalTrade paypalTrade;

    @Test
    public void createPayment() throws PayPalRESTException {
        Double shipping = 1D;
        Double subtotal = 2D;
        Double tax = 1D;
        String currency = "USD";
        String description = "PAYPAL DESCRIPTION";
        paypalTrade.createPayment(shipping, subtotal, tax, currency, description);
    }

    @Test
    public void executePayment() throws PayPalRESTException {
        String paymentId = "PAYID-LYGDEJA9LH4042075999553N";
        String payerId = "L8JJL9J78K226";
        paypalTrade.executePayment(paymentId, payerId);
    }

    // 查询付款
    @Test
    public void getPayment() throws PayPalRESTException {
        String paymentId = "PAYID-LYGDEJA9LH4042075999553N";
        paypalTrade.getPayment(paymentId);
    }

    // 查询历史付款
    @Test
    public void getPaymentHistory() throws PayPalRESTException {
        String count = "10";
        paypalTrade.getPaymentHistory(count);
    }

    // 查询交易
    @Test
    public void getSale() throws PayPalRESTException {
        String saleId = "51783015WJ5447923";
        paypalTrade.getSale(saleId);
    }

    // 退款
    @Test
    public void refund() throws PayPalRESTException {
        // 每次退款请修改 id，并将 currency 和 total 替换成对应的值
        String id = "57C253130N2657817";
        String currency = "USD";
        String total = "7.00";
        paypalTrade.refund(id, currency, total);
    }

}
