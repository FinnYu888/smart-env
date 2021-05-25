package com.ai.apac.smartenv.device.dto;

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
public class DeviceCutoverResultModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "序列" ,index = 0)
    private String no;

    @ExcelProperty(value = "设备编码" ,index = 1)
    private String deviceCode1;

    @ExcelProperty(value = "录像机设备编码" ,index = 2)
    private String deviceCode2;

    @ExcelProperty(value = "状态" ,index = 3)
    private String status;

    @ExcelProperty(value = "原因" ,index = 4)
    private String reason;

}
