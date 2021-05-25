package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.dto.PersonDeviceImportResultModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PersonDeviceImportResultVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/2 14:59
 * @Version 1.0
 */
@Data
@ApiModel(value = "", description = "人员终端导入结果")
public class PersonDeviceImportResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int successCount;

    private int failCount;

    private String fileKey;

    private List<PersonDeviceImportResultModel> failRecords;

}
