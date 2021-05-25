package com.ai.apac.smartenv.websocket.module.notification.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 全局消息通知
 * @Date 2020/2/14 8:11 下午
 **/
@Data
public class NotificationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "通知内容")
    private String content;

    @ApiModelProperty(value = "通知分类,Alarm/Event/Task/")
    private String category;

    @ApiModelProperty(value = "访问路由")
    private String path;

    @ApiModelProperty(value = "路由类型 1-本系统内路由 2-外部系统链接")
    private Integer pathType;

    @ApiModelProperty(value = "通知等级,Info/Warning")
    private String level;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "租户ID,当isBroadCast=true时必填")
    private String tenantId;

    /**
     * 是否是广播消息
     */
    @ApiModelProperty(value = "是否是广播消息")
    private boolean isBroadCast;
    
    @ApiModelProperty(value = "告警消息Id，事件Id，消息Id")
    private String id;
}
