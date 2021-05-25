package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResTypeSpec;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ResTypeSpecVO对象", description = "RResTypeSpecVO对象")
public class ResTypeSpecVO extends ResTypeSpec {
}
