package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;
import java.util.List;

/**
 * 车辆基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleViewDetailsVO对象", description = "车辆360车辆完整信息对象")
public class VehicleViewDetailsVO extends VehicleInfo {
    private static final long serialVersionUID = 1L;

    String mediaURI;

    String drivingLicenseURI1;

    String drivingLicenseURI2;


    List<VehicleDriverVO> vehicleDriverVOList;

    List<DeviceInfoVO> deviceInfoVOList;

    List<DeviceInfoVO> vcrInfoVOList;


}
