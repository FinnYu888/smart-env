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
package com.ai.apac.smartenv.vehicle.mapper;

import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrderMilestone;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-08-06
 */
public interface VehicleMaintOrderMapper extends BaseMapper<VehicleMaintOrder> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param vehicleMaintOrder
	 * @return
	 */
	List<VehicleMaintOrderVO> selectVehicleMaintOrderPage(IPage page, VehicleMaintOrderVO vehicleMaintOrder);

	/**
	* 查询订单详情和审批过程详情
	* @author 66578
	*/
	List<VehicleMaintOrderMilestone> selectVehicleMaintOrderMilestone(Map paramMap);

	List<VehicleMaintOrderMilestone> queryVehicleMaintOrder(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
