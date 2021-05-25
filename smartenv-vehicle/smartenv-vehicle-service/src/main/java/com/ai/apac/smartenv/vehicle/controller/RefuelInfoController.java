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
package com.ai.apac.smartenv.vehicle.controller;

import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleDriverVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.vehicle.entity.RefuelInfo;
import com.ai.apac.smartenv.vehicle.vo.RefuelInfoVO;
import com.ai.apac.smartenv.vehicle.wrapper.RefuelInfoWrapper;
import com.ai.apac.smartenv.vehicle.service.IRefuelInfoService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录加油信息 控制器
 *
 * @author Blade
 * @since 2020-08-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/refuelinfo")
@Api(value = "记录加油信息", tags = "记录加油信息接口")
public class RefuelInfoController extends BladeController {

	private IRefuelInfoService refuelInfoService;
	private IWorkareaClient workareaClient;
	private IOssClient ossClient;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入refuelInfo")
	public R<RefuelInfoVO> detail(RefuelInfo refuelInfo) {
		RefuelInfo detail = refuelInfoService.getOne(Condition.getQueryWrapper(refuelInfo));
		RefuelInfoVO refuelInfoVO = RefuelInfoWrapper.build().entityVO(detail);
		return R.data(getRefuelInfo(refuelInfoVO));
	}

	private RefuelInfoVO getRefuelInfo(RefuelInfoVO refuelInfoVO) {
		if (refuelInfoVO == null ) {
			return refuelInfoVO;
		}
			//根据车辆id获取车牌,从缓存
			VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, refuelInfoVO.getVehicleId());
			if(null!=vehicleInfo)
				refuelInfoVO.setPlateNmuber(vehicleInfo.getPlateNumber());
			//添加区域id,单次查询全区域名字
		    R<WorkareaInfo> workareaInfoR = workareaClient.getWorkInfoById(refuelInfoVO.getAreaId());
		    if(null!=workareaInfoR&&null!=workareaInfoR.getData())
				refuelInfoVO.setAreaName(workareaInfoR.getData().getAreaName());
		    //加油人
			Person person = PersonCache.getPersonById(null, refuelInfoVO.getPersonId());
			if(null!=person){
			    if(StringUtils.isBlank(person.getJobNumber())){
                    refuelInfoVO.setPersonName(person.getPersonName());
                }else{
                    refuelInfoVO.setPersonName(person.getPersonName()+"("+person.getJobNumber()+")");
                }
			}
			//油品
			refuelInfoVO.setOilTypeName(DictCache.getValue(VehicleConstant.VehicleRefuel.REFUEL_OIL,refuelInfoVO.getOilType()));
			//车辆仪表盘图片
		    if (StringUtils.isNotBlank(refuelInfoVO.getPicCarDb()))
		    	refuelInfoVO.setPicCarDbURL(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, refuelInfoVO.getPicCarDb()).getData());
			//加油站仪表照片
		    if (StringUtils.isNotBlank(refuelInfoVO.getPicGasBd()))
				refuelInfoVO.setPicGasDdURL(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, refuelInfoVO.getPicGasBd()).getData());
			//加油小票
		    if (StringUtils.isNotBlank(refuelInfoVO.getPicReceipt()))
				refuelInfoVO.setPicReceiptURL(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, refuelInfoVO.getPicReceipt()).getData());


		return refuelInfoVO;
	}

	/**
	 * 分页 记录加油信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入refuelInfo")
	public R<IPage<RefuelInfoVO>> list(RefuelInfo refuelInfo, Query query,String queryTime,String queryVehicleId) {
		//配置的查询时间是 blade_dict 'refuel_query_time';
		//由d 和 m 开头 分别代表 以天数和月份计算, 需求是前多少日期, 所以算个日期比较即可
		IPage<RefuelInfo> pages = refuelInfoService.page(refuelInfo,query,queryTime,queryVehicleId);
		IPage<RefuelInfoVO> pageVO = RefuelInfoWrapper.build().pageVO(pages);
		List<RefuelInfoVO> records = pageVO.getRecords();
		getRefuelInfoList(records);
		return R.data(pageVO);
	}

	private List<RefuelInfoVO> getRefuelInfoList(List<RefuelInfoVO> refuelInfoVOs) {
		if (refuelInfoVOs == null ||refuelInfoVOs.size()==0) {
			return refuelInfoVOs;
		}
		Map<Long, String> areaMap = new HashMap<>();
		List<Long> areaIds = new ArrayList<>();
		for (RefuelInfoVO refuelInfoVO : refuelInfoVOs) {
			//根据车辆id获取车牌,从缓存
			VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, refuelInfoVO.getVehicleId());
			if(null!=vehicleInfo)
				refuelInfoVO.setPlateNmuber(vehicleInfo.getPlateNumber());
			//添加区域id,单次查询全区域名字
			areaIds.add(refuelInfoVO.getAreaId());

			Person person = PersonCache.getPersonById(null, refuelInfoVO.getPersonId());
            if(null!=person){
                if(StringUtils.isBlank(person.getJobNumber())){
                    refuelInfoVO.setPersonName(person.getPersonName());
                }else{
                    refuelInfoVO.setPersonName(person.getPersonName()+"("+person.getJobNumber()+")");
                }
            }
			//油品
			refuelInfoVO.setOilTypeName(DictCache.getValue(VehicleConstant.VehicleRefuel.REFUEL_OIL,refuelInfoVO.getOilType()));
		}
		//获取 workarea信息
		R<Map<Long,String>> areaRturn = workareaClient.getWorkInfoByIds(areaIds);
		if(null!=areaRturn&&!"400".equals(areaRturn.getCode())){
			areaMap = areaRturn.getData();
			for (RefuelInfoVO refuelInfoVO : refuelInfoVOs) {
				Long areaid = refuelInfoVO.getAreaId();
				if(null!=areaMap){
					if (areaMap.containsKey(areaid)) {
						refuelInfoVO.setAreaName(areaMap.get(areaid));
					}
				}
			}
		}

		return refuelInfoVOs;
	}


//	/**
//	 * 自定义分页 记录加油信息
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入refuelInfo")
//	public R<IPage<RefuelInfoVO>> page(RefuelInfoVO refuelInfo, Query query) {
//		IPage<RefuelInfoVO> pages = refuelInfoService.selectRefuelInfoPage(Condition.getPage(query), refuelInfo);
//		return R.data(pages);
//	}

	/**
	 * 新增 记录加油信息
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入refuelInfo")
	public R save(@Valid @RequestBody RefuelInfo refuelInfo) {
//		refuelInfoService.save(refuelInfo);
		return R.status(refuelInfoService.save(refuelInfo));
	}

	/**
	 * 修改 记录加油信息
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入refuelInfo")
	public R update(@Valid @RequestBody RefuelInfo refuelInfo) {
		return R.status(refuelInfoService.updateById(refuelInfo));
	}

//	/**
//	 * 新增或修改 记录加油信息
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入refuelInfo")
//	public R submit(@Valid @RequestBody RefuelInfo refuelInfo) {
//		return R.status(refuelInfoService.saveOrUpdate(refuelInfo));
//	}

	
	/**
	 * 删除 记录加油信息
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(refuelInfoService.deleteLogic(Func.toLongList(ids)));
	}
}
