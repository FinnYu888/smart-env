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
package com.ai.apac.smartenv.websocket.wrapper;


import cn.hutool.core.util.NumberUtil;
import com.ai.apac.smartenv.common.enums.DeviceStatusEnum;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.utils.BeanUtil;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author qianlong
 * @since 2020-02-18
 */
public class PersonDetailWrapper {

	public static PersonDetailWrapper build() {
		return new PersonDetailWrapper();
 	}

	public PersonDetailVO entityVO(PersonDetailVO personDetailVO) {
		PersonDetailVO personDetail = BeanUtil.copy(personDetailVO, PersonDetailVO.class);
		personDetail.setDeviceStatusName(DeviceStatusEnum.getDescByValue(personDetailVO.getDeviceStatus()));
		String totalDistance = personDetail.getTotalDistance();
		if(StringUtils.isNotBlank(totalDistance)){
			String format = NumberUtil.decimalFormat("#.00", Double.valueOf(totalDistance));
			personDetail.setTotalDistance(format);
		}
		return personDetail;
	}
}
