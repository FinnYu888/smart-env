package com.ai.apac.smartenv.vehicle.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VehicleMaintTypeVO implements Serializable {
    private String dictKey;
    private String dictValue;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VehicleMaintTypeVO> chileType;

}
