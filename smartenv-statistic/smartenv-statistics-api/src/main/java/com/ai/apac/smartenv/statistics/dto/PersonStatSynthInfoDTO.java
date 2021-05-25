package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("人员统计信息")
@Document("PersonStatInfoData")
public class PersonStatSynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String personId;

    private String personCode;

    private String date;

    private String realWorkHours;

    private String realScheduleHours;

    private String realScheduleStatus;

    private String realWorkMiles;

    private String projectCode;

}
