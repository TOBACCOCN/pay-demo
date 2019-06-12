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

@Configuration
public class PayPalTrade {

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(Double shipping, Double subtotal, Double tax, String currency, String description, ItemList itemList,
                                 String paymentMethod, String paymentIntent, String successUrl, String cancelUrl) throws PayPalRESTException{
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

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public Payment getPayment(String paymentId) throws PayPalRESTException {
        return Payment.get(apiContext, paymentId);
    }

    public PaymentHistory getPaymentHistory(String count) throws PayPalRESTException {
        Map<String, String> containerMap = new HashMap<>();
        containerMap.put("count", count);
        return Payment.list(apiContext, containerMap);
    }

    public Sale getSale(String saleId) throws PayPalRESTException {
        return Sale.get(apiContext, saleId);
    }

    public DetailedRefund refundPayment(String id, String currency, String total) throws PayPalRESTException {
        Sale sale = new Sale();
        sale.setId(id);

        RefundRequest refund = new RefundRequest();
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total);
        refund.setAmount(amount);

        return  sale.refund(apiContext, refund);
    }
}
