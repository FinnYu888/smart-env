package com.ai.apac.smartenv.workarea.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 简单工作区域信息
 * @Date 2021/1/17 7:59 下午
 **/
@Data
public class SimpleWorkAreaInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属大区
     */
    @ApiModelProperty(value = "所属大区")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long division;

    @ApiModelProperty(value = "1路线 2区域")
    private Long areaType;

    /**
     * 工作区域类型
     */
    @ApiModelProperty(value = "1工作 2加油 3加水 4行驶 5维修")
    private Long workAreaType;

    /**
     * 片区负责人
     */
    @ApiModelProperty(value = "片区负责人")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long areaHead;

    /**
     * 绑定类型
     */
    @ApiModelProperty(value = "1人员 2车辆")
    private Long bindType;
}
