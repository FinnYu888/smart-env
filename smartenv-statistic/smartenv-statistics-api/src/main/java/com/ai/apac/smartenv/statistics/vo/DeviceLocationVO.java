package com.ai.apac.smartenv.statistics.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 查询
 * @Date 2021/1/6 12:50 下午
 **/
@Data
public class DeviceLocationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("设备ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;

    @ApiModelProperty("设备实体类型 1-车辆 2-人员")
    private Integer deviceObjType;

    @ApiModelProperty("设备实体名称")
    private String objName;

    @ApiModelProperty("纬度")
    private Double lat;

    @ApiModelProperty("经度")
    private Double lng;

    @ApiModelProperty("格式为设备ID-设备实体类型")
    private String groupId;

    /**
     * 车辆工作所在片区
     */
    @ApiModelProperty(value = "设备状态")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    @Indexed
    private Long deviceStatus;

    @ApiModelProperty(value = "工作状态")
    @Indexed
    private Integer workStatus;

    @ApiModelProperty(value = "工作状态名称")
    private String workStatusName;

    @ApiModelProperty(value = "告警状态 1-正常 2-有告警")
    private Integer alarmStatus;
}
