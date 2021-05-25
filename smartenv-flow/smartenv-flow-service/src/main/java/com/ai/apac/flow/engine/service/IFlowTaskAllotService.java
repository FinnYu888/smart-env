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

import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.smartenv.flow.vo.FlowTaskAllotVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-08-26
 */
public interface IFlowTaskAllotService extends BaseService<FlowTaskAllot> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param flowTaskAllot
	 * @return
	 */
	IPage<FlowTaskAllotVO> selectFlowTaskAllotPage(IPage<FlowTaskAllotVO> page, FlowTaskAllotVO flowTaskAllot);

	/**
	* 更新待处理任务为完成状态
	* @author 66578
	*/
    boolean finishTask(String flowName,String workflowId, Map<String, Object> paramMap, Long orderId, String currentTask);
    /**
    * 根据条件查询
    * @author 66578
    */
    List<FlowTaskAllot> queryFlowTaskAllotList(QueryWrapper queryWrapper);

    /**
    * 新增待处理节点
    * @author 66578
    */
    void createFlowTask(String flowName, String taskNode, Long orderId, String tenantId);


}
