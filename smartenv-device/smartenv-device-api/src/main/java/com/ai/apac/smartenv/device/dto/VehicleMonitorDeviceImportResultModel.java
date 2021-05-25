package com.ai.apac.smartenv.device.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName PersonDeviceImportResultModel
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/2 15:00
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleMonitorDeviceImportResultModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "设备编码" ,index = 0)
    private String deviceCode;

    @ExcelProperty(value = "设备名称" ,index = 1)
    private String deviceName;

    @ExcelProperty(value = "设备型号" ,index = 2)
    private String deviceType;

    @ExcelProperty(value = "设备厂家" ,index = 3)
    private String deviceFactory;

    @ExcelProperty(value = "设备分类" ,index = 4)
    private String entityCategoryId;

    @ExcelProperty(value = "SIM卡号" ,index = 5)
    private String simCode;

    @ExcelProperty(value = "通道1" ,index = 6)
    private String channel1;

    @ExcelProperty(value = "通道2" ,index = 7)
    private String channel2;

    @ExcelProperty(value = "通道3" ,index = 8)
    private String channel3;

    @ExcelProperty(value = "通道4" ,index = 9)
    private String channel4;

    @ExcelProperty(value = "通道5" ,index = 10)
    private String channel5;

    @ExcelProperty(value = "通道6" ,index = 11)
    private String channel6;

    @ExcelProperty(value = "通道7" ,index = 12)
    private String channel7;

    @ExcelProperty(value = "通道8" ,index = 13)
    private String channel8;

    @ExcelProperty(value = "状态" ,index = 14)
    private String status;

    @ExcelProperty(value = "原因" ,index = 15)
    private String reason;

}
