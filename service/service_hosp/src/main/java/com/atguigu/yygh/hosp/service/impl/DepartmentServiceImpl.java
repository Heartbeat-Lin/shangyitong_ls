package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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



    //根据医院编号，查询医院所有科室列表
    @Override
    public List<DepartmentVo> findDepTree(String hoscode) {
        //创建list集合，用于最终的数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> of = Example.of(departmentQuery);//构建查询对象
        List<Department> departmentList = departmentRepository.findAll(of);

        //根据大科室编号 bigcode分组， 获取每个大科室里面下级子科室
        Map<String, List<Department>> departmentMap =   //这一行和下面一行是根据java8新特性数据流写的
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //遍历map集合departmentMap
        for (Map.Entry<String,List<Department>> entry: departmentMap.entrySet()){

            //大科室编号
            String bigcode = entry.getKey();

            //大科室编号对应的全局数据
            List<Department> departmentList1 = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(departmentList1.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList();
            for (Department department : departmentList1) {
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合当中
                children.add(departmentVo2);
            }

            //把小科室list集合放到大科室children当中
            departmentVo1.setChildren(children);
            result.add(departmentVo1);
        }


        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            return department.getDepname();
        }
        return null;
    }

}
