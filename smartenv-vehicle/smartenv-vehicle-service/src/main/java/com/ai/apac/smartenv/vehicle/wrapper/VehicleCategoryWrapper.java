package com.ai.apac.smartenv.vehicle.wrapper;

import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.vo.EntityCategoryVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆分类信息包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-02-07
 */
public class VehicleCategoryWrapper extends BaseEntityWrapper<VehicleCategory, VehicleCategoryVO> {

    public static VehicleCategoryWrapper build() {
        return new VehicleCategoryWrapper();
    }

    @Override
    public VehicleCategoryVO entityVO(VehicleCategory vehicleCategory) {
        VehicleCategoryVO vehicleCategoryVO = BeanUtil.copy(vehicleCategory, VehicleCategoryVO.class);

        return vehicleCategoryVO;
    }

    @Override
    public List<VehicleCategoryVO> listVO(List<VehicleCategory> entityList) {
        List<VehicleCategoryVO> voList = new ArrayList<>();
        entityList.forEach(entity -> {
            voList.add(this.entityVO(entity));
        });
        return voList;
    }


}