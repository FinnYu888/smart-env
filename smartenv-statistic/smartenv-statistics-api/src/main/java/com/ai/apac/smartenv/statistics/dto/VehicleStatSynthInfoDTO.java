package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("车辆统计信息")
@Document("VehicleStatInfoData")
public class VehicleStatSynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vehicleId;

    private String vehicleCode;

    private String date;

    private String realWorkHours;

    private String realScheduleHours;

    private String realWorkMiles;

    private String oilConsumption;

    private String avgOilConsumption;

    private String alarmTimes;

    private String projectCode;
}
