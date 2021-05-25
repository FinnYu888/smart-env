package com.ai.apac.smartenv.address.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleTrackModel
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/2
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/2  21:34    panfeng          v1.0.0             修改原因
 */
@Data
public class PersonTrackModelVO  extends BaseRowModel implements Serializable {

    @ExcelProperty(value = "经度" ,index = 0)
    private String lng;
    @ExcelProperty(value = "纬度" ,index = 1)
    private String lat;
    @ExcelProperty(value = "速度" ,index = 2)
    private Long speed;
    @ExcelProperty(value = "时间" ,index = 3)
    private String eventTime;
    @ExcelProperty(value = "acc状态名称" ,index = 4)
    private String accStatuslable;
    @ExcelProperty(value = "位置上报时间" ,index = 5)
    private String lastOnlineTime;
//    @ExcelProperty(value = "工作状态" ,index = 7)
//    private String workStatus;
    @ExcelProperty(value = "地址" ,index = 6)
    private String address;
    @ExcelProperty(value = "距离" ,index = 7)
    private Long distance;



}
