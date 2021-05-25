package com.ai.apac.smartenv.cache.controller;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.inventory.cache.InventoryCache;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.workarea.cache.WorkareaCache;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description 业务级别缓存操作
 * @Date 2020/2/25 10:15 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/bizCache")
@Api(value = "业务级别缓存操作", tags = "业务级别缓存操作")
public class BizCacheController extends BladeController {

    private ISysClient sysClient;

    /**
     * 重新加载所有用户数据
     *
     * @return
     */
    @GetMapping("/reload-all-person")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "重新加载所有人员数据", notes = "重新加载所有人员数据")
    public R reloadTenant() {
        PersonCache.reload();
        return R.status(true);
    }

    /**
     * 加载指定租户的用户数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-person/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "加载指定租户的人员数据", notes = "加载指定租户的人员数据")
    public R reloadTenantRole(@PathVariable String tenantId) {
        PersonCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 加载所有设备数据
     *
     * @return
     */
    @GetMapping("/reload-all-device")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "加载所有设备数据", notes = "加载所有设备数据")
    public R reloadAllDevice() {
        DeviceCache.reload();
        return R.status(true);
    }


    /**
     * 加载指定租户的设备数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-device/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "加载指定租户的设备数据", notes = "加载指定租户的设备数据")
    public R reloadTenantDevice(@PathVariable String tenantId) {
        DeviceCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 加载所有车辆数据
     *
     * @return
     */
    @GetMapping("/reload-all-vehicle")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "加载所有车辆数据", notes = "加载所有车辆数据")
    public R reloadAllVehicle() {
        VehicleCache.reload();
        return R.status(true);
    }

    /**
     * 加载指定租户的车辆数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-vehicle/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "加载指定租户的车辆数据", notes = "加载指定租户的车辆数据")
    public R reloadTenantVehicle(@PathVariable String tenantId) {
        VehicleCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 加载所有实体与设备关联关系
     *
     * @return
     */
    @GetMapping("/reload-all-deviceRel")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "加载所有实体与设备关联关系", notes = "加载所有实体与设备关联关系")
    public R reloadAllRelDevice() {
        DeviceRelCache.reload();
        return R.status(true);
    }

    /**
     * 加载指定租户的实体与设备关联关系
     *
     * @return
     */
    @GetMapping("/reload-tenant-deviceRel/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "加载指定租户的实体与设备关联关系", notes = "加载指定租户的实体与设备关联关系")
    public R reloadTenantDeviceRel(@PathVariable String tenantId) {
        DeviceRelCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 加载排班数据
     *
     * @return
     */
    @GetMapping("/reload-schedule")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "加载排班数据", notes = "加载排班数据")
    public R reloadSchedule() {
        ScheduleCache.reload();
        return R.status(true);
    }

    /**
     * 加载库存数据
     *
     * @return
     */
    @GetMapping("/reload-inventory")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "加载库存数据", notes = "加载库存数据")
    public R reloadInventory() {
        InventoryCache.reload();
        return R.status(true);
    }

    /**
     * 加载所有工作区域/线路的基础数据
     *
     * @return
     */
    @GetMapping("/reload-all-workarea")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "加载所有工作区域/线路数据", notes = "加载所有工作区域/线路数据")
    public R reloadAllWorkarea(@PathVariable String tenantId) {
        WorkareaCache.reload(tenantId);
        return R.status(true);
    }
}
