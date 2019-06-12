package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AlipayController {

    private static Logger logger = LoggerFactory.getLogger(AlipayController.class);

    @Autowired
    private AlipayTrade alipayTrade;

    @PostMapping("/alipay/hello")
    @ResponseBody
    public Object hello(HttpServletRequest request) {
        Map<String, String> map = getMap(request.getParameterMap());
        logger.info(">>>>> PARAM_MAP: {}", map);
        return map;
    }

    @PostMapping("/alipay/notify")
    public void doNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> map = getMap(request.getParameterMap());
        logger.info(">>>>> NOTIFY_PARAM_MAP: {}", map);
        if (alipayTrade.rsaCheck(map)) {
            response.getWriter().write("success");
            logger.info("SUCCESS");
        } else {
            response.getWriter().write("failure");
            logger.info("FAILURE");
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
