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
package com.ai.apac.smartenv.workarea.service.impl;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.vo.WorkareaNodeVO;
import com.ai.apac.smartenv.workarea.mapper.WorkareaNodeMapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaNodeWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 工作区域节点信息 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
@AllArgsConstructor
public class WorkareaNodeServiceImpl extends BaseServiceImpl<WorkareaNodeMapper, WorkareaNode> implements IWorkareaNodeService {

	private CoordsTypeConvertUtil coordsTypeConvertUtil;

	@Override
	public IPage<WorkareaNodeVO> selectWorkareaNodePage(IPage<WorkareaNodeVO> page, WorkareaNodeVO workareaNode) {
		return page.setRecords(baseMapper.selectWorkareaNodePage(page, workareaNode));
	}

	@Override
	public List<WorkareaNodeVO> queryWorkareaNodeVOListByParam(WorkareaNode workareaNode, String tenantId) {
		QueryWrapper<WorkareaNode> queryWrapper = new QueryWrapper<>();

		if(workareaNode != null && workareaNode.getWorkareaId() != null) {
			queryWrapper.like("workarea_id",workareaNode.getWorkareaId());
		}
		if(tenantId != null ) {
			queryWrapper.eq("tenant_id",tenantId);
		}
//		queryWrapper.orderByDesc("update_time");
		List<WorkareaNode> workareaNodeList = this.list(queryWrapper);
		coordsTypeConvertUtil.toWebConvert(workareaNodeList);
		List<WorkareaNodeVO> workareaNodeVOS = WorkareaNodeWrapper.build().listVO(workareaNodeList);


		return workareaNodeVOS;
	}
}
