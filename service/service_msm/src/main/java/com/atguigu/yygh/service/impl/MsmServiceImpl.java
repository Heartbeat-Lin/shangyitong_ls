package com.atguigu.yygh.service.impl;

import com.atguigu.yygh.service.MsmService;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Set;

@Service
public class MsmServiceImpl implements MsmService {


    @Override
    public boolean send(String phone, String code) {

        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)){
            return false;
        }

        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf0708842397dd018423f7f4700003";
        String accountToken = "39b7466ffcfb45898cfa0c6b83f93486";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf0708842397dd0184247bf0e5001f";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId= "1";
        String[] datas = {code,"10"};//这里的10就是过期时间
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        if("000000".equals(result.get("statusCode"))){
            return  true;
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return false;
        }

    }
}
