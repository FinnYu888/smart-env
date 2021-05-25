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
package com.ai.apac.smartenv.device.service;

import com.ai.apac.smartenv.device.dto.SimInfoDTO;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.vo.SimInfoVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 * SIM卡信息 服务类
 *
 * @author Blade
 * @since 2020-05-08
 */
public interface ISimInfoService extends BaseService<SimInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param simInfo
	 * @return
	 */
	IPage<SimInfo> selectSimInfoPage(Query query, SimInfo simInfo);


	Boolean saveOrUpdateSimInfo(SimInfoDTO simInfoDTO);

	Boolean removeSimInfo(String ids);

	/**
	 * 根据设备主键查询绑定的SIM卡信息
	 * @param deviceId
	 * @return
	 */
	SimInfo getSimByDeviceId(Long deviceId);

	/**
	 * 根据设备CODE查询绑定的SIM卡信息
	 * @param deviceCode
	 * @return
	 */
	SimInfo getSimByDeviceCode(String deviceCode);

	List<SimInfo> listUnBindSim(SimInfo simInfo);

    SimRel getSimInfoBySimCode2(String simCode2);
}
