package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@Slf4j
//@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    //根据dictcode查询下级结点
    @ApiOperation(value = "根据dictCode获取下级结点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        if (StringUtils.isEmpty(dictCode)){
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        List<Dict> list=dictService.findByDictCode(dictCode);
        log.info("调用了findByDictCode方法");
        for (Dict dict : list) {
            log.info("dict"+dict);
        }
        return Result.ok(list);
    }


    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    @ApiOperation(value="导出")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
       // return Result.ok();
    }

    @ApiOperation(value = "导入")
    @PostMapping("importData")
    public Result importData(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    //根据dictcode值和value值进行查询

    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);

        return dictName;
    }


    //根据value值进行查询
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName("",value);

        return dictName;
    }



}
