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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.springblade.core.secure.BladeUser;

import com.ai.apac.smartenv.person.vo.PersonImportResultVO;

public interface IPersonAsyncService {

	Future<PersonImportResultVO> importPersonInfo(Object object, BladeUser user, HashMap<String, Long> stationMap) throws InterruptedException;

	Boolean thirdPersonInfoAsync(List<List<String>> datasList, String tenantId,String actionType,Boolean isAsyn);

	/**
	 * 将人员设备数据同步刷新到Mongo中
	 * @param projectCode
	 */
	void syncPersonDeviceStatusToMongo(String projectCode);
}
