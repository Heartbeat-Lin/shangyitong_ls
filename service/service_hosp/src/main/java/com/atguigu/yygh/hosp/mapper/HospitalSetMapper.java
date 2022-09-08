package com.atguigu.yygh.hosp.mapper;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;

//让这个接口继承basemapper，用mp
@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {


}
