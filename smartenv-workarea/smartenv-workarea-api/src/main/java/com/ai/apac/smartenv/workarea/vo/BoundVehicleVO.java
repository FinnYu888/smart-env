package com.ai.apac.smartenv.workarea.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 工作区域关联表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
//@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BoundPersonVO对象", description = "工作区域关联表")
public class BoundVehicleVO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "部门Id")
    private Long deptId;
    @ApiModelProperty(value = "部门下车辆列表")
    private List<VehicleVO> list;

}
