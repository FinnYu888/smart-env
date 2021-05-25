package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetBigScreenDto {


    //    @ApiModelProperty("工作区域ID")
//    private String personWorkareaIds;
//    @ApiModelProperty("工作区域ID")
//    private String vehicleWorkareaIds;
    @ApiModelProperty("租户ID")
    private String tenantId;
    @ApiModelProperty("区域ID")
    private String regionId;
    @ApiModelProperty("实体类型")
    private Long entityType;


    private Long vehicleType;

    private Long personPositionId;

    private Boolean isEasyV;



}
