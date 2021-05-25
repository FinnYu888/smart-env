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
public class PutTrackDTO implements Serializable {

    private static final long serialVersionUID = 6756378141881263943L;

    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    @ApiModelProperty(value = "初始经度")
    private String lng;

    @ApiModelProperty(value = "初始纬度")
    private String lat;

    @ApiModelProperty(value = "速度",hidden = true)
    private String speed;

    @ApiModelProperty(value = "ACC状态")
    private Integer accStatus = 0;
}
