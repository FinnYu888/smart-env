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
package com.ai.apac.smartenv.vehicle.service;

import com.ai.apac.smartenv.vehicle.entity.VehicleExt;
import com.ai.apac.smartenv.vehicle.vo.VehicleExtVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;

import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 车辆信息扩展表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IVehicleExtService extends BaseService<VehicleExt> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param vehicleExt
	 * @return
	 */
	IPage<VehicleExtVO> selectVehicleExtPage(IPage<VehicleExtVO> page, VehicleExtVO vehicleExt);

	/**
	 * 
	 * @Function: IVehicleExtService::savePicture
	 * @Description: 保存图片
	 * @param vehicleId
	 * @param attrId
	 * @param attrName
	 * @param attrValue
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月7日 下午2:41:01 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean savePicture(Long vehicleId, Long attrId, String attrName, String attrValue);

	/**
	 * 
	 * @Function: IVehicleExtService::updatePicture
	 * @Description: 更新照片
	 * @param id
	 * @param vehiclePicName
	 * @param picAttrId
	 * @param picAttrName
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月11日 下午11:37:59 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void updatePicture(Long vehicleId, String attrValue, Long attrId, String attrName);

	VehicleExt getVehicleAttr(Long vehicleId,  Long attrId);

	void removeVehicleAttr(Long vehicleId);

	VehicleInfoVO getPictures(VehicleInfoVO vehicleInfoVO);

}
