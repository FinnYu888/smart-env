package com.ai.apac.smartenv.websocket.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 获取人员位置请求
 * @Date 2020/2/26 3:58 下午
 **/
@Data
public class GetPersonPositionDTO implements Serializable {

    private static final long serialVersionUID = -3513145283505396994L;

    @ApiModelProperty("人员ID")
    private String personIds;

    @ApiModelProperty("车辆状态")
    private Integer status;

    @ApiModelProperty("工作区域ID")
    private String workareaIds;

    @ApiModelProperty("区域ID")
    private String regionId;

    @ApiModelProperty("人员姓名")
    private String personName;

}
