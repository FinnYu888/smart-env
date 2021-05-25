package com.ai.apac.smartenv.vehicle.feign;

import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IVehicleCategoryClientFallback implements IVehicleCategoryClient {

    @Override
    public R<VehicleCategory> getCategory(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<VehicleCategory> getCategoryByCode(String code, String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getCategoryName(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getCategoryCode(Long id) {
        return R.fail("获取数据失败");
    }

	@Override
	public R<List<VehicleCategory>> getCategoryByParentCategoryId(Long parentCategoryId) {
		return R.fail("获取数据失败");
	}

    @Override
    public R<List<Long>> getSubCategoryIdByParentCategoryId(Long parentCategoryId) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @Override
    public R<List<VehicleCategoryVO>> getAllCategory() {
        return R.fail("获取数据失败");
    }

    /**
     * 取租户下所有车辆类型
     * @param tenantId
     * @return
     */
    @Override
    public R<List<VehicleCategoryVO>> listVehicleCategoryByTenantId(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleWorkTypeVO>> listVehicleWorkType() {
        return R.fail("获取数据失败");
    }

    @Override
    public R<VehicleWorkTypeVO> getVehicleWorkTypeByCode(String workTypeCode) {
        return R.fail("获取数据失败");
    }
}
