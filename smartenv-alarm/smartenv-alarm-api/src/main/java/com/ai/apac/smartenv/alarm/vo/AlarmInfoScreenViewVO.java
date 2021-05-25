package com.ai.apac.smartenv.alarm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName AlarmInfoScreenViewVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/21 11:01
 * @Version 1.0
 */
@Data
@ApiModel(value = "大屏实时告警信息监控VO对象", description = "大屏实时告警信息监控VO对象")
public class AlarmInfoScreenViewVO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @ApiModelProperty(value = "告警时间")
    private String alarmTime;

    @ApiModelProperty(value = "告警对象")
    private String alarmEntity;

    @ApiModelProperty(value = "告警信息")
    private String alarmMessage;

    @ApiModelProperty(value = "告警区域ID")
    private String alarmRegion;

    @ApiModelProperty(value = "告警区域")
    private String alarmRegionName;

    @ApiModelProperty(value = "告警类型名称")
    private String alarmTypeName;

}
