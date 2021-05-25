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
package com.ai.apac.smartenv.green.controller;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.vo.DeviceViewVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.green.dto.GreenareaInfoDTO;
import com.ai.apac.smartenv.green.entity.GreenareaItem;
import com.ai.apac.smartenv.green.service.IGreenareaItemService;
import com.ai.apac.smartenv.green.vo.GreenareaItemVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import com.ai.apac.smartenv.green.vo.GreenareaInfoVO;
import com.ai.apac.smartenv.green.entity.GreenareaInfo;
import com.ai.apac.smartenv.green.wrapper.GreenareaInfoWrapper;
import com.ai.apac.smartenv.green.service.IGreenareaInfoService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.entityCategoryClient;

/**
 * 绿化养护信息
 * @author Blade
 * @since 2020-07-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/greenareainfo")
@Api(value = "绿化养护信息", tags = "绿化养护信息接口")
public class GreenareaInfoController extends BladeController {

	private IGreenareaInfoService greenareaInfoService;

	private IGreenareaItemService greenareaItemService;

	private IDeviceRelClient deviceRelClient;

	private IDeviceClient deviceClient;

	private IDictClient dictClient;

	private IEntityCategoryClient entityCategoryClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入greenareaInfo")
	public R<GreenareaInfoVO> detail(GreenareaInfo greenareaInfo) {
		GreenareaInfo detail = greenareaInfoService.getOne(Condition.getQueryWrapper(greenareaInfo));
		GreenareaInfoVO greenareaInfoVO = GreenareaInfoWrapper.build().entityVO(detail);
		QueryWrapper<GreenareaItem> greenareaItemQueryWrapper = new QueryWrapper<GreenareaItem>();
		greenareaItemQueryWrapper.lambda().eq(GreenareaItem::getGreenareaId,detail.getId());
		List<GreenareaItem> greenareaItems = greenareaItemService.list(greenareaItemQueryWrapper);
		if(ObjectUtil.isNotEmpty(greenareaItems) && greenareaItems.size() > 0){
			List<GreenareaItemVO> greenareaItemVOS = new ArrayList<GreenareaItemVO>();
			for(GreenareaItem greenareaItem:greenareaItems){
				GreenareaItemVO greenareaItemVO = Objects.requireNonNull(BeanUtil.copy(greenareaItem, GreenareaItemVO.class));
				greenareaItemVO.setItemSpecName(dictClient.getValue("green_item_type",greenareaItemVO.getItemSpecId()).getData());
				greenareaItemVOS.add(greenareaItemVO);
			}
			greenareaInfoVO.setGreenareaItemVOList(greenareaItemVOS);
		}
		return R.data(greenareaInfoVO);
	}


	/**
	 * 自定义分页 绿化养护信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "绿化区域分页展现", notes = "绿化区域分页展现")
	@ApiLog(value = "绿化区域分页展现")
	public R<IPage<GreenareaInfoVO>> page(GreenareaInfo greenareaInfo, Query query) {
		IPage<GreenareaInfo> pages = greenareaInfoService.pageGreenareas(greenareaInfo, query);

		List<GreenareaInfo> greenareaInfoList = pages.getRecords();
		List<GreenareaInfoVO> greenareaInfoVOList = new ArrayList<GreenareaInfoVO>();
		greenareaInfoList.forEach(greenareaInfo_ -> {
			GreenareaInfoVO greenareaInfoVO = Objects.requireNonNull(BeanUtil.copy(greenareaInfo_, GreenareaInfoVO.class));
			QueryWrapper<GreenareaItem> queryWrapper = new QueryWrapper<GreenareaItem>();
			queryWrapper.lambda().eq(GreenareaItem::getGreenareaId,greenareaInfo_.getId());
			List<GreenareaItem> items =  greenareaItemService.list(queryWrapper);
			Integer treeNum = 0;
			if(ObjectUtil.isNotEmpty(items) && items.size() > 0 ){
				for(GreenareaItem item:items){
					treeNum = treeNum+Integer.parseInt(item.getItemCount());
				}
			}
			greenareaInfoVO.setTreeNum(treeNum.toString());
            greenareaInfoVO.setBindDevice(false);
            List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(greenareaInfoVO.getId(), CommonConstant.ENTITY_TYPE.GREEN).getData();
            if (null != deviceRelList && deviceRelList.size() > 0){
                greenareaInfoVO.setBindDevice(true);
            }
			greenareaInfoVOList.add(greenareaInfoVO);
		});
		IPage<GreenareaInfoVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
        iPage.setRecords(greenareaInfoVOList);
        return R.data(iPage);
	}

	/**
	 * 新增 绿化养护信息
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "新增绿化区域", notes = "新增绿化区域")
	@ApiLog(value = "新增绿化区域")
	public R save(@Valid @RequestBody GreenareaInfoDTO greenareaInfoDTO) {
        // 验证入参
        verifyParam(greenareaInfoDTO);

		Boolean save = greenareaInfoService.saveGreenareaInfo(greenareaInfoDTO);
		return R.status(save);
	}

	private void verifyParam(GreenareaInfoDTO greenareaInfoDTO){
	    QueryWrapper<GreenareaInfo> greenareaInfoQueryWrapper  = new QueryWrapper<GreenareaInfo>();
        greenareaInfoQueryWrapper.lambda().eq(GreenareaInfo::getRegionId,greenareaInfoDTO.getRegionId());
        List<GreenareaInfo> greenareaInfoList = greenareaInfoService.list(greenareaInfoQueryWrapper);
        if(ObjectUtil.isNotEmpty(greenareaInfoList) && greenareaInfoList.size() > 0){
            throw new ServiceException("该区域已设置绿化信息，不能重复设置");
        }
    }

	/**
	 * 修改 绿化养护信息
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "修改绿化区域", notes = "修改绿化区域")
	@ApiLog(value = "修改绿化区域")
	public R update(@Valid @RequestBody GreenareaInfoDTO greenareaInfoDTO) {
		return R.status(greenareaInfoService.updateGreenareaInfo(greenareaInfoDTO));
	}


	/**
	 * 删除 绿化养护信息
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "删除绿化区域", notes = "删除绿化区域")
	@ApiLog(value = "删除绿化区域")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(greenareaInfoService.removeGreenareaInfo(Func.toLongList(ids)));
	}

	/**
	 * 车辆360视图车辆信息-查绑定的车辆监控终端
	 */
	@GetMapping("/devices")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "根据ID查询绿化区域的监控设备", notes = "传入id")
	@ApiLog(value = "根据ID查询绿化区域的监控设备")
	public R<List<DeviceViewVO>> greenAreaDevices(@RequestParam Long greenId, BladeUser user) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(DeviceConstant.DeviceCategory.GREEN_MONITOR_DEVICE);

		List<Long> entityCategoryIdList = entityCategoryClient.getSubCategoryIdByParentCategoryId(DeviceConstant.DeviceCategory.GREEN_MONITOR_DEVICE).getData();

		if (entityCategoryIdList.size() > 0) {
			ids.addAll(entityCategoryIdList);
		}

		List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(greenId, CommonConstant.ENTITY_TYPE.GREEN).getData();
		List<DeviceViewVO> monotorViewVOList = new ArrayList<DeviceViewVO>();

		if (null != deviceRelList && deviceRelList.size() > 0) {

			for (DeviceRel deviceRel : deviceRelList) {
				Long deviceId = deviceRel.getDeviceId();
				DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
				Long id = deviceInfo.getEntityCategoryId();
				if (ids.contains(id)) {
					DeviceViewVO deviceViewVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, DeviceViewVO.class));
					deviceViewVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo.getEntityCategoryId()).getData());
					monotorViewVOList.add(deviceViewVO);
				}
			}
		}
		return R.data(monotorViewVOList);
	}
}
