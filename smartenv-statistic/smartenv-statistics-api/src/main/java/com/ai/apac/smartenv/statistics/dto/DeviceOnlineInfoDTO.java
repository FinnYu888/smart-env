package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 设备在线率统计
 * @Date 2021/1/20 9:59 上午
 **/
@Data
public class DeviceOnlineInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("人员设备在线数量")
    private Long personDeviceOnline;

    @ApiModelProperty("车辆设备在线数量")
    private Long vehicleDeviceOnline;

    @ApiModelProperty("项目编码")
    private String projectCode;

    @ApiModelProperty("项目名称")
    private String projectName;
}
