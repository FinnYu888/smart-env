package com.ai.apac.smartenv.websocket.common;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/15 4:53 下午
 **/
@Data
public class PositionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("经度")
    @Longitude
    private String lng;

    @ApiModelProperty("纬度")
    @Latitude
    private String lat;

    @ApiModelProperty("时间,yyyymmddhhmmss")
    private String time;

    @ApiModelProperty("时间戳,单位是秒")
    private Long timestamp;

    @ApiModelProperty("速度,单位是km/h")
    private String speed;
}
