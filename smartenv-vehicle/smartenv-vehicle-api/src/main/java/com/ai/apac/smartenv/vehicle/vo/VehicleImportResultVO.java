package com.ai.apac.smartenv.vehicle.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.ai.apac.smartenv.vehicle.dto.VehicleInfoImportResultModel;

@Data
@ApiModel(value = "", description = "车辆导入结果")
public class VehicleImportResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int successCount;
    private int failCount;
    private String fileKey;
    private List<VehicleInfoImportResultModel> failRecords;
    private List<VehicleInfoImportResultModel> allRecords;

}
