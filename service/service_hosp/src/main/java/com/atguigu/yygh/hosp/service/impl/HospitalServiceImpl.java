package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> parameterMap) {

        //把参数map集合转换对象 hospital(先转成string 再转成想要的对象
        String mapString = JSONObject.toJSONString(parameterMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);


        //如果不存在，进行添加
        if (null!=hospitalExist){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospital.setId(hospitalExist.getId());  //这一行是看评论加的
            hospitalRepository.save(hospital);
        }else{
            //如果存在, 进行修改
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            //hospital.setId(hospitalExist.getId());  //这一行是看评论加的
            hospitalRepository.save(hospital);

        }



    }

    @Override
    public Hospital getHosByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {


        //从第0页开始
        Pageable pageable = PageRequest.of(page-1,limit);
        //创建条件构造器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //vo对象转换成hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital, exampleMatcher);
        //调用方法实现查询
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);

        //添加引入feign之后内容

        //获取查询list集合，遍历进行医院等级封装
        all.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });


        return all;


    }

    //这里修改的是mongodb中的值
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值

        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String,Object> result = new HashMap<>();

        Hospital hospital = hospitalRepository.findById(id).get();

        //进行封装(医院基本信息：包含医院等级）
        Hospital hospital1 = this.setHospitalHosType(hospital);

        result.put("hospital",hospital1);

        //单独处理更加直观
        result.put("bookingRule",hospital.getBookingRule());

        //不需要重复返回
        hospital.setBookingRule(null);

        return result;
    }

    @Override
    public String getHosName(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode==null? null:hospitalByHoscode.getHosname();
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> map = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getHosByHoscode(hoscode));
        map.put("hospital",hospital);
        //预约规则
        map.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);

        return map;
    }

    //获取查询list集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());

        //查询省 市 地区
        String province = dictFeignClient.getName(hospital.getProvinceCode());
        String city = dictFeignClient.getName(hospital.getCityCode());
        String district = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("addressString",province+city+district);
        return hospital;

    }


}
