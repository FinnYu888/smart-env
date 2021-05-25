package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResInfoQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@ApiModel(value = "物品列表查询对象", description = "物品列表查询对象")
public class ResInfoQueryVO extends ResInfoQuery {

    private String inventoryName;

    private String manageStateName;
}
