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
package com.ai.apac.smartenv.system.feign;

import org.springblade.core.datascope.model.DataScopeModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * IDataScopeClientFallback
 *
 * @author Chill
 */
@Component
public class IDataScopeClientFallback implements IDataScopeClient {
	@Override
	public DataScopeModel getDataScopeByMapper(String mapperId, String roleId) {
		return null;
	}

	@Override
	public DataScopeModel getDataScopeByCode(String code) {
		return null;
	}

	@Override
	public List<Long> getDeptAncestors(Long deptId) {
		return null;
	}
}