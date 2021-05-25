package com.ai.apac.smartenv.facility.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.ai.apac.smartenv.facility.dto.AshcanImportResultModel;


@Data
@ApiModel(value = "", description = "垃圾桶导入结果")
public class AshcanImportResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int successCount;
    private int failCount;
    private String fileKey;
    private List<AshcanImportResultModel> failRecords;

}
