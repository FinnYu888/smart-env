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

import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.service.IDeptService;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.ai.apac.smartenv.system.wrapper.DeptWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.Func;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Api(value = "部门", tags = "部门")
//@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
public class DeptController extends BladeController {

    private IDeptService deptService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入dept")
    @ApiLog(value = "查询部门详情")
    public R<DeptVO> detail(Dept dept) {
        Dept detail = deptService.getOne(Condition.getQueryWrapper(dept));
        return R.data(DeptWrapper.build().entityVO(detail));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptName", value = "部门名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fullName", value = "部门全称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入dept")
    @ApiLog(value = "查询部门列表")
    public R<List<INode>> list(@ApiIgnore @RequestParam Map<String, Object> dept, BladeUser bladeUser) {
        QueryWrapper<Dept> queryWrapper = Condition.getQueryWrapper(dept, Dept.class);
        List<Dept> dbList = deptService.list(queryWrapper);
        if(dbList == null || dbList.size() == 0){
            R.data(DeptWrapper.build().listNodeVO(new ArrayList<Dept>()));
        }
        List<Dept> list = dbList.stream()
                .filter(deptObj -> deptObj.getTenantId().equals(bladeUser.getTenantId()))
                .collect(Collectors.toList());
//        List<Dept> list = deptService.list((!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Dept::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        return R.data(DeptWrapper.build().listNodeVO(list));
    }

    /**
     * 懒加载列表
     */
    @GetMapping("/lazy-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptName", value = "部门名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fullName", value = "部门全称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "懒加载列表", notes = "传入dept")
    public R<List<INode>> lazyList(@ApiIgnore @RequestParam Map<String, Object> dept, Long parentId, BladeUser bladeUser) {
        List<DeptVO> list = deptService.lazyList(bladeUser.getTenantId(), parentId, dept);
        return R.data(DeptWrapper.build().listNodeLazyVO(list));
    }

    /**
     * 获取部门树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    @ApiLog(value = "获取部门树形结构")
    public R<List<DeptVO>> tree(String tenantId, BladeUser bladeUser) {
        List<DeptVO> tree = deptService.tree(Func.toStrWithEmpty(tenantId, bladeUser.getTenantId()));
        return R.data(tree);
    }

    /**
     * 懒加载获取部门树形结构
     */
    @GetMapping("/lazy-tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "懒加载树形结构", notes = "树形结构")
    @ApiLog(value = "懒加载获取部门树形结构")
    public R<List<DeptVO>> lazyTree(String tenantId, Long parentId, BladeUser bladeUser) {
        List<DeptVO> tree = deptService.lazyTree(Func.toStrWithEmpty(tenantId, bladeUser.getTenantId()), parentId);
        return R.data(tree);
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入dept")
    @CacheEvict(cacheNames = {SYS_CACHE}, allEntries = true)
    @ApiLog(value = "新增/修改部门")
    public R submit(@Valid @RequestBody Dept dept) {
        return R.status(deptService.submit(dept));
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "删除", notes = "传入ids")
    @CacheEvict(cacheNames = {SYS_CACHE}, allEntries = true)
    @ApiLog(value = "删除部门")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(deptService.removeDept(ids));
    }


}
