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
package com.ai.apac.smartenv.device.mapper;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.vo.DeviceRelVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface DeviceRelMapper extends BaseMapper<DeviceRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param deviceRel
	 * @return
	 */
	List<DeviceRelVO> selectDeviceRelPage(IPage page, DeviceRelVO deviceRel);

	/**
	 * 
	 * @Function: DeviceRelMapper::listForBinding
	 * @Description: 查询绑定设备信息
	 * @param deviceInfo
	 * @param i
	 * @param size
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月11日 下午2:35:04 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<DeviceRel> listForBinding(DeviceInfo deviceInfo, int i, Integer size, Long vehicleId);


	List<DeviceRel> listForEntityAndTime(@Param(value = "entityId") Long entityId,@Param(value = "entityType")Long entityType, @Param(value = "entityCategoryId") Long entityCategoryId, @Param(value = "startTime")Timestamp startTime,@Param(value = "endTime")Timestamp endTime);

    List<DeviceRel> getModifyList(@Param(value = "entityId") Long entityId,@Param(value = "entityType")Long entityType, @Param(value = "entityCategoryId") Long entityCategoryId, @Param(value = "startTime")Timestamp startTime);

	List<DeviceRel> getEntityRelsByCategory(@Param(value = "tenantId") String tenantId,@Param(value = "categoryId") String categoryId,@Param(value = "deviceStatus") String deviceStatus);

}
