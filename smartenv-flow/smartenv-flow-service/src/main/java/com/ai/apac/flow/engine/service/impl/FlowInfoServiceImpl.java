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

import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.smartenv.flow.entity.FlowInfo;
import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.smartenv.flow.vo.FlowInfoDetailVO;
import com.ai.apac.smartenv.flow.vo.FlowInfoVO;
import com.ai.apac.flow.engine.mapper.FlowInfoMapper;
import com.ai.apac.flow.engine.service.IFlowInfoService;
import com.ai.apac.smartenv.flow.vo.FlowTaskAllotVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-09-07
 */
@Service
public class FlowInfoServiceImpl extends BaseServiceImpl<FlowInfoMapper, FlowInfo> implements IFlowInfoService {
	@Autowired
	private IFlowTaskAllotService flowTaskAllotService;
	@Override
	public IPage<FlowInfoVO> selectFlowInfoPage(IPage<FlowInfoVO> page, FlowInfoVO flowInfo) {
		return page.setRecords(baseMapper.selectFlowInfoPage(page, flowInfo));
	}

	@Override
	public IPage<FlowInfo> selectFlowInfoPage(IPage<FlowInfo> page, QueryWrapper queryWrapper) {
		return baseMapper.selectPage(page,queryWrapper);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void modifyFlowInfo(FlowInfoDetailVO flowInfoDetailVO) {
		FlowInfo flowInfo = getById(flowInfoDetailVO.getId());
		if (null == flowInfo) {

		}
		flowInfo.setConfigFlag(1);
		flowInfo.setFlowName(flowInfoDetailVO.getFlowName());
		flowInfo.setRemark(flowInfoDetailVO.getRemark());
		updateById(flowInfo);
		List<FlowTaskAllotVO> flowTaskAllotVOS = flowInfoDetailVO.getTaskAllotVOList();
		if (CollectionUtil.isNotEmpty(flowTaskAllotVOS)) {
			for (FlowTaskAllotVO flowTaskAllotVO:flowTaskAllotVOS) {
				flowTaskAllotService.saveOrUpdate(flowTaskAllotVO);
			}
		}
	}
	@Override
	public boolean checkFlowInfoConfig(String flowCode, String tenantId) {
		boolean flag = false;
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("flow_code",flowCode);
		queryWrapper.eq("tenant_id",tenantId);
		queryWrapper.eq("config_flag",1);
		queryWrapper.eq("status",1);
		FlowInfo flowInfo =getOne(queryWrapper,false);
		if (null != flowInfo) {
			QueryWrapper query = new QueryWrapper();
			query.eq("tenant_id",tenantId);
			query.eq("status",1);
			query.eq("flow_code",flowCode);
			List<FlowTaskAllot> flowTaskAllots = flowTaskAllotService.queryFlowTaskAllotList(query);
			if (CollectionUtil.isNotEmpty(flowTaskAllots)) {
				flag = true;
			}
		}

		return flag;
	}

}
