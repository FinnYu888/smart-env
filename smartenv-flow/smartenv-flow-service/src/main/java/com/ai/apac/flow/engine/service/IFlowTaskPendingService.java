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
package com.ai.apac.flow.engine.service;

import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.smartenv.flow.vo.FlowTaskPendingVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-08-26
 */
public interface IFlowTaskPendingService extends BaseService<FlowTaskPending> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param flowTaskPending
	 * @return
	 */
	IPage<FlowTaskPendingVO> selectFlowTaskPendingPage(IPage<FlowTaskPendingVO> page, FlowTaskPendingVO flowTaskPending);

	/**
	* 查询人员待处理任务列表
	* @author 66578
	*/
	List<Long>  getFlowTask(Long personIds, Long postionIds, List<Long> roleIds,String taskNode);

	/**
	* 更新待处理任务为已完成状态
	* @author 66578
	*/
    void finishFlowTaskPending(String flowName, String currentTask, Long orderId);

    /**
    * 查询当前用户是否有处理权限
    * @author 66578
    */
	List<FlowTaskPending> getTaskDonePermission(Long orderId, String taskNode, Long personId, Long postionId, String roleIds);
	/**
	* 查询该用户待处理任务
	* @author 66578
	*/
	FlowTaskPending getTodoTask(Long orderId,String flowCode);
}
