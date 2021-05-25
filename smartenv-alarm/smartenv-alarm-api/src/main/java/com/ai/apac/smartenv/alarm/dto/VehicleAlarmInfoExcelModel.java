package com.ai.apac.smartenv.alarm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AlarmInfoExcelExportModel
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/20     zhaidx           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleAlarmInfoExcelModel extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "告警时间" ,index = 0)
    private String alarmTime;

    @ExcelProperty(value = "车辆" ,index = 1)
    private String vehicle;

    @ExcelProperty(value = "告警名称" ,index = 2)
    private String alarmName;

    @ExcelProperty(value = "告警类型" ,index = 3)
    private String alarmType;

    @ExcelProperty(value = "告警级别" ,index = 4)
    private String alarmLevel;

    @ExcelProperty(value = "告警信息" ,index = 5)
    private String alarmMessage;

    @ExcelProperty(value = "处理状态" ,index = 6)
    private String alarmStatus;
    
}
