package com.ai.apac.smartenv.websocket.module.mock.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/10 5:50 下午
 **/
@ApiModel
@Data
public class PutTrackTaskDTO extends PutTrackDTO implements Serializable {

    private static final long serialVersionUID = 6756378141881263943L;

    @ApiModelProperty("间隔时间,单位是秒")
    private Integer interval;

    @ApiModelProperty("持续时间,单位是分钟")
    private Integer duration;

}
