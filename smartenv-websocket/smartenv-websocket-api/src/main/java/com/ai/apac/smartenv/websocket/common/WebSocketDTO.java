package com.ai.apac.smartenv.websocket.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/15 11:56 上午
 **/
@Data
@ApiModel
public class WebSocketDTO implements Serializable {

    /**
     * 订阅的topic名称
     */
    @ApiModelProperty(value = "订阅的topic名称",hidden = true)
    private String topicName;

    /**
     * 触发的action名称
     */
    @ApiModelProperty(value = "触发的action名称",hidden = true)
    private String actionName;

    /**
     * 推送任务ID
     */
    @ApiModelProperty(value = "推送任务ID",hidden = true)
    private String taskId;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID",hidden = true)
    private String tenantId;

    /**
     * 是否是广播消息
     */
    @ApiModelProperty(value = "是否是广播消息",hidden = true)
    private boolean isBroadCast;
}
