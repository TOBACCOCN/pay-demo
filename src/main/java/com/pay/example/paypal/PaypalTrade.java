package com.pay.example.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
@Slf4j
public class PaypalTrade {

    // private static Logger logger = LoggerFactory.getLogger(PayPalTrade.class);

    @Autowired
    private PaypalConfig paypalConfig;
    @Autowired
    private APIContext apiContext;

    /**
     * 创建付款
     * https://developer.paypal.com/docs/api/quickstart/payments/#create-payment
     *
     * @param shipping    邮费
     * @param subtotal    商品总价
     * @param tax         税费
     * @param currency    货币类型
     * @param description 描述
     * @return 付款对象
     */
    public Payment createPayment(Double shipping, Double subtotal, Double tax,
                                 String currency, String description) throws PayPalRESTException {
        Details details = new Details();
        details.setShipping(String.valueOf(shipping));
        details.setSubtotal(String.valueOf(subtotal));
        details.setTax(String.valueOf(tax));

        Amount amount = new Amount();
        // 澳元：AUD，巴西雷亚尔：BRL，加元：CAD，捷克克朗：CZK，丹麦克朗：DKK，欧元：EUR，港元：HKD，匈牙利福林：HUF，
        // 印度卢比：INR，以色列新谢克尔：ILS，日元：JPY，马来西亚林吉特：MYR，墨西哥比索：MXN，新台币：TWD，新西兰元：NZD，
        // 挪威克朗：NOK，菲律宾比索：PHP，波兰兹罗提：PLN，英镑：GBP，俄罗斯卢布：RUB，新加坡元：SGD，瑞典克朗：SEK，
        // 瑞士法郎：CHF，泰铢：THB，美国美元：USD
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", (shipping + subtotal + tax)));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(PaypalConstant.PAYMENT_METHOD_PAYPAL);

        Payment payment = new Payment();
        payment.setIntent(PaypalConstant.PAYMENT_INTENT_SALE);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(paypalConfig.getReturnUrl());
        redirectUrls.setCancelUrl(paypalConfig.getCancelUrl());
        payment.setRedirectUrls(redirectUrls);

        long start = System.currentTimeMillis();
        payment = payment.create(apiContext);
        log.info(">>>>> CREATE_PAYMENT_RESULT: [{}], COST: [{}] MS", payment, System.currentTimeMillis() - start);
        // {"id":"PAYID-LYKXQUY7S817880FN944843N","intent":"sale","payer":{"payment_method":"paypal"},"transactions":[{"related_resources":[],"amount":{"currency":"USD","total":"1.00"},"description":"PAYPAL DESCRIPTION","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"1.00","currency":"USD"}]}}],"state":"created","create_time":"2020-01-08T06:36:03Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYKXQUY7S817880FN944843N","rel":"self","method":"GET"},{"href":"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd\u003d_express-checkout\u0026token\u003dEC-60A24947WN722943P","rel":"approval_url","method":"REDIRECT"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYKXQUY7S817880FN944843N/execute","rel":"execute","method":"POST"}]}
        return payment;
    }

    /**
     * 执行付款
     * https://developer.paypal.com/docs/api/quickstart/payments/#execute-payment
     *
     * @param paymentId 付款 ID
     * @param payerId   付款人 ID
     * @return 付款对象
     */
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        long start = System.currentTimeMillis();
        payment = payment.execute(apiContext, paymentExecution);
        log.info(">>>>> EXECUTE_PAYMENT_RESULT: [{}], COST: [{}] MS", payment, System.currentTimeMillis() - start);
        // {"id":"PAYID-LYMYZGA1KT17621JY335084U","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"78E80404P31955519","transactions":[{"related_resources":[{"sale":{"id":"5NH01053JB244602L","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LYMYZGA1KT17621JY335084U","create_time":"2020-01-11T08:56:34Z","update_time":"2020-01-11T08:56:34Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/5NH01053JB244602L","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/5NH01053JB244602L/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYMYZGA1KT17621JY335084U","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2020-01-11T08:51:35Z","update_time":"2020-01-11T08:56:34Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYMYZGA1KT17621JY335084U","rel":"self","method":"GET"}]}
        return payment;
    }

    /**
     * 查询付款
     * 支付状态：（created、approved<completed、refunded>、failed）
     * https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetPaymentServlet.java
     *
     * @param paymentId 付款 ID
     * @return 付款对象
     */
    public Payment getPayment(String paymentId) throws PayPalRESTException {
        long start = System.currentTimeMillis();
        Payment payment = Payment.get(apiContext, paymentId);
        log.info(">>>>> GET_PAYMENT_RESULT:[{}], COST: [{}] MS", payment, System.currentTimeMillis() - start);
        // {"id":"PAYID-LW3FWFY16G51910YC295084P","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"2K075889X6185680M","transactions":[{"related_resources":[{"sale":{"id":"7GN627225H606415D","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"refunded","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LW3FWFY16G51910YC295084P","create_time":"2019-10-28T03:06:50Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"parent_payment","method":"GET"}]}},{"refund":{"id":"9VS622513P922364J","amount":{"currency":"USD","total":"-7.00"},"state":"completed","sale_id":"7GN627225H606415D","parent_payment":"PAYID-LW3FWFY16G51910YC295084P","create_time":"2019-10-31T09:50:20Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/refund/9VS622513P922364J","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D","rel":"sale","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-28T03:05:59Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"self","method":"GET"}]}
        return payment;
    }

    /**
     * 查询历史付款
     * https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetPaymentHistoryServlet.java
     *
     * @param count 付款数据数量
     * @return 历史付款对象
     */
    public PaymentHistory getPaymentHistory(String count) throws PayPalRESTException {
        Map<String, String> containerMap = new HashMap<>();
        containerMap.put("count", count);

        long start = System.currentTimeMillis();
        PaymentHistory paymentHistory = Payment.list(apiContext, containerMap);
        log.info(">>>>> GET_PAYMENT_HISTORY_RESULT: [{}], COST: [{}] MS", paymentHistory, System.currentTimeMillis() - start);
        // {"payments":[{"id":"PAYID-LYGCSJA76332903F6429345F","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"8Y80069167188854N","transactions":[{"related_resources":[{"sale":{"id":"4VY89857P9821791J","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LYGCSJA76332903F6429345F","create_time":"2020-01-01T05:13:41Z","update_time":"2020-01-01T05:13:41Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/4VY89857P9821791J","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/4VY89857P9821791J/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYGCSJA76332903F6429345F","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2020-01-01T05:07:48Z","update_time":"2020-01-01T05:13:41Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LYGCSJA76332903F6429345F","rel":"self","method":"GET"}]},{"id":"PAYID-LW3FWFY16G51910YC295084P","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"2K075889X6185680M","transactions":[{"related_resources":[{"sale":{"id":"7GN627225H606415D","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"refunded","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LW3FWFY16G51910YC295084P","create_time":"2019-10-28T03:06:50Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"parent_payment","method":"GET"}]}},{"refund":{"id":"9VS622513P922364J","amount":{"currency":"USD","total":"-7.00"},"state":"completed","sale_id":"7GN627225H606415D","parent_payment":"PAYID-LW3FWFY16G51910YC295084P","create_time":"2019-10-31T09:50:20Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/refund/9VS622513P922364J","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/7GN627225H606415D","rel":"sale","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-28T03:05:59Z","update_time":"2019-10-31T09:50:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FWFY16G51910YC295084P","rel":"self","method":"GET"}]},{"id":"PAYID-LW3FUOY3U0805050U195633K","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"8WM745769D795631X","transactions":[{"related_resources":[{"sale":{"id":"2LD8973198335891V","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LW3FUOY3U0805050U195633K","create_time":"2019-10-28T03:04:40Z","update_time":"2019-10-28T03:04:40Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/2LD8973198335891V","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/2LD8973198335891V/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FUOY3U0805050U195633K","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-28T03:02:19Z","update_time":"2019-10-28T03:04:40Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW3FUOY3U0805050U195633K","rel":"self","method":"GET"}]},{"id":"PAYID-LW2U3YI8UB70993GD7449126","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"816753988Y420733B","transactions":[{"related_resources":[{"sale":{"id":"5KU155104F152720F","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LW2U3YI8UB70993GD7449126","create_time":"2019-10-27T07:59:13Z","update_time":"2019-10-27T07:59:13Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/5KU155104F152720F","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/5KU155104F152720F/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW2U3YI8UB70993GD7449126","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-27T07:57:21Z","update_time":"2019-10-27T07:59:13Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW2U3YI8UB70993GD7449126","rel":"self","method":"GET"}]},{"id":"PAYID-LW2AE7Y0JE124261T909491J","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"6YR55853P54039242","transactions":[{"related_resources":[{"sale":{"id":"4VC64463J4781714V","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LW2AE7Y0JE124261T909491J","create_time":"2019-10-26T08:24:45Z","update_time":"2019-10-26T08:24:45Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/4VC64463J4781714V","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/4VC64463J4781714V/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW2AE7Y0JE124261T909491J","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-26T08:23:27Z","update_time":"2019-10-26T08:24:45Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LW2AE7Y0JE124261T909491J","rel":"self","method":"GET"}]},{"id":"PAYID-LWZ7XYY1EE35330AT888530P","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"78X97579PF910170L","transactions":[{"related_resources":[{"sale":{"id":"1T768926BU7953515","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LWZ7XYY1EE35330AT888530P","create_time":"2019-10-26T07:58:31Z","update_time":"2019-10-26T07:58:31Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/1T768926BU7953515","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/1T768926BU7953515/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LWZ7XYY1EE35330AT888530P","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-26T07:55:15Z","update_time":"2019-10-26T07:58:31Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LWZ7XYY1EE35330AT888530P","rel":"self","method":"GET"}]},{"id":"PAYID-LWZ7UVQ88861701EP181483P","intent":"sale","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"cart":"07018624GG8752903","transactions":[{"related_resources":[{"sale":{"id":"0FX39531K8008700C","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payment_mode":"INSTANT_TRANSFER","state":"completed","protection_eligibility":"ELIGIBLE","protection_eligibility_type":"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE","transaction_fee":{"currency":"USD","value":"0.54"},"parent_payment":"PAYID-LWZ7UVQ88861701EP181483P","create_time":"2019-10-26T07:53:25Z","update_time":"2019-10-26T07:53:25Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/0FX39531K8008700C","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/0FX39531K8008700C/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LWZ7UVQ88861701EP181483P","rel":"parent_payment","method":"GET"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00","handling_fee":"0.00","shipping_discount":"0.00","insurance":"0.00"}},"payee":{"email":"youjiezhixin-facilitator@eliteai.com.cn","merchant_id":"NYC75S9HRCAXN"},"description":"payment description","item_list":{"items":[{"name":"Ground Coffee 40 oz","quantity":"1","price":"5.00","currency":"USD","tax":"0.00"}],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-10-26T07:48:38Z","update_time":"2019-10-26T07:53:25Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LWZ7UVQ88861701EP181483P","rel":"self","method":"GET"}]},{"id":"PAY-0D4228675G5307345LTWP3LI","intent":"order","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"transactions":[{"related_resources":[{"order":{"id":"O-0WY293650D154662W","amount":{"currency":"USD","total":"0.01","details":{"subtotal":"0.01"}},"state":"VOIDED","parent_payment":"PAY-0D4228675G5307345LTWP3LI","create_time":"2020-01-11T07:42:53Z","update_time":"2020-01-11T07:42:53Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-0WY293650D154662W","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-0D4228675G5307345LTWP3LI","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-0WY293650D154662W/do-void","rel":"void","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-0WY293650D154662W/authorize","rel":"authorization","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-0WY293650D154662W/capture","rel":"capture","method":"POST"}]}}],"amount":{"currency":"USD","total":"0.01","details":{"subtotal":"0.01"}},"payee":{"merchant_id":"NYC75S9HRCAXN"},"item_list":{"items":[],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-05-28T09:21:49Z","update_time":"2019-06-26T09:23:05Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-0D4228675G5307345LTWP3LI","rel":"self","method":"GET"}]},{"id":"PAY-2BK85416XN1137903LTR3HGI","intent":"order","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}},"transactions":[{"related_resources":[{"order":{"id":"O-01J81453XH558290T","amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00"}},"state":"VOIDED","parent_payment":"PAY-2BK85416XN1137903LTR3HGI","create_time":"2020-01-11T07:42:53Z","update_time":"2020-01-11T07:42:53Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-01J81453XH558290T","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-2BK85416XN1137903LTR3HGI","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-01J81453XH558290T/do-void","rel":"void","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-01J81453XH558290T/authorize","rel":"authorization","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-01J81453XH558290T/capture","rel":"capture","method":"POST"}]}}],"amount":{"currency":"USD","total":"7.00","details":{"subtotal":"5.00","shipping":"1.00","tax":"1.00"}},"payee":{"merchant_id":"NYC75S9HRCAXN"},"item_list":{"items":[],"shipping_address":{"recipient_name":"buyer test","line1":"2211 N 1st Street","line2":"Building 17","city":"San Jose","country_code":"US","postal_code":"95131","state":"CA"}}}],"state":"approved","create_time":"2019-05-21T08:15:21Z","update_time":"2019-05-23T06:35:16Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-2BK85416XN1137903LTR3HGI","rel":"self","method":"GET"}]},{"id":"PAY-9F327181SV7426159LTRVF2I","intent":"order","payer":{"payment_method":"paypal","status":"VERIFIED","payer_info":{"email":"youjiezhixin-buyer-1@eliteai.com.cn","first_name":"test","last_name":"buyer","payer_id":"L8JJL9J78K226","phone":"2138861335","country_code":"C2","shipping_address":{"recipient_name":"s ss","line1":"十八丘","city":"株洲","country_code":"CN","postal_code":"412400","state":"湖南"}}},"transactions":[{"related_resources":[{"order":{"id":"O-6BP00459W22781233","amount":{"currency":"USD","total":"0.01","details":{"subtotal":"0.01"}},"state":"VOIDED","parent_payment":"PAY-9F327181SV7426159LTRVF2I","create_time":"2020-01-11T07:42:53Z","update_time":"2020-01-11T07:42:53Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-6BP00459W22781233","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-9F327181SV7426159LTRVF2I","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-6BP00459W22781233/do-void","rel":"void","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-6BP00459W22781233/authorize","rel":"authorization","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/orders/O-6BP00459W22781233/capture","rel":"capture","method":"POST"}]}}],"amount":{"currency":"USD","total":"0.01","details":{"subtotal":"0.01"}},"payee":{"merchant_id":"NYC75S9HRCAXN"},"item_list":{"items":[],"shipping_address":{"recipient_name":"s ss","line1":"十八丘","city":"株洲","country_code":"CN","postal_code":"412400","state":"湖南"}}}],"state":"approved","create_time":"2019-05-21T01:22:49Z","update_time":"2019-05-23T06:45:20Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-9F327181SV7426159LTRVF2I","rel":"self","method":"GET"}]}],"count":10,"next_id":"PAY-83M87778J6194124SLTRJRLY"}
        return paymentHistory;
    }

    /**
     * 查询交易
     * https://github.com/paypal/PayPal-Java-SDK/blob/master/rest-api-sample/src/main/java/com/paypal/api/payments/servlet/GetSaleServlet.java
     * 付款成功后会返回交易对象，交易对象包含交易 ID
     *
     * @param saleId 交易 ID
     * @return 销售对象
     */
    public Sale getSale(String saleId) throws PayPalRESTException {
        long start = System.currentTimeMillis();
        Sale sale = Sale.get(apiContext, saleId);
        log.info(">>>>> GET_SALE_RESULT:[{}], COST: [{}] MS", sale, System.currentTimeMillis() - start);
        return sale;
    }

    /**
     * 退款
     * https://developer.paypal.com/docs/api/quickstart/refund-payment/#
     *
     * @param id       交易 ID
     * @param currency 货币类型
     * @param total    退款金额
     * @return 退款详情
     */
    public DetailedRefund refund(String id, String currency, String total) throws PayPalRESTException {
        Sale sale = new Sale();
        sale.setId(id);

        RefundRequest refund = new RefundRequest();
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total);
        refund.setAmount(amount);

        long start = System.currentTimeMillis();
        DetailedRefund detailedRefund = sale.refund(apiContext, refund);
        log.info(">>>>> REFUND_RESULT: [{}], COST: [{}] MS", detailedRefund, System.currentTimeMillis() - start);
        // {"refund_from_transaction_fee":{"currency":"USD","value":"0.02"},"refund_from_received_amount":{"currency":"USD","value":"0.49"},"total_refunded_amount":{"currency":"USD","value":"0.51"},"id":"0GD64706M7173092X","amount":{"currency":"USD","total":"0.51"},"state":"completed","sale_id":"8KS42269T50192523","parent_payment":"PAYID-LVMPN6Q9PF45923TA5382724","create_time":"2020-01-11T08:31:31Z","update_time":"2020-01-11T08:31:31Z","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/refund/0GD64706M7173092X","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-LVMPN6Q9PF45923TA5382724","rel":"parent_payment","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/8KS42269T50192523","rel":"sale","method":"GET"}]}
        return detailedRefund;
    }
}
