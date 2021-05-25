package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.dto.ResInfoPutInStorageExcelModelDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ResInfoPutInStorageExcelResultModelVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/7/14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/14     zhaidx           v1.0.0               修改原因
 */
@Data
@ApiModel(value = "资源入库请求对象处理结果", description = "资源入库请求对象处理结果")
public class ResInfoPutInStorageExcelResultModelVO implements Serializable {
    private static final long serialVersionUID = -3885595906129290949L;

    private int successCount;

    private int failCount;

    private String fileKey;

    private List<ResInfoPutInStorageExcelModelDTO> failRecords;
}
