package com.atguigu.yygh.hosp.controller;


import com.atguigu.common.utils.MD5;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {

    //注入Service
    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查询医院设置表所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        //调用service中的方法
        List<HospitalSet> list = hospitalSetService.listByIds(Arrays.asList(1));
        Result<List<HospitalSet>> ok = Result.ok(list);

        return ok;
    }

    //2. 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        return b?Result.ok():Result.fail();
    }

    //3. 条件查询带分页
    @PostMapping("findPageHospSet/{current}/{limit}") //这里注意，get方法不能有请求体，so 有@RequestBody注解时应该用post
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit,
                                  //@RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
                                  @RequestBody HospitalSetQueryVo hospitalSetQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page  = new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname))wrapper.like("hosname", hosname);
        if(!StringUtils.isEmpty(hoscode))wrapper.eq("hoscode", hoscode);
        //调用方法实现分页查询
        Page<HospitalSet> page1 = hospitalSetService.page(page, wrapper);

        return Result.ok(page1);


    }




    //4. 添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }


    //5. 根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
//        try {
//            //模拟异常
//            int a = 1/0;
//        }catch (Exception e){
//            throw  new YyghException("失败",201); //这个异常需要手动抛出
//        }


        HospitalSet byId = hospitalSetService.getById(id);
        return Result.ok(byId);
    }


    //6. 修改医院设置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b){
            return Result.ok();
        } else {
            return Result.fail();
        }
    }


    //7.批量删除医院的设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        boolean b = hospitalSetService.removeByIds(idList);
        if (b){
            return Result.ok();
        } else {
            return Result.fail();
        }

    }

    //8 医院设置的锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,@PathVariable Integer status){

        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //向里面设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);

        return Result.ok();
    }

    //9 发送签名秘钥key
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet byId = hospitalSetService.getById(id);
        String signKey = byId.getSignKey();
        String hoscode = byId.getHoscode();

        return Result.ok();
    }


}
