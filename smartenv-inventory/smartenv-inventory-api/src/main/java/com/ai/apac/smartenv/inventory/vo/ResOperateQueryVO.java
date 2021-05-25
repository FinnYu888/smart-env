package com.ai.apac.smartenv.inventory.vo;


import com.ai.apac.smartenv.inventory.entity.ResOperateQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "物品操作记录", description = "物品操作记录")
public class ResOperateQueryVO extends ResOperateQuery {
    private static final long serialVersionUID = 1L;
}
