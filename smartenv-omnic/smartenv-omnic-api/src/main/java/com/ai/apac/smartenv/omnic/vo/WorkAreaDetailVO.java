package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WorkAreaDetailsVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/15 14:08
 * @Version 1.0
 */
@Data
@ApiModel(value = "大屏工作区域内信息VO对象", description = "工作区域内信息VO对象")
public class WorkAreaDetailVO implements Serializable {

    private String  workAreaId;

    private String  workAreaName;

    private String areaHead;

    private String areaHeadName;

    private Integer shouldWorkVehicleCount;

    private Integer shouldworkPersonCount;

    private Integer workingVehicleCount;

    private Integer workingPersonCount;

    private Integer workingOffVehicleCount;

    private Integer workingOffPersonCount;

    private Integer alarmCount;

    private Integer eventCount;
}

