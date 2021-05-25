package com.ai.apac.smartenv.facility.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;

/**
 * @ClassName GarbageAmountDaily
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/23 15:07
 * @Version 1.0
 */
@Data
@ApiModel(value = "每日垃圾总吨数", description = "每日垃圾总吨数")
public class GarbageAmountDaily implements Serializable {
    private static final long serialVersionUID = 1L;

    private String garbageType;

    private String garbageAmount;

    private String transferDate;
}
