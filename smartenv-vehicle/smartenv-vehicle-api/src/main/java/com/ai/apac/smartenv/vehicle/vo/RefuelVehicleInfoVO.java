package com.ai.apac.smartenv.vehicle.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "RefuelVehicleInfoVO对象", description = "选择加油车辆的id与车牌")
public class RefuelVehicleInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
        private Long id;
        private String plateNumber;
}
