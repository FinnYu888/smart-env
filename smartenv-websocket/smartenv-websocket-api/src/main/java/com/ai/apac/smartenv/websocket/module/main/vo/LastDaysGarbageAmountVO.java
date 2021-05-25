package com.ai.apac.smartenv.websocket.module.main.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: LastDaysGarbageAmountVO
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:12    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "最近N天垃圾收集吨数对象", description = "最近N天垃圾收集吨数对象")
public class LastDaysGarbageAmountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    String garbageTypeId;

    String garbageTypeName;

    List<GarbageAmountDaily> garbageAmountDailyList;

}
