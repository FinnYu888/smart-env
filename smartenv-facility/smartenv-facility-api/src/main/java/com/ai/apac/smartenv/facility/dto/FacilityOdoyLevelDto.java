package com.ai.apac.smartenv.facility.dto;

import lombok.Data;

import java.io.Serializable;

/**
* 中转站臭味级别
* @author 66578
*/
@Data
public class FacilityOdoyLevelDto implements Serializable {
    public Long facilityId;
    public Float senSorValue= 0F;
}
