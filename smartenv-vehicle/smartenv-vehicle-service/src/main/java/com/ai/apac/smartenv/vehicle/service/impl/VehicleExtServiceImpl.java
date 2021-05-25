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
package com.ai.apac.smartenv.vehicle.service.impl;

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleExt;
import com.ai.apac.smartenv.vehicle.vo.VehicleExtVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.mapper.VehicleExtMapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleExtService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.ArrayList;
import java.util.List;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 车辆信息扩展表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class VehicleExtServiceImpl extends BaseServiceImpl<VehicleExtMapper, VehicleExt> implements IVehicleExtService {

	@Override
	public IPage<VehicleExtVO> selectVehicleExtPage(IPage<VehicleExtVO> page, VehicleExtVO vehicleExt) {
		return page.setRecords(baseMapper.selectVehicleExtPage(page, vehicleExt));
	}

	@Override
	public boolean savePicture(Long vehicleId, Long attrId, String attrName, String attrValue) {
		VehicleExt vehicleExt = new VehicleExt();
		vehicleExt.setVehicleId(vehicleId);
		vehicleExt.setAttrId(attrId);
		vehicleExt.setAttrName(attrName);
		vehicleExt.setAttrValue(attrValue);
		return save(vehicleExt);
	}

	@Override
	public void updatePicture(Long vehicleId, String attrValue, Long attrId, String attrName) {
		VehicleExt vehicleExt = new VehicleExt();
		vehicleExt.setVehicleId(vehicleId);
		vehicleExt.setAttrId(attrId);
		VehicleExt picture = getOne(Condition.getQueryWrapper(vehicleExt).last("LIMIT 1"));
		if (picture != null && StringUtil.isNotBlank(attrValue)) {
			picture.setAttrValue(attrValue);
			updateById(picture);
		} else if (picture == null && StringUtil.isNotBlank(attrValue)) {
			savePicture(vehicleId, attrId, attrName, attrValue);
		}
	}

	@Override
	public VehicleExt getVehicleAttr(Long vehicleId, Long attrId) {
		QueryWrapper<VehicleExt> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("vehicle_id", vehicleId).eq("attr_id", attrId);
		return getOne(queryWrapper.last("LIMIT 1"));
	}

	@Override
	public void removeVehicleAttr(Long vehicleId) {
		QueryWrapper<VehicleExt> queryWrapper = new QueryWrapper<VehicleExt>();
		queryWrapper.eq("vehicle_id", vehicleId);
		List<VehicleExt> extList = list(queryWrapper);
		if (extList != null && !extList.isEmpty()) {
			List<Long> paramList = new ArrayList<>();
			extList.forEach(ext -> {
				paramList.add(ext.getId());
			});
			deleteLogic(paramList);
		}
	}

	@Override
	public VehicleInfoVO getPictures(VehicleInfoVO vehicleInfoVO) {
		Long vehicleId = vehicleInfoVO.getId();
		// 车辆照片
		VehicleExt picture = getVehicleAttr(vehicleId, VehicleConstant.VehicleExtAttr.PIC_ATTR_ID);
		if (picture != null) {
			vehicleInfoVO.setVehiclePicName(picture.getAttrValue());
		} else {
			vehicleInfoVO.setVehiclePicName(DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_VEHICLE));
		}
		// 行驶证正页
		picture = getVehicleAttr(vehicleId, VehicleConstant.VehicleExtAttr.DRIVING_PIC_FIRST_ATTR_ID);
		if (picture != null) {
			vehicleInfoVO.setDrivingPicFirstName(picture.getAttrValue());
		}
		// 行驶证副页
		picture = getVehicleAttr(vehicleId, VehicleConstant.VehicleExtAttr.DRIVING_PIC_SECOND_ATTR_ID);
		if (picture != null) {
			vehicleInfoVO.setDrivingPicSecondName(picture.getAttrValue());
		}
		return vehicleInfoVO;
	}
	
}
