package com.ai.apac.smartenv.websocket.module.main.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: GarbageAmountDaily
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:13    zhanglei25          v1.0.0             修改原因
 */
@Data
@ApiModel(value = "每日垃圾总吨数", description = "每日垃圾总吨数")
public class GarbageAmountDaily implements Serializable {
    private static final long serialVersionUID = 1L;

    private String garbageType;

    private String garbageAmount;

    private String transferDate;
}
