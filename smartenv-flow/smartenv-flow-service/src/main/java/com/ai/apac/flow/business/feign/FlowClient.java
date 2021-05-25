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
package com.ai.apac.flow.business.feign;

import com.ai.apac.flow.business.service.FlowBusinessService;
import com.ai.apac.smartenv.flow.entity.BladeFlow;
import com.ai.apac.smartenv.flow.feign.IFlowClient;
import com.ai.apac.smartenv.flow.utils.TaskUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.omg.CORBA.SystemException;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;



/**
 * 流程远程调用实现类
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
public class FlowClient implements IFlowClient {

	private RuntimeService runtimeService;
	private IdentityService identityService;
	private TaskService taskService;
	@Autowired
	private FlowBusinessService flowBusinessService;

	@Override
	@PostMapping(START_PROCESS_INSTANCE_BY_ID)
	public R<BladeFlow> startProcessInstanceById(String processDefinitionId, String businessKey, @RequestBody Map<String, Object> variables) {
		// 设置流程启动用户
		identityService.setAuthenticatedUserId(TaskUtil.getTaskUser());
		// 开启流程
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
		// 组装流程通用类
		BladeFlow flow = new BladeFlow();
		flow.setProcessInstanceId(processInstance.getId());

		return R.data(flow);
	}

	@Override
	@PostMapping(START_PROCESS_INSTANCE_BY_KEY)
	public R<String> startProcessInstanceByKey(String processDefinitionKey, String businessKey, @RequestBody Map<String, Object> variables) {
		// 设置流程启动用户
		//identityService.setAuthenticatedUserId(TaskUtil.getTaskUser());
		// 开启流程
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
		// 组装流程通用类
		/*BladeFlow flow = new BladeFlow();
		flow.setProcessInstanceId(processInstance.getId());*/
		return R.data(processInstance.getProcessInstanceId());
	}

	@Override
	@PostMapping(COMPLETE_TASK)
	public R completeTask(String taskId,String taskName, String processInstanceId, String comment, @RequestBody Map<String, Object> variables) {

		flowBusinessService.completeTask(taskId,taskName,processInstanceId,comment,variables);

		return R.success("流程提交成功");
	}

	@Override
	@GetMapping(TASK_VARIABLE)
	public R<Object> taskVariable(String taskId, String variableName) {
		return R.data(taskService.getVariable(taskId, variableName));
	}

	@Override
	public R<Object> taskVariableByProcessInstId(String processInstId, String variableName) {
		String taskId = taskService.createTaskQuery().processInstanceId(processInstId).singleResult().getId();
		if (StringUtils.isNotEmpty(taskId)) {
			throw new ServiceException("没有获取到对应的任务号");
		}
		return  R.data(taskService.getVariable(taskId,variableName));

	}

	@Override
	@GetMapping(TASK_VARIABLES)
	public R<Map<String, Object>> taskVariables(String taskId) {
		return R.data(taskService.getVariables(taskId));
	}

	@Override
	public R<String> getTaskId(String processInstId) {
		return R.data(taskService.createTaskQuery().processInstanceId(processInstId).singleResult().getId());
	}


}
