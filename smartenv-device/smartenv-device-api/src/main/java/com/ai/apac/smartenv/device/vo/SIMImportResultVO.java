package com.ai.apac.smartenv.device.vo;
import com.ai.apac.smartenv.device.dto.SIMInfoImportResultModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "", description = "SIM卡导入结果")
public class SIMImportResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int successCount;

    private int failCount;

    private String fileKey;

    private List<SIMInfoImportResultModel> failRecords;

}
