package com.pay.example.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * paypal 交易处理类
 *
 * @author zhangyonghong
 * @date 2019.6.22
 */
@Configuration
public class PayPalTrade {

    @Autowired
    private APIContext apiContext;

    /**
     * 创建付款 https://developer.paypal.com/docs/api/quickstart/payments/#create-payment
     *
     * @param shipping      邮费
     * @param subtotal      商品总价
     * @param tax           税费
     * @param currency      货币类型
     * @param description   描述
     * @param itemList      商品列表
     * @param paymentMethod 支付方式（paypal 或信用卡）
     * @param paymentIntent 支付类型（sale 或 authorize）
     * @param successUrl    支付后跳转页面
     * @param cancelUrl     取消支付后跳珠页面
     * @return 付款对象
     */
    public Payment createPayment(Double shipping, Double subtotal, Double tax, String currency, String description, ItemList itemList,
                                 String paymentMethod, String paymentIntent, String successUrl, String cancelUrl) throws PayPalRESTException {
        Details details = new Details();
        details.setShipping("" + shipping);
        details.setSubtotal("" + subtotal);
        details.setTax("" + tax);

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", (shipping + subtotal + tax)));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setItemList(itemList);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(paymentMethod);

        Payment payment = new Payment();
        payment.setIntent(paymentIntent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(successUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    /**
     * 执行付款 https://developer.paypal.com/docs/api/quickstart/payments/#execute-payment
     *
     * @param paymentId 付款 ID
     * @param payerId   付款人 ID
     * @return 付款对象
     */
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    /**
     * 查询付款 https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetPaymentServlet.java
     *
     * @param paymentId 付款 ID
     * @return 付款对象
     */
    public Payment getPayment(String paymentId) throws PayPalRESTException {
        return Payment.get(apiContext, paymentId);
    }

    /**
     * 查询历史付款 https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetPaymentHistoryServlet.java
     *
     * @param count 付款数据数量
     * @return 付款对象
     */
    public PaymentHistory getPaymentHistory(String count) throws PayPalRESTException {
        Map<String, String> containerMap = new HashMap<>();
        containerMap.put("count", count);
        return Payment.list(apiContext, containerMap);
    }

    /**
     * 查询交易 https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetSaleServlet.java
     * 付款成功后会返回交易对象，交易对象包含交易 ID
     *
     * @param saleId 交易 ID
     * @return 销售对象
     */
    public Sale getSale(String saleId) throws PayPalRESTException {
        return Sale.get(apiContext, saleId);
    }

    /**
     * 退款 https://developer.paypal.com/docs/api/quickstart/refund-payment/#
     *
     * @param id       交易 ID
     * @param currency 货币类型
     * @param total    退款金额
     * @return 退款详情
     */
    public DetailedRefund refundPayment(String id, String currency, String total) throws PayPalRESTException {
        Sale sale = new Sale();
        sale.setId(id);

        RefundRequest refund = new RefundRequest();
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total);
        refund.setAmount(amount);

        return sale.refund(apiContext, refund);
    }
}
