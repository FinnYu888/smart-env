package com.ai.apac.smartenv.assessment.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName StaffKpiInsModel
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/1 14:07
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffKpiInsModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "人员" ,index = 0)
    private String staffName;

    @ExcelProperty(value = "部门" ,index = 1)
    private String deptName;

    @ExcelProperty(value = "岗位" ,index = 2)
    private String stationName;

    @ExcelProperty(value = "考核目标" ,index = 3)
    private String kpiTargetName;

    @ExcelProperty(value = "考核开始时间" ,index = 4)
    private String startTime;

    @ExcelProperty(value = "考核结束时间" ,index = 5)
    private String endTime;

    @ExcelProperty(value = "考核状态" ,index = 6)
    private String statusName;

    @ExcelProperty(value = "综合总分" ,index = 7)
    private String totalScore;

    @ExcelProperty(value = "评分等级" ,index = 8)
    private String grade;

}
