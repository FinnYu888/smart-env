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
package com.ai.apac.smartenv.person.service;

import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.vo.PersonVehicleRelVO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.secure.BladeUser;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 人员与车辆关系表 服务类
 *
 * @author Blade
 * @since 2020-02-07
 */
public interface IPersonVehicleRelService extends BaseService<PersonVehicleRel> {


	Boolean batchRemove(List<Long> ids);
	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param personVehicleRel
	 * @return
	 */
	IPage<PersonVehicleRelVO> selectPersonVehicleRelPage(IPage<PersonVehicleRelVO> page, PersonVehicleRelVO personVehicleRel);


	/**
	 * 根据车辆id查询驾员
	 *
	 * @param vehicleId 车辆id
	 * @return
	 */
	List<PersonVehicleRel> getPersonByVehicle(Long vehicleId);

	Boolean unbindPerson(Long vehicleId);

	Boolean unbindVehicle(Long personId);

	List<PersonVehicleRel> getVehicleByPersonId(Long personId);

	/**
	 * 
	 * @Function: IPersonVehicleRelService::personBindVehicle
	 * @Description: 人员绑定车辆
	 * @param personVehicleRelVO
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @param bladeUser 
	 * @date: 2020年3月11日 下午1:39:47 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void personBindVehicle(PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser);

	/**
	 * 
	 * @Function: IPersonVehicleRelService::vehicleBindPerson
	 * @Description: 车辆绑定人员
	 * @param personVehicleRelVO
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @param bladeUser 
	 * @date: 2020年3月11日 下午1:43:54 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void vehicleBindPerson(PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser);

	List<Long> getRelVehiclesByuserId(Long userId);

}
