package com.pay.example;

import com.pay.example.paypal.PayPalTrade;
import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PayPalTradeTests {

    private static Logger logger = LoggerFactory.getLogger(PayPalTradeTests.class);

    @Autowired
    private PayPalTrade payPalTrade;

    @Test
    public void getPayment() throws PayPalRESTException {
        Payment payment = payPalTrade.getPayment("PAYID-LTXZBXA1LX695496U1097253");
        logger.info(">>>>> payment: {}", payment);
    }

    @Test
    public void getPaymentHistory() throws PayPalRESTException {
        PaymentHistory paymentHistory = payPalTrade.getPaymentHistory("10");
        logger.info(">>>>> paymentHistory: {}", paymentHistory);
    }

    @Test
    public void getSale() throws PayPalRESTException {
        Sale sale = payPalTrade.getSale("51783015WJ5447923");
        logger.info(">>>>> sale: {}", sale);
    }

    @Test
    public void refundPayment() throws PayPalRESTException {
        // 每次退款请修改 id，并将 currency 和 total 替换成对应的值
        DetailedRefund detailedRefund = payPalTrade.refundPayment("57C253130N2657817", "USD", "7.00");
        logger.info(">>>>> detailedRefund: {}", detailedRefund);
    }

}
