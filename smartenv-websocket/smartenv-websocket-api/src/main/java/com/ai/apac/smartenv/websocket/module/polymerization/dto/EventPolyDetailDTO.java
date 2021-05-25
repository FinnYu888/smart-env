package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import com.ai.apac.smartenv.event.vo.ButtonsVO;
import com.ai.apac.smartenv.event.vo.EventAssignedHistoryVO;
import com.ai.apac.smartenv.event.vo.EventMediumVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: EventPolyDetailDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/17
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/17     zhaidx           v1.0.0               修改原因
 */
@Data
public class EventPolyDetailDTO extends BasicPolymerizationDetailDTO {
    private static final long serialVersionUID = 1810995830547589590L;

    private List<EventAssignedHistoryVO> assignedHistoryVOS;

    /**
     * 整改前照片
     */
    @ApiModelProperty(value = "整改前照片")
    private List<EventMediumVO> preEventMediumList;

    /**
     * 整改后照片
     */
    @ApiModelProperty(value = "整改后照片")
    private List<EventMediumVO> afterEventMediumList;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventTypeName;
    /**
     * 事件检查类型
     */
    @ApiModelProperty(value = "事件检查类型")
    private String eventInspectTypeName;
    /**
     * 事件等级
     */
    @ApiModelProperty(value = "事件等级")
    private String eventLevelName;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String statusName;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String workareaName;

    @ApiModelProperty(value = "业务区域主管名称")
    private String workAreaManageName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    /**
     * 按钮列表
     */
    private List<ButtonsVO> buttons;

    @ApiModelProperty(value = "事件基本信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;
    /**
     * 事件检查类型
     */
    @ApiModelProperty(value = "事件检查类型")
    private String eventInspectType;
    /**
     * 事件等级
     */
    @ApiModelProperty(value = "事件等级")
    private String eventLevel;
    /**
     * 事件涉及的工作区域
     */
    @ApiModelProperty(value = "事件涉及的工作区域")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
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
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long reportPersonId;
    /**
     * 事件上报人名称
     */
    @ApiModelProperty(value = "事件上报人名称")
    private String reportPersonName;
    /**
     * 事件处理人ID
     */
    @ApiModelProperty(value = "事件处理人ID")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
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
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long belongArea;

    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段")
    private String ext1;

    /**
     * 所属区域名称
     */
    @ApiModelProperty(value = "所属区域名称")
    private String belongAreaName;

    @ApiModelProperty(value = "上报地址")
    private String location;
}
