package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;

@Data
public class VehiclePolymerizationDetailDTO extends BasicPolymerizationDetailDTO{

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;


    @ApiModelProperty(value = "驾驶员")
    private String driver;


    @ApiModelProperty(value = "ACC状态,0 无信息,1 开启,2 正常关闭,3 异常关闭")
    private Long deviceStatus;
    @ApiModelProperty(value = "ACC状态,0 无信息,1 开启,2 正常关闭,3 异常关闭")
    private String deviceStatusName;

    @ApiModelProperty(value = "车辆大类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private String kindCode;


    /**
     * 总里程
     */
    @ApiModelProperty(value = "总里程")
    private String totalDistance;

    @ApiModelProperty(value = "实时速度")
    private String speed;


    @ApiModelProperty(value = "工作状态")
    @Indexed
    private Integer workStatus;

    @ApiModelProperty(value = "工作状态名称")
    private String workStatusName;



    @ApiModelProperty(value = "最新告警内容")
    private String lastAlarmContent;


}
