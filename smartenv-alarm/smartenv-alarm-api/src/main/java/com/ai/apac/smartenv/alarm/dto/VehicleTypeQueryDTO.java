package com.ai.apac.smartenv.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: VehicleTypeQueryDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2021/1/8
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2021/1/8     zhaidx           v1.0.0               修改原因
 */
@Data
public class VehicleTypeQueryDTO implements Serializable {
    private static final long serialVersionUID = -6716151163211746740L;

    /**
     * 告警规则Id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmRuleId;
    /**
     * 告警规则类型Id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmEntityCategoryId;
     /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 标记返回是否作为前端页面上的查询数据
     */
    private Boolean isSearch;
}
