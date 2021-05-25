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

import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.flow.engine.mapper.FlowTaskAllotMapper;
import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.smartenv.flow.vo.FlowTaskAllotVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-08-26
 */
@Service
public class FlowTaskAllotServiceImpl extends BaseServiceImpl<FlowTaskAllotMapper, FlowTaskAllot> implements IFlowTaskAllotService {
	@Autowired
	private TaskService taskService;
	@Autowired
	private IFlowTaskPendingService flowTaskPendingService;
	@Override
	public IPage<FlowTaskAllotVO> selectFlowTaskAllotPage(IPage<FlowTaskAllotVO> page, FlowTaskAllotVO flowTaskAllot) {
		return page.setRecords(baseMapper.selectFlowTaskAllotPage(page, flowTaskAllot));

	}

	@Override
	public boolean finishTask(String flowName,String workflowId, Map<String, Object> paramMap, Long orderId, String currentTask) {
		String taskId = null;
		Task task = taskService.createTaskQuery().processInstanceId(workflowId).singleResult();
		if (null != task && task.getTaskDefinitionKey().equals(currentTask)) {
			taskId = task.getId();
		}else {
			return false;
		}

		// 非空判断
		if (Func.isEmpty(paramMap)) {
			paramMap = Kv.create();
		}
		// 完成任务
		taskService.complete(taskId, paramMap);

		//更新待处理任务
		flowTaskPendingService.finishFlowTaskPending(flowName,currentTask,orderId);
		return true;
	}

	@Override
	public List<FlowTaskAllot> queryFlowTaskAllotList(QueryWrapper queryWrapper) {
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createFlowTask(String flowName, String taskNode, Long orderId, String tenantId) {
		QueryWrapper<FlowTaskAllot> queryWrapper = new QueryWrapper();
		queryWrapper.eq("flow_code",flowName);
		queryWrapper.eq("task_node",taskNode);
		queryWrapper.eq("tenant_id",tenantId);
		queryWrapper.eq("status",1);
		queryWrapper.eq("is_deleted",0);
		FlowTaskAllot flowTaskAllot = getOne(queryWrapper);

		FlowTaskPending flowTaskPending = new FlowTaskPending();
		flowTaskPending.setFlowName(flowName);
		flowTaskPending.setTaskNode(taskNode);
		flowTaskPending.setDoneType(flowTaskAllot.getDoneType());
		flowTaskPending.setDoneValue(flowTaskAllot.getDoneValue());
		flowTaskPending.setOrderId(orderId);
		flowTaskPendingService.save(flowTaskPending);
	}




}
