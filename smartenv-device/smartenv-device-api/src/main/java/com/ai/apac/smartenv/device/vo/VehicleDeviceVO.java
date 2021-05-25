package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.List;

/**
 * @ClassName VehicleDeviceVO
 * @Desc 车辆设备VO
 * @Author ZHANGLEI25
 * @Date 2020/2/19 10:45
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleDeviceVO对象", description = "车辆设备视图&编辑对象")
public class VehicleDeviceVO extends DeviceInfoVO {


    @ApiModelProperty(value = "厂家名称")
    private String deviceFactoryName;

    /**
     * SIM卡号
     */
    @ApiModelProperty(value = "SIM卡号")
    private String sim;

    @ApiModelProperty(value = "SIM卡号码")
    private String simNumber;

    @ApiModelProperty(value = "SIM ID")
    private String simId;


    /**
     * ICCID
     */
    @ApiModelProperty(value = "鉴权码")
    private String authCode;

    private String deviceChannelNumber;

    private String relEntityId;

    private List<DeviceChannel> deviceChannelList;

    @ApiModelProperty(value = "坐标ID")
    private String coordId;

    @ApiModelProperty(value = "坐标值")
    private String coordValue;

    @ApiModelProperty(value = "坐标值名称")
    private String coordValueName;

}
