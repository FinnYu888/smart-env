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
package com.ai.apac.flow.engine.service.impl;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.flow.engine.mapper.FlowTaskPendingMapper;
import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.smartenv.flow.vo.FlowTaskPendingVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-08-26
 */
@Service
public class FlowTaskPendingServiceImpl extends BaseServiceImpl<FlowTaskPendingMapper, FlowTaskPending> implements IFlowTaskPendingService {

	@Override
	public IPage<FlowTaskPendingVO> selectFlowTaskPendingPage(IPage<FlowTaskPendingVO> page, FlowTaskPendingVO flowTaskPending) {
		return page.setRecords(baseMapper.selectFlowTaskPendingPage(page, flowTaskPending));
	}

	@Override
	public List<Long> getFlowTask(Long personId, Long postionId, List<Long> roleIds,String taskNode) {
		List<FlowTaskPending> pendings =  baseMapper.getFlowTask(personId,postionId,roleIds,taskNode);
		List<Long> orderIds = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(pendings)) {

			pendings.forEach(pending->{
				orderIds.add(pending.getOrderId());
			});

		}
		return orderIds;
	}

	@Override
	public void finishFlowTaskPending(String flowName, String currentTask, Long orderId) {
		LambdaQueryWrapper<FlowTaskPending> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(FlowTaskPending::getFlowName,flowName);
		queryWrapper.eq(FlowTaskPending::getTaskNode,currentTask);
		queryWrapper.eq(FlowTaskPending::getOrderId,orderId);
		queryWrapper.eq(FlowTaskPending::getStatus,1);
		FlowTaskPending flowTaskPending = baseMapper.selectOne(queryWrapper);

		flowTaskPending.setStatus(0);
		baseMapper.updateById(flowTaskPending);
	}

	@Override
	public List<FlowTaskPending> getTaskDonePermission(Long orderId, String taskNode, Long personId, Long postionId, String roleIds) {
		List<Long> roleIdList =null;
		if (StringUtils.isNotBlank(roleIds)) {
			roleIdList = Func.toLongList(roleIds);
		}
		return baseMapper.getTaskDonePermission(orderId,taskNode,personId,postionId,roleIdList);
	}

	@Override
	public FlowTaskPending getTodoTask(Long orderId, String flowCode) {
		PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
		Long personId = personUserRel.getPersonId();
		String roleIds = AuthUtil.getUser().getRoleId();

		//查询该登录人的岗位
		Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
		Long positionId = null;
		if (null != person) {
			positionId = person.getPersonPositionId();
		}

		List<FlowTaskPending> list = getTaskDonePermission(orderId, flowCode,personId,positionId,roleIds);
		if (CollectionUtil.isEmpty(list)) {
			throw new ServiceException("当前没有待处理的任务");
		}
		return list.get(0);
	}

}
