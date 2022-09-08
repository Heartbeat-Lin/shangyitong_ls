package com.atguigu.yygh.cmn.mapper;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

//让这个接口继承basemapper，用mp
@Mapper
public interface DictMapper extends BaseMapper<Dict> {


}
