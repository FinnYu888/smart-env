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
import com.ai.apac.smartenv.device.entity.DevicePersonInfo;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.DeviceViewVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.device.vo.VehicleDeviceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * 记录设备信息 服务类
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface IDeviceInfoService extends BaseService<DeviceInfo> {


	boolean saveOrUpdateDeviceInfo(DeviceInfo entity);

	boolean updateVehicleDeviceInfo(VehicleDeviceVO vehicleDeviceVO) throws IOException;

	boolean saveVehicleDeviceInfo(VehicleDeviceVO vehicleDeviceVO) throws IOException;


	boolean updatePersonDeviceInfo(PersonDeviceVO personDeviceVO) throws IOException;

	boolean savePersonDeviceInfo(PersonDeviceVO personDeviceVO) throws IOException;

	boolean batchRemove(List<Long> idList);

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param deviceInfo
	 * @return
	 */


	IPage<DeviceInfoVO> selectDeviceInfoPage(IPage<DeviceInfoVO> page, DeviceInfoVO deviceInfo);

	IPage<DeviceInfo> pageDevices(DeviceInfo deviceInfo, Query query, String tag,String simCode);

	IPage<VehicleDeviceVO> pageDevices4Query(DeviceInfo deviceInfo, Query query, String tag, String simCode);

	/**
	 * 设施关联终端信息
	 *
	 * @param entityCategoryId
	 * @param facilityId
	 * @return
	 */
	List<DeviceViewVO> listFacilityDevice(@NotNull Long entityCategoryId, @NotNull Long facilityId);

	List<DeviceInfo> listDevicesByParam(List<String> ids, Long categoryId);

	List<DeviceInfo> listDevicesByParam(DeviceInfo deviceInfo);

	/**
	 * 终端绑定接口
	 *
	 * @param entityType
	 * @param entityId
	 * @param deviceIds
	 * @return
	 */
	boolean bindDevice(String entityType, Long entityId, @NotEmpty String deviceIds);

	boolean deleteBindDevice(String entityType, Long entityId, String deviceIds);

	Boolean updateAllDeviceRel();

	String syncDeviceRel(String entityType, Long entityId, String deviceId, String optFlag);

	String syncDeviceCode(String oldCode,String newCode,String type);
	/**
	 * @param entityId
	 * @param entityType
	 * @return
	 * @Function: IDeviceInfoService::unbindDevice
	 * @Description: 根据实体id和类型解绑
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 上午10:25:16
	 * <p>
	 * Modification History:
	 * Date         Author          Version            Description
	 * -------------------------------------------------------------
	 */
	Boolean unbindDevice(Long entityId, Long entityType);

	/**
	 * 根据条件查询设备
	 * @param deviceInfo
	 * @return
	 */
	List<DeviceInfo> selectDeviceList(DeviceInfo deviceInfo);

	List<DeviceInfo> listBindedDevice(Long entityId,String entityType,String tenantId);


	DeviceInfo getDeviceInfoByCode(String code);

    List<DevicePersonInfo> getByEntityAndCategoryList(List<Long> scheduleVehicleIdList, Long personWatchDevice);

    List<DevicePersonInfo> listDeviceByCategoryId(Long entityCategoryId);

	boolean updateDeviceStatus(String code,Long status);
}
