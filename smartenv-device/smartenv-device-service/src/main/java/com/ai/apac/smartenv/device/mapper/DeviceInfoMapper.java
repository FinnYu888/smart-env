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
import com.ai.apac.smartenv.device.entity.DevicePersonInfo;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.VehicleDeviceVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 记录设备信息 Mapper 接口
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param deviceInfo
	 * @return
	 */
	List<DeviceInfoVO> selectDeviceInfoPage(IPage page, DeviceInfoVO deviceInfo);

	/**
	 * 自定义列表查询
	 *
	 * @param deviceInfo
	 * @return
	 */
	List<DeviceInfo> selectDeviceInfoList(DeviceInfo deviceInfo);

	List<DeviceInfo>  listBindedDevice(@Param(value = "entityId") Long entityId, @Param(value = "entityType") String entityType, @Param(value = "tenantId") String tenantId);

	List<DevicePersonInfo> listDeviceEntity(@Param(value = "entityIdList") List<Long> entityIdList, @Param(value = "entityType") Long entityType);


	@Select({"SELECT info.* ,rel.entity_id FROM ai_device_rel rel LEFT JOIN ai_device_info info ON rel.device_id = info.id where info.entity_category_id = #{entityCategoryId} and rel.is_deleted = 0"})
	List<DevicePersonInfo> listDeviceByCategoryId(@Param("entityCategoryId") Long entityCategoryId);

	List<VehicleDeviceVO> selectDeviceInfoVOPage(IPage page, @Param("ew") QueryWrapper queryWrapper);
	int countDeviceInfoVOPage(@Param("ew") QueryWrapper queryWrapper);
}
