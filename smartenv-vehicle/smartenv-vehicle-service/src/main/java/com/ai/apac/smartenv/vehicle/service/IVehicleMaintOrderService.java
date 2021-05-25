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
package com.ai.apac.smartenv.vehicle.service;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintMilestone;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrderMilestone;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderApproveVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-08-06
 */
public interface IVehicleMaintOrderService extends BaseService<VehicleMaintOrder> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param vehicleMaintOrder
	 * @return
	 */
	IPage<VehicleMaintOrderVO> selectVehicleMaintOrderPage(IPage<VehicleMaintOrderVO> page, VehicleMaintOrderVO vehicleMaintOrder);

	/**
	* 保存申请记录
	* @author 66578
	*/
	void saveVehcileMaintOrder(VehicleMaintOrder vo, Person captainPerson,Person managePerson);

	/**
	* 第一审批人审批
	* @author 66578
	*/
	void vechileMainFirstApprove(VehicleMaintOrderApproveVO maintOrderApproveVO);

	/**
	* 查询订单详情和审批记录I
	* @author 66578
	*/
	VehicleMaintOrderMilestone queryVehicleMaintOrderMilestone(Long orderId);


	/**
	* 查询订单列表
	* @author 66578
	*/
	IPage<VehicleMaintOrder> queryVehicleMaintOrderPage(IPage<VehicleMaintOrder> page, QueryWrapper queryWrapper);

	/**
	* 车辆维保预算提交
	* @author 66578
	*/
	void vechileMainBudget(VehicleMaintOrder vehicleMaintOrder);

	/**
	* 查询改用户下审批的单子
	* @author 66578
	*/
	public IPage<VehicleMaintOrder> queryVehicleMaintOrderApprovePage(Query query, QueryWrapper queryWrapper) ;

    /**
    * 车辆经理提交审批
    * @author 66578
    */
    void vechileMainManageApprove(VehicleMaintOrderApproveVO maintOrderApproveVO);

    /**
    * 车辆维修完成确认节点
    * @author 66578
    */
	void vechileMainToFinish(VehicleMaintOrder vehicleMaintOrder);

	/**
	* 取消申请
	* @author 66578
	*/
    void cancelOrder(String id);

    /**
    * 查询车辆维修记录
    * @author 66578
    */
	IPage<VehicleMaintOrder> queryVehicleMaintOrderRecord(IPage<VehicleMaintOrder> page, QueryWrapper<VehicleMaintOrder> queryWrapper);


}
