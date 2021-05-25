package com.ai.apac.smartenv.statistics.vo;

import com.ai.apac.smartenv.statistics.entity.RptVehicleStay;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleDistanceInfoVO对象", description = "VehicleDistanceInfo视图对象")
public class VehicleDistanceInfoVO extends VehicleDistanceInfo {
    private static final long serialVersionUID = 1L;

}