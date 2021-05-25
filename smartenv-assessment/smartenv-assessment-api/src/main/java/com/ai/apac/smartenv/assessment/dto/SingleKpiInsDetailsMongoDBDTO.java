package com.ai.apac.smartenv.assessment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SingleKpiInsDetailsMongoDBDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/21 16:33
 * @Version 1.0
 */
@Data
public class SingleKpiInsDetailsMongoDBDTO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long kpiTplDetailId;

    private String kpiName;

    private String kpiDesc;

    private String appraisalCriteria;

    private Integer weighting;

    private String score;

    private String staffRemark;

    private String managerRemark;


}

