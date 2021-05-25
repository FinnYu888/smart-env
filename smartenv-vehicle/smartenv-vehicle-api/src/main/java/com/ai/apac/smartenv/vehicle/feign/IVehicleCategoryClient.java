package com.ai.apac.smartenv.vehicle.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * VehicleCategory Feign接口类
 *
 * @author ZHANGLEI25
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_VEHICLE_NAME,
        fallback = IVehicleCategoryClientFallback.class
)
public interface IVehicleCategoryClient {


    String API_PREFIX = "/client";
    String CATEGORY = API_PREFIX + "/category";
    String GET_ALL_CATEGORY = API_PREFIX + "/get-all-category";
    String CATEGORY_NAME = API_PREFIX + "/category-name";
    String CATEGORY_CODE = API_PREFIX + "/category-code";
    String CATEGORY_LIST_BY_PARENT_CATEGORY_ID = API_PREFIX + "/category-list-by-parent-category-id";
    String SUB_CATEGORYID_BY_PARENT_CATEGORY_ID = API_PREFIX + "/subCategoryId-by-parent-category-id";
    String CATEGORY_UNDER_TENANT = API_PREFIX + "/vehicle-category-by-tenant-id";
    String CATEGORY_BY_CODE = API_PREFIX + "/category-by-code";
    String LIST_VEHICLE_WORK_TYPE = API_PREFIX + "/list-vehicle-work-type";
    String GET_VEHICLE_WORK_TYPE_BY_CODE = API_PREFIX + "/get-vehicle-work-type-by-code";


    /**
     * 根据类别ID获取类别
     *
     * @param id 主键
     * @return Menu
     */
    @GetMapping(CATEGORY)
    R<VehicleCategory> getCategory(@RequestParam("id")  Long id);

    @GetMapping(CATEGORY_BY_CODE)
    R<VehicleCategory> getCategoryByCode(@RequestParam("code") String code,@RequestParam("tenantId") String tenantId);

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @GetMapping(GET_ALL_CATEGORY)
    R<List<VehicleCategoryVO>> getAllCategory();

    /**
     * 根据类别ID获取类别名称
     *
     * @param id 主键
     * @return Menu
     */
    @GetMapping(CATEGORY_NAME)
    R<String> getCategoryName(@RequestParam("id")  Long id);

    /**
     * 根据类别ID获取类别CODE
     *
     * @param id 主键
     * @return
     */
    @GetMapping(CATEGORY_CODE)
    R<String> getCategoryCode(@RequestParam("id")  Long id);


    @GetMapping(CATEGORY_LIST_BY_PARENT_CATEGORY_ID)
	R<List<VehicleCategory>> getCategoryByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId);

    @GetMapping(SUB_CATEGORYID_BY_PARENT_CATEGORY_ID)
    R<List<Long>> getSubCategoryIdByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId);

    /**
     * 取租户下所有车辆类型
     * @param tenantId
     * @return
     */
    @GetMapping(CATEGORY_UNDER_TENANT)
    R<List<VehicleCategoryVO>> listVehicleCategoryByTenantId(@RequestParam("tenantId") String tenantId);

    @GetMapping(LIST_VEHICLE_WORK_TYPE)
    R<List<VehicleWorkTypeVO>> listVehicleWorkType();

    @GetMapping(GET_VEHICLE_WORK_TYPE_BY_CODE)
    R<VehicleWorkTypeVO> getVehicleWorkTypeByCode(String workTypeCode);
}
