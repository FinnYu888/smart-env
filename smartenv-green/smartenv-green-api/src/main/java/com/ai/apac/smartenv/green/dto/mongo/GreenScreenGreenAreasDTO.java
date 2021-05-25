package com.ai.apac.smartenv.green.dto.mongo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GreenScreenGreenAreasDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:47
 * @Version 1.0
 */
@Data
public class GreenScreenGreenAreasDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String tenantId;
    String totalArea;
    String totalGreenArea;
    String totalGreenPer;
    String totalTreeNum;
    String totalLawnArea;

    List<GreenScreenGreenAreaDTO> greenAreaList;

}
