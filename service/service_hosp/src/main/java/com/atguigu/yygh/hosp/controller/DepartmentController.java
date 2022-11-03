package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin    //controller上面不要忘记这个注解：解决跨域
public class DepartmentController {


    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询医院所有科室列表
    @ApiOperation(value = "查询医院所有列表")
    @GetMapping("getDepList/{hoscode}")
    public Result getDepList(@PathVariable String hoscode){
        //这里选用了departmentVo的类，因为其中包含了科室的下级科室
        List<DepartmentVo> list = departmentService.findDepTree(hoscode);
        return Result.ok(list);
    }

}
