package com.ai.apac.smartenv.vehicle.controller;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/26 5:54 下午
 **/

import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.vo.SimpleDictVO;
import com.ai.apac.smartenv.system.vo.StationVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleCategoryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制器
 *
 * @author zhanglei
 */
@RestController
@AllArgsConstructor
@RequestMapping("/vehiclecategory")
@Api(value = "车辆类型管理", tags = "车辆类型管理")
public class VehicleCategoryController {

    private IVehicleCategoryService vehicleCategoryService;

    /**
     * 新增
     */
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增车辆类型", notes = "传入VehicleCategory")
    @ApiLog(value = "新车辆类型信息")
    public R create(@Valid @RequestBody VehicleCategory vehicleCategory) {
        return R.status(vehicleCategoryService.createVehicleCategory(vehicleCategory));
    }

    /**
     * 修改
     */
    @PutMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改车辆类型", notes = "传入vehicleCategoryId")
    @ApiLog(value = "修改车辆类型")
    public R update(@RequestBody VehicleCategory vehicleCategory) {
        return R.status(vehicleCategoryService.updateVehicleCategory(vehicleCategory));
    }


    /**
     * 根据ID查询车辆类型详情
     *
     * @return
     */
    @GetMapping("/{vehicleCategoryId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vehicleCategoryId", value = "车辆类型ID", paramType = "path", dataType = "long")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据ID查询车辆类型详情", notes = "根据ID查询车辆类型详情")
    public R<VehicleCategoryVO> getVehicleCategoryById(@PathVariable("vehicleCategoryId") Long vehicleCategoryId) {
        return R.data(VehicleCategoryWrapper.build().entityVO(vehicleCategoryService.getById(vehicleCategoryId)));
    }

    /**
     * 删除
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除车辆类型", notes = "传入车辆类型集合列表")
    @ApiLog(value = "删除车辆类型信息")
    public R deleteStation(@ApiParam(value = "车辆类型主键集合", required = true) @RequestParam("vehicleCategoryIds") String vehicleCategoryIds) {
        return R.status(vehicleCategoryService.deleteVehicleCategory(vehicleCategoryIds));
    }


    /**
     * 获取车辆分类树形结构
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "车辆分类树结构", notes = "车辆分类树结构")
    @ApiLog(value = "获取车辆分类树形结构")
    public R<List<VehicleCategoryVO>> tree(BladeUser bladeUser) {
        List<VehicleCategoryVO> tree = vehicleCategoryService.tree();
        return R.data(tree);
    }

}
