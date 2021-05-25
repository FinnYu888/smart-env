package com.ai.apac.smartenv.websocket.module.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author qianlong
 * @description //Websocket定时任务对象,这个任务仅放在redis中
 * @Date 2020/2/16 4:02 下午
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Websocket定时任务对象", description = "Websocket定时任务对象")
public class WebsocketTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private String taskType;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * sessionId
     */
    @ApiModelProperty(value = "sessionId")
    private String sessionId;

    /**
     * 订阅的Topic
     */
    @ApiModelProperty(value = "订阅的Topic")
    private String topic;

    /**
     * 任务执行周期
     */

//    @Deprecated
//    @ApiModelProperty(value = "任务执行周期")
//    private String schedule;

    /**
     * 是否是广播消息
     */
    @ApiModelProperty(value = "是否是广播消息,默认是点对点")
    private boolean isBroadCast = false;

    /**
     * 任务执行参数
     */
    @ApiModelProperty(value = "任务执行参数")
    private Map<String, Object> params;


    /**
     * 任务监听的实体ID
     */
    @ApiModelProperty("任务监听的实体ID")
    private List<String> entityIds;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;




    public WebsocketTask(){

    }

    public WebsocketTask(String taskType, String topic,@Deprecated String schedule, Map<String, Object> params) {
        this.taskType = taskType;
        this.topic = topic;
        this.params = params;
    }
}
