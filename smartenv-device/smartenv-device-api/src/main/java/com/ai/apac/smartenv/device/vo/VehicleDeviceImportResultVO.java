package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.dto.VehicleMonitorDeviceImportResultModel;
import com.ai.apac.smartenv.device.dto.VehicleSensorDeviceImportResultModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PersonDeviceImportResultVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/2 14:59
 * @Version 1.0
 */
@Data
@ApiModel(value = "", description = "车辆终端导入结果")
public class VehicleDeviceImportResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int successCount;

    private int failCount;

    private String fileKey1;

    private String fileKey2;

    private List<VehicleMonitorDeviceImportResultModel> failRecords1;

    private List<VehicleSensorDeviceImportResultModel> failRecords2;

}
