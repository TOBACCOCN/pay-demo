package com.pay.example.paypal;

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

/**
 * paypal 控制器
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@Controller
@RequestMapping("/paypal")
@Slf4j
public class PaypalController {

    // private static Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaypalTrade paypalTrade;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * 创建付款
     *
     * @return 付款地址
     */
    @RequestMapping(value = "/createPayment", method = RequestMethod.POST)
    public String createPayment() {
        Double shipping = 1.00;
        Double subtotal = 5.00;
        Double tax = 1.00;
        String currency = "USD";
        String description = "payment description";

        try {
            Payment payment = paypalTrade.createPayment(shipping, subtotal, tax, currency, description);
            for (Links links : payment.getLinks()) {
                if ("approved".equals(links.getRel())) {
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/cancelPayment", method = RequestMethod.GET)
    public String cancelPayment() {
        return "cancel";
    }

    /**
     * 执行付款
     *
     * @param request   请求对象
     * @param paymentId 付款 ID
     * @param payerId   付款人 ID
     * @return 付款结果页面
     */
    @RequestMapping(value = "executePayment", method = RequestMethod.GET)
    public String executePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {
        log.info(">>>>> REQUEST_URL: [{}]", request.getRequestURL());
        try {
            Payment payment = paypalTrade.executePayment(paymentId, payerId);
            if ("approved".equals(payment.getState())) {
                return "success";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

}
