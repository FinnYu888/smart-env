package com.ai.apac.smartenv.device.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleInfoImportResultModel.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月21日 下午3:10:12
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月21日     zhaoaj           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SIMInfoImportResultModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "SIM卡类型" ,index = 0)
    private String simType;

    @ExcelProperty(value = "SIM卡编码" ,index = 1)
    private String simCode;

    @ExcelProperty(value = "SIM卡电话号码" ,index = 2)
    private String simNumber;

    @ExcelProperty(value = "设备编码" ,index = 3)
    private String deviceCode;

    @ExcelProperty(value = "备注" ,index = 4)
    private String remark;

    @ExcelProperty(value = "状态" ,index = 4)
    private String status;

    @ExcelProperty(value = "原因" ,index = 5)
    private String reason;
}

