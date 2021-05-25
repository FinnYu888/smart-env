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
package com.ai.apac.smartenv.inventory.service.impl;

import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import com.ai.apac.smartenv.inventory.mapper.ResOrderMilestoneMapper;
import com.ai.apac.smartenv.inventory.service.IResOrderMilestoneService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Service
public class ResOrderMilestoneServiceImpl extends BaseServiceImpl<ResOrderMilestoneMapper, ResOrderMilestone> implements IResOrderMilestoneService {

	@Override
	public IPage<ResOrderMilestoneVO> selectResOrderMilestonePage(IPage<ResOrderMilestoneVO> page, ResOrderMilestoneVO resOrderMilestone) {
		return page.setRecords(baseMapper.selectResOrderMilestonePage(page, resOrderMilestone));
	}

	@Override
	public Boolean updateOrderMilestoneByCond(ResOrderMilestone resOrderMilestone) {
		baseMapper.updateOrderMilestoneByCond(resOrderMilestone.getDoneResult(),resOrderMilestone.getDoneRemark(),resOrderMilestone.getAssignmentId(),resOrderMilestone.getAssignmentName(),resOrderMilestone.getOrderId(),resOrderMilestone.getTaskDefineName());
		return true;
	}

	@Override
	public Boolean updateDeliverOrderMilestone(ResOrderMilestoneVO resOrderMilestone) {
		baseMapper.updateDeliverOrderMilestone(resOrderMilestone.getTaskId(),resOrderMilestone.getOrderId(),resOrderMilestone.getTaskDefineName());
		return true;
	}

	@Override
	public void saveOrderMileStone(ResOrderMilestone orderMilestone) {
		 baseMapper.insert(orderMilestone);
	}


}
