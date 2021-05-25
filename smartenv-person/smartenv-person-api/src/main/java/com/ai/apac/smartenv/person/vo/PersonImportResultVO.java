package com.ai.apac.smartenv.person.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.ai.apac.smartenv.person.dto.PersonImportResultModel;

@Data
@ApiModel(value = "", description = "人员导入结果")
public class PersonImportResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int successCount;
    private int failCount;
    private String fileKey;
    private List<PersonImportResultModel> failRecords;
    private List<PersonImportResultModel> allRecords;

}
