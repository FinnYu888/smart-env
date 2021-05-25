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
package com.ai.apac.smartenv.device.service.impl;

import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.vo.DeviceExtVO;
import com.ai.apac.smartenv.device.mapper.DeviceExtMapper;
import com.ai.apac.smartenv.device.service.IDeviceExtService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 记录设备属性 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class DeviceExtServiceImpl extends BaseServiceImpl<DeviceExtMapper, DeviceExt> implements IDeviceExtService {

	@Override
	public IPage<DeviceExtVO> selectDeviceExtPage(IPage<DeviceExtVO> page, DeviceExtVO deviceExt) {
		return page.setRecords(baseMapper.selectDeviceExtPage(page, deviceExt));
	}

	@Override
	public List<DeviceExt> getExtInfoByDeviceId(Long id) {
		QueryWrapper<DeviceExt> queryWrapper = new QueryWrapper<DeviceExt>();
		queryWrapper.eq("device_id",id);
		List<DeviceExt> relList = baseMapper.selectList(queryWrapper);
		return relList;
	}

	@Override
	public List<DeviceExt> getExtInfoByParam(DeviceExt deviceExt) {
		QueryWrapper<DeviceExt> queryWrapper = new QueryWrapper<DeviceExt>();
		if(null != deviceExt.getDeviceId()){
			queryWrapper.eq("device_id",deviceExt.getDeviceId());
		}
		if(null != deviceExt.getAttrId()){
			queryWrapper.eq("attr_id",deviceExt.getAttrId());
		}
		if(null != deviceExt.getAttrValue()){
			queryWrapper.eq("attr_value",deviceExt.getAttrValue());
		}
		List<DeviceExt> relList = baseMapper.selectList(queryWrapper);
		return relList;
	}

	@Override
	public Integer countExtInfoByParam(DeviceExt deviceExt) {
		QueryWrapper<DeviceExt> queryWrapper = new QueryWrapper<DeviceExt>();
		if(null != deviceExt.getAttrId()){
			queryWrapper.eq("attr_id",deviceExt.getAttrId());
		}
		if(null != deviceExt.getAttrValue()){
			queryWrapper.eq("attr_value",deviceExt.getAttrValue());
		}
		return baseMapper.selectCount(queryWrapper);
	}


}
