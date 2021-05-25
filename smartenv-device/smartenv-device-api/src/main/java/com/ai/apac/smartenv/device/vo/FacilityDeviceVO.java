package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * zhaidx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityDeviceVO对象", description = "设施设备视图&编辑对象")
public class FacilityDeviceVO extends DeviceInfoVO {


    @ApiModelProperty(value = "厂家名称")
    private String deviceFactoryName;
    /**
     * 实体类型名称
     */
    private String entityCategoryName;

    /**
     * SIM卡号
     */
    @ApiModelProperty(value = "SIM卡号")
    private String sim;

    @ApiModelProperty(value = "SIMID")
    private String simId;

    @ApiModelProperty(value = "SIM卡号码")
    private String simNumber;

    /**
     * ICCID
     */
    @ApiModelProperty(value = "鉴权码")
    private String authCode;

    private List<DeviceChannel> deviceChannelList;

}
