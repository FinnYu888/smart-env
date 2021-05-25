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
package com.ai.apac.smartenv.flow.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.flow.entity.BladeFlow;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 工作流远程调用接口.
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_FLOW_NAME,
	fallback = IFlowClientFallback.class
)
public interface IFlowClient {

	String API_PREFIX = "/client";
	String START_PROCESS_INSTANCE_BY_ID = API_PREFIX + "start-process-instance-by-id";
	String START_PROCESS_INSTANCE_BY_KEY = API_PREFIX + "start-process-instance-by-key";
	String COMPLETE_TASK = API_PREFIX + "complete-task";
	String TASK_VARIABLE = API_PREFIX + "task-variable";
	String TASK_VARIABLES = API_PREFIX + "task-variables";
	String TASK_ID= API_PREFIX + "task-id";
	String TASK_VARIABLE_PROCESSINSTID = API_PREFIX+ "task-id-processinstId";

	/**
	 * 开启流程
	 *
	 * @param processDefinitionId 流程id
	 * @param businessKey         业务key
	 * @param variables           参数
	 * @return BladeFlow
	 */
	@PostMapping(START_PROCESS_INSTANCE_BY_ID)
	R<BladeFlow> startProcessInstanceById(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("businessKey") String businessKey, @RequestBody Map<String, Object> variables);

	/**
	 * 开启流程
	 *
	 * @param processDefinitionKey 流程标识
	 * @param businessKey          业务key
	 * @param variables            参数
	 * @return BladeFlow
	 */
	@PostMapping(START_PROCESS_INSTANCE_BY_KEY)
	R<String> startProcessInstanceByKey(@RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("businessKey") String businessKey, @RequestBody Map<String, Object> variables);

	/**
	 * 完成任务
	 *
	 * @param taskId            任务id
	 * @param processInstanceId 流程实例id
	 * @param comment           评论
	 * @param variables         参数
	 * @return R
	 */
	@PostMapping(COMPLETE_TASK)
	R completeTask(@RequestParam("taskId") String taskId, @RequestParam("taskName")String taskName,@RequestParam("processInstanceId") String processInstanceId, @RequestParam("comment") String comment, @RequestBody Map<String, Object> variables);

	/**
	 * 获取流程变量
	 *
	 * @param taskId       任务id
	 * @param variableName 变量名
	 * @return R
	 */
	@GetMapping(TASK_VARIABLE)
	R<Object> taskVariable(@RequestParam("taskId") String taskId, @RequestParam("variableName") String variableName);
	/**
	 * 获取流程变量
	 *
	 * @param taskId       任务id
	 * @param variableName 变量名
	 * @return R
	 */
	@GetMapping(TASK_VARIABLE_PROCESSINSTID)
	R<Object> taskVariableByProcessInstId(@RequestParam("processInstId") String processInstId, @RequestParam("variableName") String variableName);

	/**
	 * 获取流程变量集合
	 *
	 * @param taskId 任务id
	 * @return R
	 */
	@GetMapping(TASK_VARIABLES)
	R<Map<String, Object>> taskVariables(@RequestParam("taskId") String taskId);

	/**
	* 获取流程taskiD
	*/
	@GetMapping(TASK_ID)
	R<String> getTaskId(@RequestParam("processInstId") String processInstId);
}
