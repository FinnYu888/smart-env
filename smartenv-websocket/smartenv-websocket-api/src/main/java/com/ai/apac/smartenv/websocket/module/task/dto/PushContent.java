package com.ai.apac.smartenv.websocket.module.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 推送到客户端的内容对象
 * @Date 2020/2/16 4:18 下午
 **/
@Getter
@Setter
@ApiModel(value = "推送到客户端的内容", description = "推送到客户端的内容")
public class PushContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端sessionId
     */
    @ApiModelProperty(value = "客户端sessionId")
    private String sessionId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private String taskType;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**
     * 推送到客户端的内容
     */
    @ApiModelProperty(value = "推送到客户端的内容")
    private String content;
}
