package com.ai.apac.smartenv.assessment.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName KpiInsMongoDBDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/20 14:04
 * @Version 1.0
 */
@Data
public class KpiInsMongoDBDTO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @Indexed
    private Long kpiTargetId;

    private String tenantId;

    private Long kpiTplId;

    private String kpiTplName;

    private String totalScore;

    private String scoreType;

    private String scoreTypeName;

    private List<KpiInsDetailsMongoDBDTO> kpiInsDetailsMongoDBDTOList;


}
