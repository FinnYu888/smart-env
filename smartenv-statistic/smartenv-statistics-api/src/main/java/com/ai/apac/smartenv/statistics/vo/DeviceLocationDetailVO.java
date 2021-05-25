package com.ai.apac.smartenv.statistics.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/6 4:35 下午
 **/
@Data
public class DeviceLocationDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("对象编号")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long objId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("速度")
    private String speed;

    @ApiModelProperty("平均速度")
    private String avgSpeed;

    @ApiModelProperty("最高速度")
    private String maxSpeed;

    @ApiModelProperty("速度单位")
    private String speedUnit;

    @ApiModelProperty("总里程")
    private String totalDistance;

    @ApiModelProperty("里程单位")
    private String distanceUnit;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("项目编码")
    private String projectCode;

    @ApiModelProperty("工作状态名称")
    private String workStatusName;

    @ApiModelProperty("车辆类型/人员岗位")
    private String objCategoryName;

    @ApiModelProperty("车牌号")
    private String plateNumber;

    @ApiModelProperty("最新未处理告警内容")
    private String lastAlarmContent;

    @ApiModelProperty("未处理告警数量")
    private Integer alarmCount;
}
