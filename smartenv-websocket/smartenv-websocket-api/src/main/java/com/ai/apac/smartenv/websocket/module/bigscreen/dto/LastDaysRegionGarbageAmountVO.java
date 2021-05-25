package com.ai.apac.smartenv.websocket.module.bigscreen.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: LastDaysRegionGarbageAmountVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  10:56    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "最近N天某区域垃圾收集总数统计", description = "最近N天某区域垃圾收集总数统计")
public class LastDaysRegionGarbageAmountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    String regionId;

    String regionName;

    String garbageAmount;

}
