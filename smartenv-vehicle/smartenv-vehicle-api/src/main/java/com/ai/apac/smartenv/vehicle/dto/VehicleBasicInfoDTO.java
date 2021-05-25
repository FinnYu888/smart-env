package com.ai.apac.smartenv.vehicle.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VehicleBasicInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String plateNumber;

    private String entityCategoryId;

    private String entityCategoryName;


    private String tenantId;

    private String tenantName;
}
