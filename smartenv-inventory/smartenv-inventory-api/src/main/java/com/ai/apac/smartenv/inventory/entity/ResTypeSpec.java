package com.ai.apac.smartenv.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ResTypeSpec对象", description = "ResTypeSpec对象")
public class ResTypeSpec extends TenantEntity {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentTypeId;
    private String typeName;
    private String description;

    private List<ResSpec> resSpecs;
}
