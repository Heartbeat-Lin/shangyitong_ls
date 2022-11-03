package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@CrossOrigin
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {


    @Autowired
    private HospitalService hospitalService;


    //查询医院列表（条件查询带分页）
    @GetMapping("/list/{page}/{limit}")
    public Result ListHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){


        Page<Hospital> pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);

        return Result.ok(pageModel);
    }

    //更新上线状态
    @ApiOperation(value = "更新医院上线状态")
    @GetMapping("/updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){

        hospitalService.updateStatus(id,status);
        return Result.ok();
    }


    //医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        //通过mongodb查出信息; 用map返回值更容易后续的取值
        Map<String,Object> hospital = hospitalService.getHospById(id);
        return Result.ok(hospital);
    }


}
