package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("第三方告警信息")
@Document("AlarmSynthInfoData")
public class AlarmSynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alarmTime;

    private String dataContent;

    private String alarmLevel;

    private String alarmType;

    private String lat;

    private String lng;

    private String address;

    private String entityType;

    private String entityCode;

    private String entityId;

    private String regionCode;

    private String regionId;

    private String projectCode;

}
