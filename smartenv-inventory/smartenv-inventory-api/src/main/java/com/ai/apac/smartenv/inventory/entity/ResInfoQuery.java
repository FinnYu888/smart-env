package com.ai.apac.smartenv.inventory.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

@Data
@ApiModel(value = "ResInfo查询列表对象", description = "ResInfo查询列表对象")
public class ResInfoQuery extends TenantEntity {

    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resType;
    private String typeName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resSpecId;
    private String  specName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryId;
    private Integer amount;
    private String unit;
    private String manageState;
}
