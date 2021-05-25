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

import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.vehicle.entity.RefuelInfo;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.RefuelInfoVO;
import com.ai.apac.smartenv.vehicle.mapper.RefuelInfoMapper;
import com.ai.apac.smartenv.vehicle.service.IRefuelInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 记录加油信息 服务实现类
 *
 * @author Blade
 * @since 2020-08-13
 */
@Service
@Slf4j
@AllArgsConstructor
public class RefuelInfoServiceImpl extends BaseServiceImpl<RefuelInfoMapper, RefuelInfo> implements IRefuelInfoService {
	@Autowired
	private IPersonUserRelClient personUserRelClient;
	@Override
	public IPage<RefuelInfoVO> selectRefuelInfoPage(IPage<RefuelInfoVO> page, RefuelInfoVO refuelInfo) {
		return page.setRecords(baseMapper.selectRefuelInfoPage(page, refuelInfo));
	}

	@Override
	public IPage<RefuelInfo> page(RefuelInfo refuelInfo, Query query,String queryTime,String queryVehicleId) {
		BladeUser user = AuthUtil.getUser();
		boolean isAdmin = false;
		if("admin".equals(user.getRoleGroup())||"administrator".equals(user.getRoleGroup())){
			isAdmin=true;
		}
		List<Long> queryVehicleList = new ArrayList<>();
		if(StringUtil.isBlank(queryVehicleId)&&!isAdmin){
			R<List<Long>> retlist = personUserRelClient.getVehicleByUserId(user.getUserId());
			if(null==retlist&&null==retlist.getData()&&retlist.getData().size()==0){
				IPage<RefuelInfo> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
				return emptyPage;
			}
			queryVehicleList = retlist.getData();
		}
		QueryWrapper<RefuelInfo> queryWrapper = generateQueryWrapper(refuelInfo,queryTime,queryVehicleId,queryVehicleList,isAdmin);
		IPage<RefuelInfo> pages = page(Condition.getPage(query), queryWrapper);
		return pages;
	}

	private QueryWrapper<RefuelInfo> generateQueryWrapper(RefuelInfo refuelInfo,String queryTime,String queryVehicleId,List<Long> queryVehicleList,Boolean isAdmin) {
		QueryWrapper<RefuelInfo> queryWrapper = new QueryWrapper<>();
		BladeUser user = AuthUtil.getUser();
		if (refuelInfo.getId() != null) {
			queryWrapper.eq("id", refuelInfo.getId());
		}
		if (StringUtils.isNotBlank(queryTime)) {
			queryWrapper.ge("refuel_time", dateC(queryTime));
		}

		if (StringUtils.isNotBlank(queryVehicleId)) {
			queryWrapper.eq("vehicle_id", queryVehicleId);
		}else if (isAdmin){
			//如果是admin 且未选择具体车辆 默认看所有
		}else {
			queryWrapper.in("vehicle_id", queryVehicleList);
		}
		if (StringUtils.isNotBlank(refuelInfo.getTenantId())) {
			queryWrapper.eq("tenant_id", refuelInfo.getTenantId());
		} else {
			if (user != null) {
				queryWrapper.eq("tenant_id", user.getTenantId());
			}
		}
		queryWrapper.eq("is_deleted",0);
		queryWrapper.orderByDesc("refuel_time");
		return queryWrapper;
	}

	private String dateC (String inputDate){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();

			c.setTime(new Date());
			int numb = Integer.parseInt(inputDate.substring(1));
		if(inputDate.startsWith("d")){
			c.add(Calendar.DATE, - numb);
		}else if(inputDate.startsWith("m")){
			c.add(Calendar.MONTH, - numb);
		}
			Date d = c.getTime();
			String time = format.format(d);
			return time;
	}


}
