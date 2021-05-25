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
package com.ai.apac.smartenv.vehicle.service.impl;

import com.ai.apac.smartenv.vehicle.entity.VehicleWorkType;
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import com.ai.apac.smartenv.vehicle.mapper.VehicleWorkTypeMapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleWorkTypeService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 车辆作业类型表 服务实现类
 *
 * @author Blade
 * @since 2021-01-12
 */
@Service
public class VehicleWorkTypeServiceImpl extends BaseServiceImpl<VehicleWorkTypeMapper, VehicleWorkType> implements IVehicleWorkTypeService {

	@Override
	public IPage<VehicleWorkTypeVO> selectVehicleWorkTypePage(IPage<VehicleWorkTypeVO> page, VehicleWorkTypeVO vehicleWorkType) {
		return page.setRecords(baseMapper.selectVehicleWorkTypePage(page, vehicleWorkType));
	}

}
