package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    //上传医院接口
    @PostMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //将map转换个类型，方便操作

        //获取医院系统传过来的签名，并且进行md5加密
        String sign = (String) parameterMap.get("sign");

        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parameterMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        //调用service中的方法
        hospitalService.save(parameterMap);
        return Result.ok();
    }


}
