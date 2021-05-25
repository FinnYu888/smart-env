package com.ai.apac.smartenv.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
@ApiModel(value = "BigScreenInfoVO对象", description = "BigScreenInfoVO对象")
public class BigScreenInfoVO {

    @ApiModelProperty(value = "片区名称")
    private String regionName;

    @ApiModelProperty(value = "事件数量")
    private Integer eventCount;


    @ApiModelProperty(value = "车辆数量")
    private Integer vehicleCount;


    @ApiModelProperty(value = "人员数量")
    private Integer personCount;


    @ApiModelProperty(value = "告警数量")
    private Integer alarmCount;
}
