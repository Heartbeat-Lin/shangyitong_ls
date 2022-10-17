package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.common.helper.HttpRequestHelper;
import com.atguigu.common.utils.MD5;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
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

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //删除排班接口
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) parameterMap.get("hoscode");
        String hosScheduleId = (String) parameterMap.get("hosScheduleId");

        //todo:签名校验

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }

    //查询排班接口
    @PostMapping("/schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //将map转换个类型，方便操作

        //医院编号
        String hoscode = (String) parameterMap.get("hoscode");
        //当前页 和 每页记录数
        int page = StringUtils.isEmpty(parameterMap.get("page")) ? 1 : Integer.parseInt((String) parameterMap.get("page"));
        int limit = StringUtils.isEmpty(parameterMap.get("limit")) ? 1 : Integer.parseInt((String) parameterMap.get("limit"));

        //因为这是一个分页查询，所以我们可以把条件放到一个封装好的对象中
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        //调用service中的方法
        Page<Schedule> pageModel1 = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);

        return Result.ok(pageModel1);

    }


    //上传排班接口
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());

        //todo:签名校验

        scheduleService.save(parameterMap);
        return Result.ok();

    }

    //删除医院接口
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) parameterMap.get("hoscode");
        String depcode = (String) parameterMap.get("depcode");

        //todo:签名校验
        //把数据库查询出来的签名做加密
        String signKey = hospitalSetService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        //获取医院系统传过来的签名
        String hospSign = (String) parameterMap.get("sign");
        //判断签名是否一致
        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR); //里面的枚举类是在model里面定义好的
        }

        departmentService.remove(hoscode,depcode);
        return Result.ok();

    }

    //上传科室接口
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //将map转换个类型，方便操作


        //获取医院系统传过来的签名
        String hospSign = (String) parameterMap.get("sign");

        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parameterMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        //把数据库查询出来的签名做加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR); //里面的枚举类是在model里面定义好的
        }

        //调用service方法
        departmentService.save(parameterMap);

        return Result.ok();

    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //将map转换个类型，方便操作

        //医院编号
        String hoscode = (String) parameterMap.get("hoscode");
        //当前页 和 每页记录数
        int page = StringUtils.isEmpty(parameterMap.get("page")) ? 1 : Integer.parseInt((String) parameterMap.get("page"));
        int limit = StringUtils.isEmpty(parameterMap.get("limit")) ? 1 : Integer.parseInt((String) parameterMap.get("limit"));


        //todo:签名校验
        String hospSign = (String) parameterMap.get("sign");
        String signKey = departmentService.getSignKey(hoscode);

        //把数据库查询出来的签名做加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR); //里面的枚举类是在model里面定义好的
        }


        //因为这是一个分页查询，所以我们可以把条件放到一个封装好的对象中
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //调用service中的方法
        Page<Department> pageModel1 = departmentService.findPageDepartment(page,limit,departmentQueryVo);

        return Result.ok(pageModel1);

    }


    //上传医院接口
    @PostMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //将map转换个类型，方便操作

        //获取医院系统传过来的签名
        String hospSign = (String) parameterMap.get("sign");

        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parameterMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        //把数据库查询出来的签名做加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR); //里面的枚举类是在model里面定义好的
        }

        //把空格重新转换回 "+" ，让数据恢复原状
        String logoData = (String) parameterMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        parameterMap.put("logoData",logoData);

        //调用service中的方法
        hospitalService.save(parameterMap);
        return Result.ok();
    }

    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(request.getParameterMap());

        //获取医院系统传过来的签名，并且进行md5加密
        String hospSign = (String) parameterMap.get("sign");

        //根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parameterMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);

        //把数据库查询出来的签名做加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if (!hospSign.equals(signKeyMD5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR); //里面的枚举类是在model里面定义好的
        }

        //调用service方法实现根据医院编号查询
        Hospital hospital = hospitalService.getHosByHoscode(hoscode);

        return Result.ok(hospital);

    }


}
