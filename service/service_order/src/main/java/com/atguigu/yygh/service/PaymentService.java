package com.atguigu.yygh.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void savePaymentInfo(OrderInfo order, Integer status);


    //更新订单状态
    void paySuccess(String out_trade_no, Integer status, Map<String, String> resultMap);

    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

}
