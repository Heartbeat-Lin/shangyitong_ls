package com.atguigu.yygh.service;

import java.util.Map;

public interface WeixinService {
    Map createNative(Long orderId);

    //调用查询接口
    Map<String, String> queryPayStatus(Long orderId, String name);


    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);

}
