package com.ai.apac.smartenv.omnic.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("根据公司ID查询公司下所有项目的综合数据统计信息")
@Document("SynthInfoData")
public class SynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long allPersonCount;

    private Long personCount;

    private Long workingPersonCount;

    private Long onlinePersonCount;

    private Long allVehicleCount;

    private Long vehicleCount;

    private Long workingVehicleCount;

    private Long onlineVehicleCount;


    private Long facilityCount;

    private Long workingFacilityCount;

    private double personWorkAreaCount;

    private double vehicleWorkAreaCount;

    private String companyId;

    private String projectCode;

    private String projectName;

    private String areaCode;

    private String updateTime;

}
