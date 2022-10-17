package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {


    List<Dict> findChildData(Long id);

    /**
     *
     * 导出
     * @param response
     */
    void exportData(HttpServletResponse response);


    /**
     *
     * 导入
     * @param file
     */
    public void importDictData(MultipartFile file);


    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
