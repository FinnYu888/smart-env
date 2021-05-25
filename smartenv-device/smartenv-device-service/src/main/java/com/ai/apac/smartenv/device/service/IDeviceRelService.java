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

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.vo.DeviceRelVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IDeviceRelService extends BaseService<DeviceRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param deviceRel
	 * @return
	 */
	IPage<DeviceRelVO> selectDeviceRelPage(IPage<DeviceRelVO> page, DeviceRelVO deviceRel);

	void saveOrUpdateDeviceRel(DeviceRel deviceRel);

	/**
	 * 根据实体查询绑定的终端
	 *
	 * @param entityId 实体id，entityType 实体类型
	 * @return
	 */
	List<DeviceRel> getDeviceRelsByEntity(Long entityId,Long entityType);

	List<DeviceRel> getDeviceRelByDeviceId(Long deviceId);


	/**
	 * 
	 * @Function: IDeviceRelService::listForBinding
	 * @Description: 查询绑定设备信息
	 * @param deviceInfo
	 * @param query
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月11日 下午2:31:56 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<DeviceRel> listForBinding(DeviceInfo deviceInfo, Query query, Long vehicleId);

	/**
	 * 根据实体Id或者类型查传感器
	 * @param entityIdList
	 * @param entityType
	 * @return
	 */
	List<DeviceRel> listDeviceRelsByEntity(List<Long> entityIdList, Long entityType, List<Long> entityCategoryIdList);

	List<Long>  getEntityRelsByCategory(String tenantId,String categoryId,String deviceStatus);

	/**
	* 获取设施绑定终端数量
	*/
	Map<Long, Long> getDeviceCount(List<Long> entityIdList, Long entityType);


	List<DeviceRel> listForEntityAndTime(Long entityId,Long entityType,   Long entityCategoryId,  Timestamp startTime, Timestamp endTime);

	List<DeviceRel> getModifyList(Long entityId,Long entityType, Long entityCategoryId,Timestamp startTime);



}
