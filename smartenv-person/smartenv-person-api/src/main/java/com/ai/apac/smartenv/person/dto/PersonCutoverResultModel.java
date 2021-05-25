package com.ai.apac.smartenv.person.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName DeviceCutoverResultModel
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/16 10:20
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonCutoverResultModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;


    @ExcelProperty(value = "序列" ,index = 0)
    private String no;

    @ExcelProperty(value = "工号" ,index = 1)
    private String jobNumber;

    @ExcelProperty(value = "人名" ,index = 2)
    private String personName;

    @ExcelProperty(value = "手机号码" ,index = 3)
    private String mobileNumber;

    @ExcelProperty(value = "设备编码" ,index = 4)
    private String deviceCode;

    @ExcelProperty(value = "状态" ,index = 5)
    private String status;

    @ExcelProperty(value = "原因" ,index = 6)
    private String reason;

}
