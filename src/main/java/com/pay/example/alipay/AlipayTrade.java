package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付宝交易处理类
 *
 * @author zhangyonghong
 * @date 2019.6.22
 */
@Component
public class AlipayTrade {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private AlipayClient alipayClient;

    /**
     * tradePrecreate 交易预创建
     * https://docs.open.alipay.com/api_1/alipay.trade.precreate/
     *
     * @param model 交易预创建对象
     * @return 交易预创建响应对象
     */
    public AlipayTradePrecreateResponse tradePrecreate(AlipayTradePrecreateModel model) throws AlipayApiException {
        // 创建 API 对应的 request 类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        // 设置业务参数
        request.setBizModel(model);
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        // 通过 alipayClient 调用 API，获得对应的 response 类
        return alipayClient.execute(request);
    }

    /**
     * rsaCheck 验签 https://docs.open.alipay.com/194/105322/
     *
     * @param map 验签参数 map
     * @return 验签成功返回 true，验签失败返回 false
     */
    public boolean rsaCheck(Map<String, String> map) throws AlipayApiException {
        // //调用 SDK 验证签名
        return AlipaySignature.rsaCheckV1(map, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(),
                alipayConfig.getSignType());
    }

    /**
     * tradeQuery 交易查询 https://docs.open.alipay.com/api_1/alipay.trade.query/
     *
     * @param model 交易查询对象
     * @return 交易查询响应对象
     */
    public AlipayTradeQueryResponse tradeQuery(AlipayTradeQueryModel model) throws AlipayApiException {
        // 创建 API 对应的 request 类
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        // 设置业务参数
        request.setBizModel(model);
        // 通过 alipayClient 调用 API，获得对应的 response 类
        return alipayClient.execute(request);
    }

    /**
     * tradeCancel 交易撤销 https://docs.open.alipay.com/api_1/alipay.trade.cancel/
     *
     * @param model 交易撤销对象
     * @return 交易撤销响应对象
     */
    public AlipayTradeCancelResponse tradeCancel(AlipayTradeCancelModel model) throws AlipayApiException {
        // 创建 API 对应的 request 类
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        // 设置业务参数
        request.setBizModel(model);
        // 通过 alipayClient 调用 API，获得对应的 response 类
        return alipayClient.execute(request);
    }

    /**
     * tradeRefund 交易退款 https://docs.open.alipay.com/api_1/alipay.trade.refund/
     *
     * @param model 交易退款对象
     * @return 交易退款响应对象
     */
    public AlipayTradeRefundResponse tradeRefund(AlipayTradeRefundModel model) throws AlipayApiException {
        // 创建 API 对应的 request 类
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        // 设置业务参数
        request.setBizModel(model);
        // 通过 alipayClient 调用 API，获得对应的 response 类
        return alipayClient.execute(request);
    }

    /**
     * tradeRefundQuery 交易退款查询
     * https://docs.open.alipay.com/api_1/alipay.trade.fastpay.refund.query/
     *
     * @param model 交易退款查询对象
     * @return 交易退款查询响应对象
     */
    public AlipayTradeFastpayRefundQueryResponse tradeRefundQuery(AlipayTradeFastpayRefundQueryModel model)
            throws AlipayApiException {
        // 创建 API 对应的 request 类
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        // 设置业务参数
        request.setBizModel(model);
        // 通过 alipayClient 调用 API，获得对应的 response 类
        return alipayClient.execute(request);
    }

}
