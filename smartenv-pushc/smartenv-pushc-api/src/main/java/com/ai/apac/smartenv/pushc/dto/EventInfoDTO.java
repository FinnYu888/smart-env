package com.ai.apac.smartenv.pushc.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 环卫事件对象
 * @Date 2020/10/13 5:10 下午
 **/
@Data
public class EventInfoDTO implements Serializable {

    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "事件基本信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 事件标题
     */
    @ApiModelProperty(value = "事件标题")
    private String eventTitle;

    /**
     * 事件上报人
     */
    @ApiModelProperty(value = "事件上报人")
    private String reporter;

    /**
     * 事件责任人
     */
    @ApiModelProperty(value = "事件责任人")
    private String principal;

    /**
     * 事件报告时间
     */
    @ApiModelProperty(value = "事件报告时间")
    private String reportTime;

    /**
     * 事件截止完成时间
     */
    @ApiModelProperty(value = "事件截止完成时间")
    private String deadline;

    /**
     * 事件状态
     */
    @ApiModelProperty(value = "事件状态")
    private String statusName;

    /**
     * 事件状态
     */
    @ApiModelProperty(value = "事件发生地址")
    private String address;
}
