package com.ai.apac.smartenv.vehicle.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/vehicleType/sync")
@Api(value = "车辆分类同步信息", tags = "车辆分类信息同步接口")
public class VehicleCategorySyncController extends BladeController {

    private IVehicleCategoryService vehicleCategoryService;

    /**
     * 同步第三方车辆分类信息
     */
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增车辆分类", notes = "传入vehicleCategory")
    @ApiLog(value = "新增车辆分类")
    public R<Boolean> syncthirdVehicleCategory(@RequestBody JSONObject obj) {
        VehicleCategory vehicleCategory = initializeData(obj);
        return R.status(vehicleCategoryService.save(vehicleCategory));
    }


    /**
     *删除第三方车辆分类信息
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "逻辑删除车辆分类", notes = "传入ids")
    @ApiLog(value = "逻辑删除车辆分类")
    public R<Boolean> delthirdVehicleCategory(@RequestBody JSONObject obj) {
        if (ObjectUtil.isNotEmpty(obj.getStr("vehicleTypeCode"))) {
            QueryWrapper<VehicleCategory> wrapper = new QueryWrapper<VehicleCategory>();
            wrapper.lambda().eq(VehicleCategory::getCategoryCode,obj.getStr("vehicleTypeCode"));
            List<VehicleCategory> vehicleCategoryList = vehicleCategoryService.list(wrapper);
            if(ObjectUtil.isNotEmpty(vehicleCategoryList) && vehicleCategoryList.size() > 0 ){
                return R.status(vehicleCategoryService.removeById(vehicleCategoryList.get(0).getId()));
            }
        } else {
            throw new ServiceException("人员编号不能为空");
        }
        return R.status(true);
    }

    private VehicleCategory initializeData(JSONObject obj) {
        VehicleCategory vehicleCategory = new VehicleCategory();
        if (ObjectUtil.isNotEmpty(obj.getStr("vehicleTypeCode"))) {
            vehicleCategory.setCategoryCode(obj.getStr("vehicleTypeCode"));
        } else {
            throw new ServiceException("车辆类型编号不能为空");
        }
        if (ObjectUtil.isNotEmpty(obj.getStr("vehicleTypeName"))) {
            vehicleCategory.setCategoryName(obj.getStr("vehicleTypeName"));
        }
        if (ObjectUtil.isNotEmpty(obj.getStr("parentVehicleTypeCode"))) {
            QueryWrapper<VehicleCategory> wrapper = new QueryWrapper<VehicleCategory>();
            wrapper.lambda().eq(VehicleCategory::getCategoryCode,obj.getStr("parentVehicleTypeCode"));
            List<VehicleCategory> vehicleCategoryList = vehicleCategoryService.list(wrapper);
            if(ObjectUtil.isNotEmpty(vehicleCategoryList) && vehicleCategoryList.size() > 0 ){
                vehicleCategory.setParentCategoryId(vehicleCategoryList.get(0).getId());
            }

        }
        return vehicleCategory;
    }
    }
