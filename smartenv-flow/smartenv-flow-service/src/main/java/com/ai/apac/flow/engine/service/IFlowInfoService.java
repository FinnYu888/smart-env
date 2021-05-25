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

import com.ai.apac.smartenv.flow.entity.FlowInfo;
import com.ai.apac.smartenv.flow.vo.FlowInfoDetailVO;
import com.ai.apac.smartenv.flow.vo.FlowInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-09-07
 */
public interface IFlowInfoService extends BaseService<FlowInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param flowInfo
	 * @return
	 */
	IPage<FlowInfoVO> selectFlowInfoPage(IPage<FlowInfoVO> page, FlowInfoVO flowInfo);

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @return
	 */
	IPage<FlowInfo> selectFlowInfoPage(IPage<FlowInfo> page, QueryWrapper queryWrapper);

	/**
	* 修改流程配置
	* @author 66578
	*/
    void modifyFlowInfo(FlowInfoDetailVO flowInfoDetailVO);

	/**
	 * 检查流程是否配置
	 * @author 66578
	 */
	boolean checkFlowInfoConfig(String flowName,String tenantId);
}
