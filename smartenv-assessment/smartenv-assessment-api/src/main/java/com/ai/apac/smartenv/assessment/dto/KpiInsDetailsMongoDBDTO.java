package com.ai.apac.smartenv.assessment.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName KpiInsDetailsMongoDBDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/20 14:16
 * @Version 1.0
 */
@Data
public class KpiInsDetailsMongoDBDTO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    private Long kpiCatalogId;

    private String kpiCatalogName;

   private List<SingleKpiInsDetailsMongoDBDTO> singleKpiInsDetailsMongoDBDTOList;


}
