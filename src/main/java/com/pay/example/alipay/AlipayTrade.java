package com.pay.example.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AlipayTrade {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private AlipayClient alipayClient;

    /**
     * tradePrecreate 交易预创建
     * https://docs.open.alipay.com/api_1/alipay.trade.precreate/
     *
     * @param outTradeNo  商户订单号，64 个字符以内、只能包含字母、数字、下划线
     * @param totalAmount 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
     * @param subject     订单标题
     * @return 交易预创建响应对象
     */
    public AlipayTradePrecreateResponse tradePrecreate(String outTradeNo, String totalAmount, String subject) throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(totalAmount);
        model.setSubject(subject);
        model.setTimeoutExpress(alipayConfig.getTimeoutExpress());
        // 创建 API 对应的 request 类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        // 设置业务参数
        request.setBizModel(model);
        request.setNotifyUrl(alipayConfig.getNotifyUrl());

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        log.debug(">>>>> TRADE_PRECREATE_RESPONSE: [{}], COST: [{}] MS",
                response.getBody(), System.currentTimeMillis() - start);
        // {"alipay_trade_precreate_response":{"code":"10000","msg":"Success","out_trade_no":"1578465103125","qr_code":"https:\/\/qr.alipay.com\/bax00005qwnxrwhsznnr603d"},"sign":"lRJHnkuhvpTbNKuvXa5+KHFVhGu2YLiMo9xWLi+WMcEGlxB+ux4wBWNsZglqJZI+ExG67dvrQFlV29kNi5YHOXIU0dfMjG/mGMiKG8ZknRUykB6Aj0eJP86aPwWvzoWB1VxAq5RXF1dN9O+w7/xaJelhH13gG3Nck9yLfFQi4flWvwmtWti/KLsYXhizUCbJyPIHSh1YkKcl7dS8RmSR41+JEATJKwGqBh0Qe9aBRECmsOHSJf5nxigLshCR/ahMcnt841B7TVIYHisTf8Oh5+zp+fTiUx8B5r9XVeXlNbz1t4AYL+wbqruW3/cGt7KuZDL95GC+4MIvLdIayYm0kg=="}
        return response;
    }

    /**
     * rsaCheck 验签
     * https://docs.open.alipay.com/194/105322/
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
     * tradeQuery 交易查询
     * 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
     * TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
     * https://docs.open.alipay.com/api_1/alipay.trade.query/
     *
     * @param outTradeNo 商户订单号
     * @return 交易查询响应对象
     */
    public AlipayTradeQueryResponse tradeQuery(String outTradeNo) throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        // 创建 API 对应的 request 类
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        // 设置业务参数
        request.setBizModel(model);

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        log.info(">>>>> TRADE_QUERY_RESPONSE: [{}], COST: [{}] MS",
                response.getBody(), System.currentTimeMillis() - start);
        // {"alipay_trade_query_response":{"code":"10000","msg":"Success","buyer_logon_id":"zyh***@163.com","buyer_pay_amount":"0.01","buyer_user_id":"2088102046300981","fund_bill_list":[{"amount":"0.01","fund_channel":"PCREDIT"}],"invoice_amount":"0.01","out_trade_no":"1572508450035","point_amount":"0.00","receipt_amount":"0.01","send_pay_date":"2019-10-31 15:58:04","total_amount":"0.01","trade_no":"2019103122001400985713574273","trade_status":"TRADE_SUCCESS"},"sign":"zQzxPt+wcy7z8uzdVYnfOzILplP9bT4E2TeNgMRmpywbd+lW/FjINRjuGmaA0bzabb+Rv3tGqc62HIRKTBqmp5dcW3wMggW+MY48Qd9j+QVUqoiIpENtR1Mi3pvWtzOp/H4HQHrAVGfEGIGWNtqUMPrJTZNoplWiK7iQUZ7+c288I4n+cVaa3FDhFo7i1XUnOgJi+YY5HDr/vNedX3gXN3mDvI1qi4Lk67aXt4SZOARNUCoVoPVoacxtd4q17h2AJ9ysB06ryrtHtsdmiMzCBF0+IPJDy5ZJ/u4iqh5xEsL5i2dsFkDsUF7QJbVviYtScgDX+sj7LscObN37mAELDQ=="}
        return response;
    }

    /**
     * tradeClose 交易关闭
     * https://docs.open.alipay.com/api_1/alipay.trade.close/
     *
     * @param outTradeNo 商户订单号
     * @return 交易退款响应对象
     */
    public AlipayTradeCloseResponse tradeClose(String outTradeNo) throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(outTradeNo);
        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeCloseRequest closeRequest = new AlipayTradeCloseRequest();
        closeRequest.setBizModel(model);

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeCloseResponse response = alipayClient.execute(closeRequest);
        log.info(">>>>>TRADE_CLOSE_RESPONSE: [{}], COST: [{}]",
                response.getBody(), System.currentTimeMillis() - start);
        // {"alipay_trade_close_response":{"code":"10000","msg":"Success","out_trade_no":"1578713503687","trade_no":"2020011122001400981411426089"},"sign":"uinMuu3nAVDyCtkrjzReMDLyFzYgiLPRz1q3RD33SatiJUzL1G+1z9dySPwFVboLZ48DesD897u2fy545nQS1aYmmD/BQGvMhK3rPw6S4UOuPydS1dRYP5/APVpFds9x/BUa/uU+sQXWaZCdR3lsAV47PCC1FIyp1Qcxnt+hshGkFRObuwcje4bu51qdbvMLUEu8w0vZ9dgl9atUrhfgn+FTu1NYH1mcD0Ch06p3pWI8ewIb1fGrehD4n2BEk3lssSbKFkYmixjckA1qCocSHfprG5FUhZBI9PJ/szobGJXaZ828N+48LTla6BtJRbfwSkrMT8bj+BhDu33IHY44VQ=="}
        return response;
    }

    /**
     * tradeCancel 交易撤销
     * https://docs.open.alipay.com/api_1/alipay.trade.cancel/
     *
     * @param outTradeNo 商户订单号
     * @return 交易撤销响应对象
     */
    public AlipayTradeCancelResponse tradeCancel(String outTradeNo) throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo(outTradeNo);
        // 创建 API 对应的 request 类
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        // 设置业务参数
        request.setBizModel(model);

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeCancelResponse response = alipayClient.execute(request);
        log.info(">>>>> TRADE_CANCEL_RESPONSE: [{}], COST: [{}]",
                response.getBody(), System.currentTimeMillis() - start);
        // {"alipay_trade_cancel_response":{"code":"10000","msg":"Success","out_trade_no":"1571145702256","retry_flag":"N"},"sign":"IV+2cZoxDVn2tDyjt3+iUoWJjC1UU9b9idQ0oFOJRopECkRobF+JsGSQBWFJgnCS9rrKVd1oSONFv3PKPMtgeVqmQUlu1PsPOsZAW1eZ2I32m/29FqClnjoWu/B5cuQea/9p93jI+2CcImSUS+PA3jyfG3Oe1Jy/tzeE4QAEJRE6nWHCRyqdFNTz4kzRsuDPZxcKxiE0MYJtSk+nBqZgE3ilWONkdSq7FnDrA0ycFW7zg/Poz76RoRoYI0iMk89vjJN6iCtOSRIGgaUFH0YP0Pd88jlHxzpvBoSmLE3mTz/rBUKE2+kU2+IRnhmiuK7RGjpYYAxEosbu29ds02XXhA=="}
        return response;
    }

    /**
     * tradeRefund 交易退款
     * https://docs.open.alipay.com/api_1/alipay.trade.refund/
     *
     * @param outTradeNo   商户订单号
     * @param refundAmount 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
     * @return 交易退款响应对象
     */
    public AlipayTradeRefundResponse tradeRefund(String outTradeNo, String refundAmount) throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(outTradeNo);
        model.setRefundAmount(refundAmount);
        // 创建 API 对应的 request 类
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        // 设置业务参数
        request.setBizModel(model);

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        log.info(">>>>> TRADE_REFUND_RESPONSE: [{}], COST: [{}]",
                response.getBody(), System.currentTimeMillis() - start);
        // {"alipay_trade_refund_response":{"code":"10000","msg":"Success","buyer_logon_id":"zyh***@163.com","buyer_user_id":"2088102046300981","fund_change":"Y","gmt_refund_pay":"2020-01-11 14:51:28","out_trade_no":"1578725389021","refund_detail_item_list":[{"amount":"0.01","fund_channel":"PCREDIT"}],"refund_fee":"0.01","send_back_fee":"0.01","trade_no":"2020011122001400981411442299"},"sign":"r3llVDx9HXg3foKvWzGQx4tJ/Ey2qCH6WajxfEq45aJBCEcK7r6NpK2Ll8ZWeXHojWWZhk23i5Yto3vpC0hsfTe+FHih5LDPy/r3IqpU1HeC5zDTpncL7Z601ar53H8BmOpkfyFprugNfTpSNg0Fs3PtRdAgFSuUlcOjBK+oPI+Z+i01KkIkXMhXT7Q+IkErE1vPhpO4IxXK5rLBJiiVboJflM8w48OSMaHv0HyTTTSDt0/qWXYK4PIGWkQSEuBf/EUyjRovSMNATyFHUGsnn/dP3FQUJ7KIhvzpgx/YwhPmWSMfMaJeM1cUkBV8k1rTM48vVpnRRVz/KUE4fzkuUg=="}
        return response;
    }

    /**
     * tradeRefundQuery 交易退款查询
     * https://docs.open.alipay.com/api_1/alipay.trade.fastpay.refund.query/
     *
     * @param outTradeNo   订单支付时传入的商户订单号
     * @param outRequestNo 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
     * @return 交易退款查询响应对象
     */
    public AlipayTradeFastpayRefundQueryResponse tradeRefundQuery(String outTradeNo, String outRequestNo)
            throws AlipayApiException {
        long start = System.currentTimeMillis();

        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(outTradeNo);
        model.setOutRequestNo(outRequestNo);
        // 创建 API 对应的 request 类
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        // 设置业务参数
        request.setBizModel(model);

        // 通过 alipayClient 调用 API，获得对应的 response 类
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        log.info(">>>>> REFUND_QUERY_RESPONSE: [{}], COST: [{}]",
                response.getBody(), System.currentTimeMillis() - start);
        return response;
    }

}
