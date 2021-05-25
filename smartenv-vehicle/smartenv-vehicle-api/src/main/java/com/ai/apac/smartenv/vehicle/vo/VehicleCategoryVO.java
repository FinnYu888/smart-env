package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆分类信息视图实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleCategoryVO对象", description = "车辆分类信息")
public class VehicleCategoryVO extends VehicleCategory  implements INode{
    private static final long serialVersionUID = 1L;

    /**
     * 子孙节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<INode> children;

    @Override
    public Long getParentId() {
        return null;
    }

    @Override
    public List<INode> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    /**
     * 用于与告警做关联时，标记是否已与当前告警做过关联
     */
    private Integer isSelected;
    /**
     * 下级分类
     */
    private List<VehicleCategoryVO> childVehicleCategoryVOS;

}
