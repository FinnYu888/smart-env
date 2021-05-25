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
package com.ai.apac.smartenv.inventory.service;

import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-02-25
 */
public interface IResOrderMilestoneService extends BaseService<ResOrderMilestone> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resOrderMilestone
	 * @return
	 */
	IPage<ResOrderMilestoneVO> selectResOrderMilestonePage(IPage<ResOrderMilestoneVO> page, ResOrderMilestoneVO resOrderMilestone);

	/**
	*创建milestone
	*/
    //Boolean createMilestone( ResOrderMilestone resOrderMilestone);
	/**
	* 更新状态
	*/
	Boolean updateOrderMilestoneByCond(ResOrderMilestone resOrderMilestone);

	Boolean updateDeliverOrderMilestone(ResOrderMilestoneVO resOrderMilestone);

	void saveOrderMileStone(ResOrderMilestone orderMilestone);
}
