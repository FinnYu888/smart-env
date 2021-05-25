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

import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.secure.BladeUser;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-02-25
 */
public interface IResOrderService extends BaseService<ResOrder> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resOrder
	 * @return
	 */
	IPage<ResOrderVO> selectResOrderPage(IPage<ResOrderVO> page, ResOrderVO resOrder);

	/**
	*物资申请
	*/
	ResOrder resApplySubmitOrder(ResInfoApplyVO infoApplyVO);

	/**
	* 物资申请审批前置任务
	*/
	void applyBefore(Long orderId,String taskId);

	/**
	*物资申请订单列表
	*/
	public IPage<ResApplyQueryResponseVO> listApplyOrderPage(IPage<Object> page, QueryWrapper queryWrapper);

	/**
	*更新订单状态
	*/
    Boolean updateOrderStatus(Long orderId, String processId,Integer status);

    /**
    * 订单取消
    */
    Boolean cancelResOrder(Long orderId);

	List<ResOrder4HomeVO> lastResOrders(String tenantId,String userId);

	Boolean canInvApprove();

	List<ResOrder4MiniVO> lastResOrder4Mini(Integer num);

	Integer countResOrder4Mini();

    /**
    * 获取订单详情接口
    */
	ResApplyDetailVO resApplyOrderDetail(Long orderId);

}
