package com.pay.example.paypal;

import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * paypal 控制器
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@Controller
@Slf4j
public class PayPalController {

    private static final String PAYPAL_SUCCESS_URL = "/payPal/executePayment";
    private static final String PAYPAL_CANCEL_URL = "/payPal/cancelPayment";
    private static final String APPROVAL_URL_REL = "approval_url";
    private static final String STATE_SUCCESS = "approved";

    // private static Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PayPalTrade payPalTrade;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/payPal/createPayment", method = RequestMethod.POST)
    public String createPayment(HttpServletRequest request) {
        Double shipping = 1.00;
        Double subtotal = 5.00;
        Double tax = 1.00;
        String currency = "USD";
        String description = "payment description";

        Item item = new Item();
        item.setName("Ground Coffee 40 oz").setQuantity("1").setCurrency(currency).setPrice("5");
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemList.setItems(items);

        String paymentMethod = PayPalConstant.PAYMENT_METHOD_paypal;
        String paymentIntent = PayPalConstant.PAYMENT_INTENT_SALE;

        String successUrl = getRequestURl(request) + PAYPAL_SUCCESS_URL;
        String cancelUrl = getRequestURl(request) + PAYPAL_CANCEL_URL;
        try {
            Payment payment = payPalTrade.createPayment(shipping, subtotal, tax, currency, description, itemList,
                    paymentMethod, paymentIntent, successUrl, cancelUrl);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals(APPROVAL_URL_REL)) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

    @RequestMapping(value = PAYPAL_CANCEL_URL, method = RequestMethod.GET)
    public String cancelPayment() {
        return "cancel";
    }

    @RequestMapping(value = PAYPAL_SUCCESS_URL, method = RequestMethod.GET)
    public String executePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        log.info(">>>>> REQUEST_URL: {}", request.getRequestURL());
        try {
            Payment payment = payPalTrade.executePayment(paymentId, payerId);
            if (payment.getState().equals(STATE_SUCCESS)) {
                return "success";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

    private String getRequestURl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + "://" + serverName + ":" + serverPort + contextPath;
    }

}
