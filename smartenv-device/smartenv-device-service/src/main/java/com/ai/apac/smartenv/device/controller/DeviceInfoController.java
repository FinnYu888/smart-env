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
package com.ai.apac.smartenv.device.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.device.vo.DeviceCatyAndTypeVO;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.DeviceStatusVO;
import com.ai.apac.smartenv.device.vo.DeviceViewVO;
import com.ai.apac.smartenv.device.wrapper.DeviceInfoWrapper;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 记录设备信息 控制器
 *
 * @author Blade
 * @since 2020-02-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/deviceinfo")
@Api(value = "记录设备信息", tags = "记录设备信息接口")
@Slf4j
public class DeviceInfoController extends BladeController {


	private BladeLogger bladeLogger;

	private IDeviceInfoService deviceInfoService;
	private IDeviceRelService deviceRelService;

	private IDictClient dictClient;

	private IEntityCategoryClient entityCategoryClient;

	private MongoTemplate mongoTemplate;

	private IPersonClient personClient;

	private IVehicleClient vehicleClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deviceInfo")
	public R<DeviceInfoVO> detail(DeviceInfo deviceInfo) {
		DeviceInfo detail = deviceInfoService.getOne(Condition.getQueryWrapper(deviceInfo));
		return R.data(DeviceInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 记录设备信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入deviceInfo")
	public R<IPage<DeviceInfoVO>> list(DeviceInfo deviceInfo, Query query) {
		IPage<DeviceInfo> pages = deviceInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(deviceInfo));
		return R.data(DeviceInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 记录设备信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入deviceInfo")
	public R<IPage<DeviceInfoVO>> page(DeviceInfoVO deviceInfo, Query query) {
		IPage<DeviceInfoVO> pages = deviceInfoService.selectDeviceInfoPage(Condition.getPage(query), deviceInfo);
		return R.data(pages);
	}

	/**
	 * 新增 记录设备信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入deviceInfo")
	public R save(@Valid @RequestBody DeviceInfo deviceInfo) {
		return R.status(deviceInfoService.save(deviceInfo));
	}

	/**
	 * 修改 记录设备信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入deviceInfo")
	public R update(@Valid @RequestBody DeviceInfo deviceInfo) {
		return R.status(deviceInfoService.updateById(deviceInfo));
	}

	/**
	 * 新增或修改 记录设备信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deviceInfo")
	public R submit(@Valid @RequestBody DeviceInfo deviceInfo) {
		boolean result = deviceInfoService.saveOrUpdate(deviceInfo);
		DeviceCache.delDeviceByEntity(deviceInfo);
		return R.status(result);
	}


	/**
	 * 删除 记录设备信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceInfoService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 查询绑定设备信息
	 * TODO
	 */
	@GetMapping("/listForBinding")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询绑定人员信息", notes = "传入deviceInfo，vehicleId")
	public R<IPage<DeviceInfoVO>> listForBinding(DeviceInfo deviceInfo, Query query, Long vehicleId) {
		List<DeviceRel> relList = deviceRelService.listForBinding(deviceInfo, query, vehicleId);
		List<DeviceInfoVO> deviceInfoVOList = new ArrayList<>();
		// 根据设备id获取设备信息
		relList.forEach(relObj -> {
			Long deviceId = relObj.getDeviceId();
			DeviceInfo deviceInfoObj = deviceInfoService.getById(deviceId);
			DeviceInfoVO deviceInfoVO = DeviceInfoWrapper.build().entityVO(deviceInfoObj);
			if (relObj.getEntityId() != null) {
//				deviceInfoVO.setIsBinded(VehicleConstant.VehicleRelBind.TRUE);
			} else {
//				deviceInfoVO.setIsBinded(VehicleConstant.VehicleRelBind.FALSE);
			}
			deviceInfoVOList.add(deviceInfoVO);
		});
		// 构造page对象
		List<DeviceInfo> totalList = deviceInfoService.list(Condition.getQueryWrapper(deviceInfo));
		IPage<DeviceInfoVO> iPage = new Page<>(query.getCurrent(), query.getSize(), totalList.size());
		iPage.setRecords(deviceInfoVOList);
		return R.data(iPage);
	}

	/**
	 * 查询绑定设备信息
	 */
	@PostMapping("/deviceStatus")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询设备实时状态信息", notes = "deviceIds")
	public R<List<DeviceStatusVO>> getDeviceStatus(@ApiParam(value = "设备ID集合", required = true) @RequestParam String deviceIds) throws IOException {
		List<DeviceStatusVO> deviceStatusVOList = new ArrayList<DeviceStatusVO>();
		String[] deviceIdSet = deviceIds.split(",");
		if(deviceIdSet.length > 0){
			for(String deviceId:deviceIdSet){
				DeviceStatusVO deviceStatusVO = new DeviceStatusVO();
				//调用大数据
				JSONObject param = new JSONObject();
				param.put("deviceId",deviceId);
				String reStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.getDeviceStatus,param);
				if(StringUtil.isNotBlank(reStr) && JSONUtil.parseObj(reStr).get("code").equals(0)){
					JSONObject data = (JSONObject)JSONUtil.parseObj(reStr).get("data");
					String status = data.getStr("status");
					String deviceStatus = DeviceConstant.DeviceStatus.NO;
					if(!StringUtil.isBlank(status)){
						deviceStatus = status;
					}
					deviceStatusVO.setStatusCode(deviceStatus);
					deviceStatusVO.setStatusName(dictClient.getValue("device_status",deviceStatus).getData());
					deviceStatusVO.setDeviceId(deviceId);
					deviceStatusVOList.add(deviceStatusVO);
				}
			}

		}
		return R.data(deviceStatusVOList);

	}

	/**
	 * 分页 设施终端设备信息
	 */
	@GetMapping("/list/facility")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "设施终端设备查询", notes = "传入entityCategoryId, facilityId")
	public R<List<DeviceViewVO>> listFacilityDevice(@ApiParam(value = "设备实体类型Id") @RequestParam Long entityCategoryId,
													@ApiParam(value = "设施Id") @RequestParam Long facilityId) {
		return R.data(deviceInfoService.listFacilityDevice(entityCategoryId, facilityId));
	}

	/**
	 * 分页 设施终端设备信息
	 */
	@GetMapping("/catyAndTypelist")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "终端类型&设备型号树", notes = "用于SIM新增时添加终端类型&设备型号")
	public R<List<DeviceCatyAndTypeVO>> listDeviceCatyAndType() {
		List<EntityCategory> entityCategoryList = entityCategoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.DEVICE.toString()).getData();
		List<DeviceCatyAndTypeVO> deviceCatyAndTypeVOList = new ArrayList<DeviceCatyAndTypeVO>();
		entityCategoryList.forEach(entityCategory -> {
			List<DeviceInfo> deviceInfoList = deviceInfoService.listDevicesByParam(null,entityCategory.getId());
			if(null != deviceInfoList && deviceInfoList.size() > 0 ){
				DeviceCatyAndTypeVO deviceCatyAndTypeVO = new DeviceCatyAndTypeVO();
				deviceCatyAndTypeVO.setDeviceCategoryId(entityCategory.getId().toString());
				deviceCatyAndTypeVO.setDeviceCategoryName(entityCategory.getCategoryName());
				Set<String> deviceTypes = new HashSet<String>();
				deviceInfoList.forEach(deviceInfo -> {
					deviceTypes.add(deviceInfo.getDeviceType());
				});
				deviceCatyAndTypeVO.setDeviceTypes(new ArrayList<String>(deviceTypes));
				deviceCatyAndTypeVOList.add(deviceCatyAndTypeVO);
			}
		});
		return R.data(deviceCatyAndTypeVOList);
	}

	/**
	 * 分页 设施终端设备信息
	 */
	@GetMapping("/list4Sim")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "根据指定参数查询设备列表", notes = "根据指定参数查询设备列表")
	public R<List<DeviceInfoVO>> listDeviceInfo4Sim(DeviceInfo deviceInfo) {
		QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();
		if(ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId())){
			queryWrapper.lambda().eq(DeviceInfo::getEntityCategoryId,deviceInfo.getEntityCategoryId());
		}

		if(ObjectUtil.isNotEmpty(deviceInfo.getDeviceType())){
			queryWrapper.lambda().eq(DeviceInfo::getDeviceType,deviceInfo.getDeviceType());
		}

		if(ObjectUtil.isNotEmpty(deviceInfo.getDeviceCode())){
			queryWrapper.lambda().like(DeviceInfo::getDeviceCode,deviceInfo.getDeviceCode());
		}

		List<DeviceInfo> deviceInfoList = deviceInfoService.list(queryWrapper);
		return R.data(DeviceInfoWrapper.build().listVO(deviceInfoList));
	}

	/**
	 * 分页 设施终端设备信息
	 */
	@PostMapping("/status")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "更新终端实时ACC状态", notes = "传入id和状态")
	public R updateDeviceStatus(@ApiParam(value = "主键", required = true) @RequestParam String code,@ApiParam(value = "状态", required = true) @RequestParam Long status) {
		deviceInfoService.updateDeviceStatus(code,status);
		return R.status(true);
	}


	/**
	 * 分页 设施终端设备信息
	 */
	@GetMapping("/tsetMQ")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "测试接口根据入参更新设备上传信息", notes = "测试接口根据入参更新设备上传信息")
	public R tsetMQ(@ApiParam(value = "jsonString", required = true) @RequestParam String jsonString) {
//		String jsonString="{\"deviceCode\":\"171CB73FED0B\",\"indexList\":[{\"divisor\":1.0,\"finalIndexValue\":26.0,\"index\":\"illuminance\",\"indexName\":\"光照度\",\"indexValue\":26.0,\"unit\":\"Lux\"},{\"divisor\":1.0,\"finalIndexValue\":58.0,\"index\":\"nitrogen\",\"indexName\":\"土壤氮\",\"indexValue\":58.0,\"unit\":\"mg/kg\"},{\"divisor\":1.0,\"finalIndexValue\":78.0,\"index\":\"phosphorus\",\"indexName\":\"土壤磷\",\"indexValue\":78.0,\"unit\":\"mg/kg\"},{\"divisor\":1.0,\"finalIndexValue\":189.0,\"index\":\"potassium\",\"indexName\":\"土壤钾\",\"indexValue\":189.0,\"unit\":\"mg/kg\"},{\"divisor\":100.0,\"finalIndexValue\":5.99,\"index\":\"ph\",\"indexName\":\"土壤PH\",\"indexValue\":599.0,\"unit\":\"PH\"},{\"divisor\":10.0,\"finalIndexValue\":26.4,\"index\":\"temperature\",\"indexName\":\"土壤温度\",\"indexValue\":264.0,\"unit\":\"°C\"},{\"divisor\":1.0,\"finalIndexValue\":100.0,\"index\":\"humidity\",\"indexName\":\"土壤湿度\",\"indexValue\":100.0,\"unit\":\"RH(%)\"}]}";
		GreenScreenDeviceDTO positionMessage = com.alibaba.fastjson.JSONObject.parseObject(jsonString, GreenScreenDeviceDTO.class);
		org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
		query.addCriteria(Criteria.where("deviceCode").is(positionMessage.getDeviceCode()));
		Update update = new Update();
		update.set("indexList",positionMessage.getIndexList());
		UpdateResult result = mongoTemplate.updateFirst(query,update, OmnicConstant.mongoNmae.DEVICE_DATA);
		return R.status(true);
	}


}
