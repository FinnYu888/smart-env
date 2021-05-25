package com.ai.apac.smartenv.event.dto.mongo;

import com.ai.apac.smartenv.event.vo.CcPeopleVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class EventInfoMongoDto implements Serializable {
    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "事件基本信息表主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long eventInfoId;
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

    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 抄送人员
     */
    @ApiModelProperty("抄送人员")
    private List<CcPeopleVO> ccPeopleVOS;
}
