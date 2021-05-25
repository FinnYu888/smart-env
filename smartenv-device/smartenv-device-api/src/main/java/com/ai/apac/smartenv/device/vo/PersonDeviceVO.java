package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * @ClassName PersonDeviceVO
 * @Desc 人员设备VO
 * @Author ZHANGLEI25
 * @Date 2020/2/19 10:45
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonDeviceVO对象", description = "人员设备视图&编辑对象")
public class PersonDeviceVO extends DeviceInfoVO {


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

    @ApiModelProperty(value = "SIM卡号")
    private String simId;

    @ApiModelProperty(value = "SIM卡号码")
    private String simNumber;

    /**
     * ICCID
     */
    @ApiModelProperty(value = "鉴权码")
    private String authCode;

    @ApiModelProperty(value = "坐标ID")
    private String coordId;

    @ApiModelProperty(value = "坐标值")
    private String coordValue;

    @ApiModelProperty(value = "坐标值名称")
    private String coordValueName;

}
