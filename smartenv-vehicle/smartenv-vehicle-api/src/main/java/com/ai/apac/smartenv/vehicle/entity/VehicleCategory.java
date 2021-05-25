package com.ai.apac.smartenv.vehicle.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;


/**
 * 车辆,设备,物资等实体的分类信息实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_vehicle_category")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "车辆类型对象", description = "车辆分类信息")
public class VehicleCategory extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    /**
     * 分类编码
     */
    @ApiModelProperty(value = "分类编码")
    private String categoryCode;
    /**
     * 父级分类ID
     */
    @ApiModelProperty(value = "父级分类ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentCategoryId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortId;
}

