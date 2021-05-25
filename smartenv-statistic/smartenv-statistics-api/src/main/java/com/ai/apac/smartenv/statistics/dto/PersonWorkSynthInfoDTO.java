package com.ai.apac.smartenv.statistics.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ApiModel("人员作业信息")
@Document("PersonWorkInfoData")
public class PersonWorkSynthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String personId;//人员ID

    private String personCode;//人员编码

    private String workDate;//作业时期

    private String operationRate;//工作完成率

    private String updateTime;//更新时间

    private String projectCode;
}
