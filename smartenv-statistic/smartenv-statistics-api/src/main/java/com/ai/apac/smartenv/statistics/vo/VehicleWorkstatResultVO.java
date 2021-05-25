package com.ai.apac.smartenv.statistics.vo;

import com.ai.apac.smartenv.statistics.entity.VehicleWorkStatResult;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleWorkstatResultVO对象", description = "VehicleWorkstatResult视图对象")
public class VehicleWorkstatResultVO extends VehicleWorkStatResult {
    private static final long serialVersionUID = 1L;

}
