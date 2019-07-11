package com.pay.example.wxpay;

import com.github.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WXPayController {

    private static Logger logger = LoggerFactory.getLogger(WXPayController.class);

    @Autowired
    private WXPayConfig wxPayConfig;

    @PostMapping("/wxpay/notify")
    public void doNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(WXPayConstant.returnCode, WXPayConstant.returnCodeFailed);
        map.put(WXPayConstant.returnMsg, "");
        try {
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            String xml = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            logger.info(">>>>> REQUEST_XML: {}", xml);

            Map<String, String> data = WXPayUtil.xmlToMap(xml);
            if (WXPayUtil.isSignatureValid(data, wxPayConfig.getKey(), wxPayConfig.getSignType())) {
                map.put(WXPayConstant.returnCode, WXPayConstant.returnCodeSuccess);
                map.put(WXPayConstant.returnMsg, WXPayConstant.returnMsgOK);
                logger.info(">>>>> SIGNATURE IS VALID");

                // TODO 处理业务

            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter, true));
            logger.error(stringWriter.toString());
        } finally {
            response.getWriter().write(WXPayUtil.mapToXml(map));
        }
    }


}
