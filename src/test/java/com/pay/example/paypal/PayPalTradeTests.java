package com.pay.example.paypal;

import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.api.payments.Sale;
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
public class PayPalTradeTests {

    // private static Logger logger = LoggerFactory.getLogger(PayPalTradeTests.class);

    @Autowired
    private PayPalTrade payPalTrade;

    // 查询付款
    @Test
    public void getPayment() throws PayPalRESTException {
        Payment payment = payPalTrade.getPayment("PAYID-LTXZBXA1LX695496U1097253");
        log.info(">>>>> PAYMENT: {}", payment);
    }

    // 查询历史付款
    @Test
    public void getPaymentHistory() throws PayPalRESTException {
        PaymentHistory paymentHistory = payPalTrade.getPaymentHistory("10");
        log.info(">>>>> PAYMENT_HISTORY: {}", paymentHistory);
    }

    // 查询交易
    @Test
    public void getSale() throws PayPalRESTException {
        Sale sale = payPalTrade.getSale("51783015WJ5447923");
        log.info(">>>>> SALE: {}", sale);
    }

    // 退款
    @Test
    public void refundPayment() throws PayPalRESTException {
        // 每次退款请修改 id，并将 currency 和 total 替换成对应的值
        DetailedRefund detailedRefund = payPalTrade.refundPayment("57C253130N2657817", "USD", "7.00");
        log.info(">>>>> DETAILED_REFUND: {}", detailedRefund);
    }

}
