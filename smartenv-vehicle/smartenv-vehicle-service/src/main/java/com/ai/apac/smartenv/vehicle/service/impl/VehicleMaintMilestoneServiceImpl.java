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


import com.ai.apac.smartenv.vehicle.entity.VehicleMaintMilestone;
import com.ai.apac.smartenv.vehicle.mapper.VahicleMaintMilestoneMapper;
import com.ai.apac.smartenv.vehicle.service.IVahicleMaintMilestoneService;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Service
public class VehicleMaintMilestoneServiceImpl extends BaseServiceImpl<VahicleMaintMilestoneMapper,VehicleMaintMilestone> implements IVahicleMaintMilestoneService {


    //public class VehicleMaintOrderServiceImpl extends BaseServiceImpl<VehicleMaintOrderMapper, VehicleMaintOrder> implements IVehicleMaintOrderService {


}
