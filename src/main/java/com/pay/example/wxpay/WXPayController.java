package com.pay.example.wxpay;

import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 微信支付控制器
 *
 * @author zhangyonghong
 * @date 2019.6.12
 */
@Controller
@Slf4j
public class WXPayController {

    // private static Logger logger = LoggerFactory.getLogger(WXPayController.class);

    @Autowired
    private WXPayConfig wxPayConfig;

    /**
     * 接收微信支付服务器关于用户支付结果的通知
     * 微信服务器发送过来的是 POST（text/xml）请求
     * https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    @PostMapping("/wxpay/notify")
    public void doNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(WXPayConstant.RETURN_CODE, WXPayConstant.RETURN_CODE_FAIL);
        map.put(WXPayConstant.RETURN_MSG, WXPayConstant.RETURN_MSG_FAIL);
        try {
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            String xml = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            log.info(">>>>> REQUEST_XML: [{}]", xml);

            Map<String, String> data = WXPayUtil.xmlToMap(xml);
            if (WXPayUtil.isSignatureValid(data, wxPayConfig.getKey(), wxPayConfig.getSignType())) {
                map.put(WXPayConstant.RETURN_CODE, WXPayConstant.RETURN_CODE_SUCCESS);
                map.put(WXPayConstant.RETURN_MSG, WXPayConstant.RETURN_MSG_OK);
                log.info(">>>>> SIGNATURE IS VALID");

                // TODO 处理业务

            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter, true));
            log.error(stringWriter.toString());
        } finally {
            response.getWriter().write(WXPayUtil.mapToXml(map));
        }
    }


}
