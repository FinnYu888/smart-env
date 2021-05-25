package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/18 4:34 下午
 **/
@Slf4j
public class WorkAreaServiceImpl {

    public void readWorkAreaInfo(String filePath) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath), "作业区域同步");
        List<Map<String, Object>> readAll = reader.readAll();
        List<String> sqlList = new ArrayList<>();
        String sqlTpl = "update ai_workarea_info set area_level = {},width = {}, area_head = {},division={} where area_name = '{}';";
        readAll.stream().forEach(workAreaInfo -> {
            Long areaLevel = (Long) workAreaInfo.get("路段等级");
            Long width = (Long) workAreaInfo.get("路宽(米)");
            String areaName = (String) workAreaInfo.get("作业区域名称");
            String areaHead = "1336259270354788354";
            String division = "1336270026443677700";
            String str = StrUtil.format(sqlTpl, areaLevel, width, areaHead, division, areaName);
            System.out.println(str);
            sqlList.add(str);
        });

    }

    /**
     * 根据文件更新区域宽度
     *
     * @param filePath
     */
    public void updateAreaWidth(String filePath) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath), "Sheet1");
        List<Map<String, Object>> readAll = reader.readAll();
        List<String> sqlList = new ArrayList<>();
        String sqlTpl = "update ai_workarea_info set width = {} where id = {};";
        readAll.stream().forEach(workAreaInfo -> {
            String id = (String) workAreaInfo.get("主键");
            String width = (String) workAreaInfo.get("宽度");
            String str = StrUtil.format(sqlTpl, width, id);
            System.out.println(str);
            sqlList.add(str);
        });
    }

//    public static void main(String args[]) throws Exception {
//        String workAreaFile = " /Users/qianlong/SVN/smartenv/DOC/16产品设计/01需求收集/沧州台账数据/无路线宽度数据.xlsx";
//        WorkAreaServiceImpl workAreaService = new WorkAreaServiceImpl();
////        workAreaService.readWorkAreaInfo(workAreaFile);
//        workAreaService.updateAreaWidth(workAreaFile);
//    }
}
