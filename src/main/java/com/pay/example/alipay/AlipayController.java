package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝控制器
 *
 * @author zhangyonghong
 * @date 2019.5.31
 */
@RestController
@Slf4j
public class AlipayController {

    // private static Logger logger = LoggerFactory.getLogger(AlipayController.class);

    @Autowired
    private AlipayTrade alipayTrade;

    @PostMapping("/alipay/hello")
    public Object hello(HttpServletRequest request) {
        Map<String, String> map = getMap(request.getParameterMap());
        log.info(">>>>> PARAM_MAP: {}", map);
        return map;
    }

    /**
     * 接收支付宝服务器关于用户支付结果的通知
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    @PostMapping("/alipay/notify")
    public void doNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> map = getMap(request.getParameterMap());
        log.info(">>>>> NOTIFY_PARAM_MAP: {}", map);
        if (alipayTrade.rsaCheck(map)) {
            response.getWriter().write("success");
            log.info(">>>>> CHECK_SIGN SUCCESS");
        } else {
            response.getWriter().write("failure");
            log.info(">>>>> CHECK_SIGN FAILURE");
        }
    }

    private Map<String, String> getMap(Map<String, String[]> paramMap) {
        Map<String, String> map = new HashMap<>();
        for (String name : paramMap.keySet()) {
            String[] values = paramMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            map.put(name, valueStr);
        }
        return map;
    }

}
