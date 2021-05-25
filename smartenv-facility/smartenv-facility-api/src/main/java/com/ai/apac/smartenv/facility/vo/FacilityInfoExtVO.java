package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityInfoVO对象", description = "FacilityInfoVO对象")
public class FacilityInfoExtVO extends FacilityInfo {
    private static final long serialVersionUID = 1L;
    private String garbageWeight;
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Integer transTimes ;
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private String odorLevel;
    private String tranStationModel;//中转站规模
    private String statusName;//中转站状态
    List<FacilityExtVO> facilityExtVOList;
}
