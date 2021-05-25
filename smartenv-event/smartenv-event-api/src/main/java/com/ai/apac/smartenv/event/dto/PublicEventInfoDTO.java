package com.ai.apac.smartenv.event.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 8:37 下午
 **/
@Data
@ApiModel
public class PublicEventInfoDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "事件信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;

    /**
     * 事件涉及的工作区域
     */
    @ApiModelProperty(value = "事件涉及的工作区域")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workareaId;
    /**
     * 事件涉及的具体地址描述
     */
    @ApiModelProperty(value = "事件涉及的具体地址描述")
    private String eventAddress;
    /**
     * 事件上报人ID
     */
    @ApiModelProperty(value = "事件上报人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String reportPersonId;
    /**
     * 事件上报人名称
     */
    @ApiModelProperty(value = "事件上报人名称")
    private String reportPersonName;

    /**
     * 事件上报人联系方式
     */
    @ApiModelProperty(value = "事件上报人联系方式")
    private String reportPersonPhone;

    /**
     * 事件处理人ID
     */
    @ApiModelProperty(value = "事件处理人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String handlePersonId;
    /**
     * 事件处理人名称
     */
    @ApiModelProperty(value = "事件处理人名称")
    private String handlePersonName;

    /**
     * 事件描述
     */
    @ApiModelProperty(value = "事件描述")
    private String eventDesc;

    /**
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见")
    private String handleAdvice;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitudinal;

    /**
     * 所属区域
     */
    @ApiModelProperty(value = "所属区域")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long belongArea;

    /**
     * 所属区域名称
     */
    @ApiModelProperty(value = "所属区域名称")
    private String belongAreaName;

    @ApiModelProperty(value = "所属城市ID")
    private Long cityId;

    @ApiModelProperty(value = "所属城市名称")
    private String cityName;

    @ApiModelProperty(value = "上传的附件列表")
    private List<EventMediumDTO> eventMediumList;
}
