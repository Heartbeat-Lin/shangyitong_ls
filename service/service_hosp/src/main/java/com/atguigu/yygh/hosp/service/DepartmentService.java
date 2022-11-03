package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {

    void save(Map<String, Object> parameterMap);

    String getSignKey(String hoscode);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode,String depcode);

    List<DepartmentVo> findDepTree(String hoscode);

    String getDepName(String hoscode, String depcode);
}
