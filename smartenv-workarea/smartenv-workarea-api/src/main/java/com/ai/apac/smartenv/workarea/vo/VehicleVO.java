package com.ai.apac.smartenv.workarea.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@ApiModel(value = "VehicleVO对象", description = "车辆简化信息")
public class VehicleVO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "部门id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long deptId;
    @ApiModelProperty(value = "车牌号")
    private String plateNumber;
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    @ApiModelProperty(value = "车辆id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "关联id")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long relId;
    @ApiModelProperty(value = "加入日期")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private String deptAddTime;
    @ApiModelProperty(value = "状态")
    private Integer status;
}
