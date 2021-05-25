package com.ai.apac.smartenv.cache.controller;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.event.cache.PublicEventKpiCache;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.system.cache.*;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
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
 * @description 系统级别缓存操作
 * @Date 2020/2/23 1:00 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/systemCache")
@Api(value = "系统级别缓存操作", tags = "系统级别缓存操作")
public class SystemCacheController extends BladeController {

    private ISysClient sysClient;

    /**
     * 重新加载租户数据
     *
     * @return
     */
    @GetMapping("/reload-all-tenant")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "重新加载租户数据", notes = "重新加载租户数据")
    public R reloadTenant() {
        TenantCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有角色数据
     *
     * @return
     */
    @GetMapping("/reload-all-role")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "加载所有角色数据", notes = "加载所有角色数据")
    public R reloadAllRole() {
        RoleCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有角色数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-role/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "加载指定租户角色数据", notes = "加载指定租户角色数据")
    public R reloadTenantRole(@PathVariable String tenantId) {
        RoleCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 重新加载所有部门数据
     *
     * @return
     */
    @GetMapping("/reload-all-dept")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "加载所有部门数据", notes = "加载所有部门数据")
    public R reloadAllDept() {
        DeptCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有部门数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-dept/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "加载指定租户部门数据", notes = "加载指定租户部门数据")
    public R reloadTenantDept(@PathVariable String tenantId) {
        DeptCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 重新加载所有字典数据
     *
     * @return
     */
    @GetMapping("/reload-dict")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "重新加载所有字典数据", notes = "重新加载所有字典数据")
    public R reloadDict() {
        DictCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有业务字典数据
     *
     * @return
     */
    @GetMapping("/reload-bizDict")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "重新加载所有业务字典数据", notes = "重新加载所有业务字典数据")
    public R reloadBizDict() {
        DictBizCache.reload();
        return R.status(true);
    }

    /**
     * 加载指定租户业务字典数据
     *
     * @return
     */
    @GetMapping("/reload-tenant-bizDict/{tenantId}")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "加载指定租户业务字典数据", notes = "加载指定租户业务字典数据")
    public R reloadTenantBizDict(@PathVariable String tenantId) {
        DictBizCache.reload(tenantId);
        return R.status(true);
    }

    /**
     * 重新加载所有操作员用户数据
     *
     * @return
     */
    @GetMapping("/reload-all-user")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "重新加载所有操作员用户数据", notes = "重新加载所有操作员用户数据")
    public R reloadAllUser() {
        UserCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载城市数据
     *
     * @return
     */
    @GetMapping("/reload-all-city")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "重新加载城市数据", notes = "重新加载城市数据")
    public R reloadCity() {
        CityCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载菜单数据
     *
     * @return
     */
    @GetMapping("/reload-all-menu")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "重新加载菜单数据", notes = "重新加载菜单数据")
    public R reloadMenu() {
        MenuCache.reload();
        return R.status(true);
    }
    /**
     * 重新加载车辆数据
     *
     * @return
     */
    @GetMapping("/reload-all-vehicle")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "重新加载车辆数据", notes = "重新加载车辆数据")
    public R reloadVehicle() {
    	VehicleCache.reload();
    	return R.status(true);
    }


    /**
     * 重新加载实体分类数据
     *
     * @return
     */
    @GetMapping("/reload-entity")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = "重新加载实体分类数据", notes = "重新加载实体分类数据")
    public R reloadEntity() {
        EntityCategoryCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载实体分类数据
     *
     * @return
     */
    @GetMapping("/reload-vehicle-category")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "重新加载车辆分类数据", notes = "重新加载车辆分类数据")
    public R reloadVehicleCategory() {
        VehicleCategoryCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有岗位数据
     *
     * @return
     */
    @GetMapping("/reload-station")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 16)
    @ApiOperation(value = "重新加载所有岗位数据", notes = "重新加载所有岗位数据")
    public R reloadStation() {
        StationCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有公司数据
     *
     * @return
     */
    @GetMapping("/reload-company")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 17)
    @ApiOperation(value = "重新加载所有公司数据", notes = "重新加载所有公司数据")
    public R reloadCompany() {
        CompanyCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有项目数据
     *
     * @return
     */
    @GetMapping("/reload-project")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 18)
    @ApiOperation(value = "重新加载所有项目数据", notes = "重新加载所有项目数据")
    public R reloadProject() {
        ProjectCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载所有公众事件KPI数据
     *
     * @return
     */
    @GetMapping("/reload-public-event-kpi")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 19)
    @ApiOperation(value = "重新加载所有公众事件KPI数据", notes = "重新加载所有公众事件KPI数据")
    public R reloadPublicEventKpi() {
        PublicEventKpiCache.reload();
        return R.status(true);
    }

    /**
     * 重新加载事件与kpi关联数据
     *
     * @return
     */
    @GetMapping("/reload-event-kpi-rel")
    @ApiOperationSupport(order = 20)
    @ApiOperation(value = "重新加载事件与kpi关联数据", notes = "重新加载事件与kpi关联数据")
    public R reloadEventKpiRel() {
        EventCache.reloadEventKpiRel();
        return R.status(true);
    }


    /**
     * 重新加载事件与kpi关联数据
     *
     * @return
     */
    @GetMapping("/reload-event-kpi-def")
    @ApiOperationSupport(order = 21)
    @ApiOperation(value = "重新加载事件kpi数据", notes = "重新加载事件kpi数据")
    public R reloadEventKpiDef() {
        EventCache.reloadEventKpiDef();
        return R.status(true);
    }


    /**
     * 重新加载事件与kpi关联数据
     *
     * @return
     */
    @GetMapping("/reload-event-kpi-catalog")
    @ApiOperationSupport(order = 22)
    @ApiOperation(value = "重新加载事件kpi分类数据", notes = "重新加载事件kpi分类数据")
    public R reloadEventKpiCatalog() {
        EventCache.reloadEventKpiCatalog();
        return R.status(true);
    }



    @GetMapping("/reload-vehicle-work-type")
    @ApiOperationSupport(order = 23)
    @ApiOperation(value = "重新加载车辆工作类型", notes = "重新加载车辆工作类型")
    public R reloadVehicleWorkType(){
        VehicleCategoryCache.reloadVehicleWorkType();
        return R.status(true);

    }

}
