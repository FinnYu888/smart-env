package com.ai.apac.smartenv.vehicle.feign;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleWorkType;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.ai.apac.smartenv.vehicle.service.IVehicleWorkTypeService;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleCategoryWrapper;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleWorkTypeWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * VehicleCategory 服务Feign实现类
 *
 * @author ZHANGLEI25
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class VehicleCategoryClient implements IVehicleCategoryClient {


    private IVehicleCategoryService vehicleCategoryService;

    private IVehicleWorkTypeService vehicleWorkTypeService;

    @Override
    @GetMapping(CATEGORY)
    public R<VehicleCategory> getCategory(Long id) {
        return R.data(vehicleCategoryService.getById(id));
    }

    @Override
    public R<VehicleCategory> getCategoryByCode(String code, String tenantId) {
        QueryWrapper<VehicleCategory> vehicleCategoryQueryWrapper = new QueryWrapper<VehicleCategory>();
        vehicleCategoryQueryWrapper.lambda().eq(VehicleCategory::getCategoryCode,code);
        vehicleCategoryQueryWrapper.lambda().eq(VehicleCategory::getTenantId,tenantId);
        return R.data(vehicleCategoryService.getOne(vehicleCategoryQueryWrapper));
    }

    @Override
    @GetMapping(CATEGORY_NAME)
    public R<String> getCategoryName(Long id) {
        VehicleCategory VehicleCategory = vehicleCategoryService.getById(id);
    	String categoryName = "";
    	if (VehicleCategory != null) {
    		categoryName = VehicleCategory.getCategoryName();
		}
        return R.data(categoryName);
    }

    @Override
    @GetMapping(CATEGORY_CODE)
    public R<String> getCategoryCode(Long id) {
        VehicleCategory VehicleCategory = vehicleCategoryService.getById(id);
    	String categoryCode = "";
    	if (VehicleCategory != null) {
    		categoryCode = VehicleCategory.getCategoryCode();
		}
        return R.data(categoryCode);
    }


    @Override
    @GetMapping(CATEGORY_LIST_BY_PARENT_CATEGORY_ID)
    public R<List<VehicleCategory>> getCategoryByParentCategoryId(Long parentCategoryId) {
        VehicleCategory wrapper = new VehicleCategory();
		wrapper.setParentCategoryId(parentCategoryId);
		List<VehicleCategory> list = vehicleCategoryService.list(Condition.getQueryWrapper(wrapper));
		return R.data(list);
    }

    @Override
    public R<List<Long>> getSubCategoryIdByParentCategoryId(Long parentCategoryId) {
        return R.data(vehicleCategoryService.getAllChildIdByParentId(parentCategoryId));
    }

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @Override
    public R<List<VehicleCategoryVO>> getAllCategory() {
        List<VehicleCategoryVO> voList = VehicleCategoryWrapper.build().listVO(vehicleCategoryService.list());
        return R.data(voList);
    }

    /**
     * 取租户下所有车辆类型
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(CATEGORY_UNDER_TENANT)
    public R<List<VehicleCategoryVO>> listVehicleCategoryByTenantId(String tenantId) {
        return R.data(vehicleCategoryService.listVehicleCategoryByTenantId(tenantId));
    }



    @Override
    @GetMapping(LIST_VEHICLE_WORK_TYPE)
    public R<List<VehicleWorkTypeVO>> listVehicleWorkType(){
        return R.data(VehicleWorkTypeWrapper.build().listVO(vehicleWorkTypeService.list()));
    }



    @Override
    @GetMapping(GET_VEHICLE_WORK_TYPE_BY_CODE)
    public R<VehicleWorkTypeVO> getVehicleWorkTypeByCode(String workTypeCode){
        List<VehicleWorkTypeVO> vehicleWorkTypeVOS = VehicleWorkTypeWrapper.build().listVO(vehicleWorkTypeService.list(new QueryWrapper<VehicleWorkType>().lambda().eq(VehicleWorkType::getVehicleCategoryCode,workTypeCode)));
        return R.data(CollectionUtil.isEmpty(vehicleWorkTypeVOS)?null:vehicleWorkTypeVOS.get(0));
    }


}
