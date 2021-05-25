package com.ai.apac.smartenv.websocket.module.main.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: ResOrder4HomeVO
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
@ApiModel(value = "首页显示的ResOrderVO对象", description = "首页显示的ResOrderVO对象")
public class ResOrder4HomeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long custId;

    private String custName;

    private String businessType;

    private String workflowId;

    private String description;

    private Integer orderStatus;

    private String relUserId;

    private String relUserName;

    private String orderStatusName;

    private String resTypeName;

    private String resSpecName;
}
