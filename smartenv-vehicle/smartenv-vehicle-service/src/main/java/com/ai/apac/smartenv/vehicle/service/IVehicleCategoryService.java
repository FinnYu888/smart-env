package com.ai.apac.smartenv.vehicle.service;

import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;


/**
 * 车辆分类信息 服务类
 *
 * @author Blade
 * @since 2020-02-07
 **/

public interface IVehicleCategoryService extends BaseService<VehicleCategory> {

    /**
     * 自定义分页
     *
     * @param page
     * @param vehicleCategory
     * @return
     */
    IPage<VehicleCategoryVO> selectVehicleCategoryPage(IPage<VehicleCategoryVO> page, VehicleCategoryVO vehicleCategory);


    List<Long> getAllChildIdByParentId(Long categoryId);

    /**
     * 根据租户ID取租户下所有车辆类型（不区分级联关系）
     * @param tenantId
     * @return
     */
    List<VehicleCategoryVO> listVehicleCategoryByTenantId(String tenantId);

    boolean createVehicleCategory(VehicleCategory vehicleCategory);

    boolean updateVehicleCategory(VehicleCategory vehicleCategory);

    boolean deleteVehicleCategory(String vehicleCategoryIds);

    List<VehicleCategoryVO> tree();


}
