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

import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 车辆基本信息表 Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface VehicleInfoMapper extends BaseMapper<VehicleInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param vehicleInfo
	 * @return
	 */
	List<VehicleInfoVO> selectVehicleInfoPage(IPage page, VehicleInfoVO vehicleInfo);

	/**
	 * 
	 * @Function: VehicleInfoMapper::updateVehicleInfoById
	 * @Description: 更新，可置空
	 * @param vehicleInfo
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月20日 下午2:30:51 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Integer updateVehicleInfoById(@Param("record")VehicleInfoVO vehicleInfo);


	List<VehicleInfoVO> selectVehicleInfoVOPage(IPage page, @Param("ew") QueryWrapper queryWrapper);

	int countVehicleInfoVOPage( @Param("ew") QueryWrapper queryWrapper);

}
