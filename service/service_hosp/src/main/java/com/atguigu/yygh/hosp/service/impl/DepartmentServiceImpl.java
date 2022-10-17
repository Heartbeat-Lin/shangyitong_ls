package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>  implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;


   //实现这个save方法就跟上次的上传方法类似
    @Override
    public void save(Map<String, Object> parameterMap) {

        //把map转成department对象
        String  parameterMapStr = JSONObject.toJSONString(parameterMap);
        Department department = JSONObject.parseObject(parameterMapStr, Department.class);


        //根据医院编号和科室编号进行查询
        Department exist =  departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        if (null!=exist){
            exist.setUpdateTime(new Date());
            exist.setIsDeleted(0);
            departmentRepository.save(exist);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

    @Override
    public String getSignKey(String hoscode) {
        //根据传递过来的医院编码，查询数据库，查询签名
        //这里做的操作就是查询mysql数据库，查到mysql中的签名
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //这下面的方法是操作mongodb的
        //创建pageable对象，设置当前页和每页记录数
        // 0是第一页
        Pageable pageable = PageRequest.of(page - 1, limit);

        //创建example对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department,matcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;


    }

    //删除医院接口
    @Override
    public void remove(String hoscode,String depcode) {
        //根据医院编号和科室编号 将department查询出来
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);

        //根据id删除
        if (null!=department){
            departmentRepository.deleteById(department.getId());
        }
    }

}
