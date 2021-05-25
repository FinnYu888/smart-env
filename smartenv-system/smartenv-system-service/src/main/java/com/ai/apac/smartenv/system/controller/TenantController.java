/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.service.ITenantService;
import com.ai.apac.smartenv.system.vo.RoleVO;
import com.ai.apac.smartenv.system.vo.TenantVO;
import com.ai.apac.smartenv.system.wrapper.TenantWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;
import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/tenant")
@Api(value = "租户管理", tags = "租户管理")
public class TenantController extends BladeController {

    private ITenantService tenantService;

    private IOssClient ossClient;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "查询租户详情")
    public R<TenantVO> detail(Tenant tenant) {
        Tenant detail = tenantService.getOne(Condition.getQueryWrapper(tenant));
        return R.data(TenantWrapper.build().entityDetailVO(detail));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "参数名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantName", value = "角色别名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "contactNumber", value = "联系电话", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "分页查询租户信息")
    public R<IPage<TenantVO>> list(@ApiIgnore @RequestParam Map<String, Object> tenant, Query query, BladeUser bladeUser) {
        QueryWrapper<Tenant> queryWrapper = Condition.getQueryWrapper(tenant, Tenant.class).orderByDesc("update_time");
        IPage<Tenant> pages = tenantService.page(Condition.getPage(query), (!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Tenant::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        return R.data(TenantWrapper.build().pageVO(pages));
    }

    /**
     * 下拉数据源
     */
    @GetMapping("/select")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "下拉数据源", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R<List<TenantVO>> select(Tenant tenant, BladeUser bladeUser) {
        QueryWrapper<Tenant> queryWrapper = Condition.getQueryWrapper(tenant);
        List<Tenant> list = tenantService.list((!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Tenant::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        return R.data(TenantWrapper.build().listVO(list));
    }

    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "自定义分页", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R<IPage<TenantVO>> page(Tenant tenant, Query query) {
        IPage<Tenant> pages = tenantService.selectTenantPage(Condition.getPage(query), tenant);
        return R.data(TenantWrapper.build().pageVO(pages));
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "新增或修改", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiLog(value = "新增/修改租户信息")
    public R submit(@Valid @RequestBody Tenant tenant) {
        CacheUtil.clear(SYS_CACHE);
        return R.status(tenantService.saveTenant(tenant));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiLog(value = "删除租户")
    @CacheEvict(cacheNames = {SYS_CACHE}, allEntries = true)
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        CacheUtil.clear(SYS_CACHE);
        return R.status(tenantService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * 授权配置
     */
    @PostMapping("/setting")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "授权配置", notes = "传入ids,accountNumber,expireTime")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @CacheEvict(cacheNames = {SYS_CACHE}, allEntries = true)
    @ApiLog(value = "租户授权")
    public R setting(@ApiParam(value = "主键集合", required = true) @RequestParam String ids, @ApiParam(value = "账号额度") Integer accountNumber, @ApiParam(value = "过期时间") Date expireTime) {
        boolean temp = tenantService.update(
                Wrappers.<Tenant>update().lambda()
                        .set(Tenant::getAccountNumber, accountNumber)
                        .set(Tenant::getExpireTime, expireTime)
                        .in(Tenant::getId, Func.toLongList(ids))
        );
        return R.status(temp);
    }

    /**
     * 根据名称查询列表
     *
     * @param name 租户名称
     */
    @GetMapping("/find-by-name")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "根据名称查询列表", notes = "传入tenant")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    public R<List<TenantVO>> findByName(String name) {
        List<Tenant> list = tenantService.list(Wrappers.<Tenant>query().lambda().like(Tenant::getTenantName, name));
        return R.data(TenantWrapper.build().listVO(list));
    }

    /**
     * 根据域名查询信息
     *
     * @param domain 域名
     */
    @GetMapping("/info")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "配置信息", notes = "传入domain")
    public R<Kv> info(String domain) {
        Tenant tenant = tenantService.getOne(Wrappers.<Tenant>query().lambda().eq(Tenant::getDomain, domain));
        Kv kv = Kv.create();
        if (tenant != null) {
            kv.set("tenantId", tenant.getTenantId())
                    .set("domain", tenant.getDomain())
                    .set("backgroundUrl", tenant.getBackgroundUrl());
        }
        return R.data(kv);
    }

    /**
     * 校验租户名是否重复
     */
    @GetMapping("/validTenantName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantName", value = "租户名", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "校验租户名是否重复", notes = "校验租户名是否重复,重复返回true,否则返回false")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R validTenantName(@RequestParam String tenantName) {
        boolean result = tenantService.validTenantName(tenantName);
        if (result) {
            return R.data(true, "租户名不能重复");
        } else {
            return R.data(false, "租户名不重复");
        }
    }

    /**
     * 修改租户状态
     */
    @PostMapping("/changeStatus")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "修改租户状态", notes = "id,status(1-正常,2-锁定)")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiLog(value = "修改租户状态")
    public R changeStatus(@Valid @RequestBody Tenant tenant) {
        boolean temp = tenantService.changeTenantStatus(tenant.getId(), tenant.getStatus());
        return R.status(temp);
    }

    /**
     * 获取租户管理员角色信息
     *
     * @param tenantId
     * @return
     */
    @GetMapping("/tenant-admin-role")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "获取租户管理员角色信息", notes = "获取租户管理员角色信息")
    @ApiLog(value = "获取租户管理员角色信息")
    public R<Role> getTenantAdminRole(@RequestParam String tenantId) {
        return R.data(tenantService.getTenantAdminRole(tenantId));
    }
}
