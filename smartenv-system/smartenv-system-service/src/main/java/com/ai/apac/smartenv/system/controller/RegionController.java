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

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.service.IRegionService;
import com.ai.apac.smartenv.system.vo.BigScreenInfoVO;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.system.vo.BusiRegionVO;
import com.ai.apac.smartenv.system.vo.RegionVO;
import com.ai.apac.smartenv.system.wrapper.RegionWrapper;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/region")
@Api(value = "区域管理", tags = "区域管理")
public class RegionController extends BladeController {

    private IRegionService regionService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入region")
    public R<RegionVO> detail(Region region) {
        Region detail = regionService.getOne(Condition.getQueryWrapper(region));
        return R.data(RegionWrapper.build().entityVO(detail));
    }

    /**
     * 获取区域树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionName", value = "区域名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取区域树形结构", notes = "获取区域树形结构")
    public R<List<INode>> tree(@ApiIgnore @RequestParam Map<String, Object> queryCond, BladeUser bladeUser) {
        QueryWrapper<Region> queryWrapper = Condition.getQueryWrapper(queryCond, Region.class);
//		List<Region> regionList = regionService.list((!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Region::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        List<Region> regionList = regionService.list(queryWrapper.lambda().eq(Region::getTenantId, bladeUser.getTenantId()));
        return R.data(RegionWrapper.build().listNodeVO(regionList));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表查询", notes = "传入region")
    public R<List<RegionVO>> list(Region region, Query query, BladeUser bladeUser) {
        QueryWrapper<Region> queryWrapper = Condition.getQueryWrapper(region);
        List<Region> pages = regionService.list(queryWrapper.lambda().eq(Region::getTenantId, bladeUser.getTenantId()));
        return R.data(RegionWrapper.build().listVO(pages));
    }


    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入region")
    public R<IPage<RegionVO>> page(RegionVO region, Query query, BladeUser bladeUser) {
        region.setTenantId(bladeUser.getTenantId());
        IPage<RegionVO> pages = regionService.selectRegionPage(Condition.getPage(query), region);
        return R.data(pages);
    }

    /**
     * 新增
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入region")
    public R save(@Valid @RequestBody Region region) {
        return R.status(regionService.save(region));
    }


    /**
     * 新增行政区域或业务区域
     *行政区域不用传坐标点数组对象
     */
    @PostMapping("/savaOrUpdateRegionNew")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增或修改行政区域或业务区域", notes = "传入busiRegionVO")
    public R savaOrUpdateRegionNew(@RequestBody BusiRegionVO busiRegionVO) {
        return R.status(regionService.savaOrUpdateRegionNew(busiRegionVO));
    }

    /**
     * 查询业务区域及网格信息
     */
    @GetMapping("/queryBusiRegionList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询业务区域及网格信息", notes = "传入regionId")
    public R<BusiRegionVO> queryBusiRegionListNew(@RequestParam Long regionId) {
        return R.data(regionService.queryBusiRegionList(regionId));
    }

    /**
     * 大屏根据业务区域查询告警、事件、人员、车辆 的数量
     */
    @GetMapping("/queryBigScreenInfoCountByRegion")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "大屏根据业务区域查询告警、事件、人员、车辆 的数量", notes = "传入regionId")
    public R<BigScreenInfoVO> queryBigScreenInfoCountByRegion(@RequestParam Long regionId,BladeUser user) {
        return R.data(regionService.queryBigScreenInfoCountByRegion(regionId,user.getTenantId()));
    }

    /**
     * 大屏查询所有业务区域的告警、事件、人员、车辆 的数量总和
     */
    @GetMapping("/queryBigScreenInfoCountByAllRegion")
    @ApiOperationSupport(order = 16)
    @ApiOperation(value = "大屏查询所有业务区域的告警、事件、人员、车辆 的数量总和", notes = "传入regionId")
    public R<BigScreenInfoVO> queryBigScreenInfoCountByAllRegion(BladeUser user) {
        return R.data(regionService.queryBigScreenInfoCountByAllRegion(user.getTenantId()));
    }


    /**
     * 查询当前区域以及包含的业务片区列表
     */
    @GetMapping("/queryChildBusiRegionList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询当前区域以及包含的业务片区列表", notes = "传入regionId")
    public R<BusiRegionTreeVO> queryChildBusiRegionList(@RequestParam Long regionId) {
        return R.data(regionService.queryChildBusiRegionList(regionId));
    }

    /**
     * 查询所有业务区域及网格
     */
    @GetMapping("/queryAllBusiRegionAndNodes")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "查询所有业务区域及网格", notes = "")
    public R<List<BusiRegionVO>> queryAllBusiRegionAndNodes(@RequestParam(required = false)  String regionType,BladeUser user) {
        return R.data(regionService.queryAllBusiRegionAndNodes(regionType,user.getTenantId()));
    }

    /**
     * 查询所有业务区域
     */
    @GetMapping("/queryAllBusiRegionList")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "查询所有业务区域", notes = "")
    public R<List<Region>> queryAllBusiRegionList(@RequestParam(required = false)  String regionType,BladeUser user) {
        return R.data(regionService.queryAllBusiRegionList(regionType,user.getTenantId()));
    }

    /**
     * 修改
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入region")
    public R update(@Valid @RequestBody Region region) {
        return R.status(regionService.updateById(region));
    }

//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入region")
//	public R submit(@Valid @RequestBody Region region) {
//		return R.status(regionService.saveOrUpdate(region));
//	}


    /**
     * 删除
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//        return R.status(regionService.deleteLogic(Func.toLongList(ids)));
        return R.status(regionService.removeRegion(ids));
    }


}
