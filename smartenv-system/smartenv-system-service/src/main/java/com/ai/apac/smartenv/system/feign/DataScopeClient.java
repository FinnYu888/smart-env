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

import lombok.RequiredArgsConstructor;
import org.springblade.core.datascope.constant.DataScopeConstant;
import org.springblade.core.datascope.model.DataScopeModel;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据权限Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class DataScopeClient implements IDataScopeClient {

	private final JdbcTemplate jdbcTemplate;

	/**
	 * 获取数据权限
	 *
	 * @param mapperId 数据权限mapperId
	 * @param roleId   用户角色集合
	 * @return DataScopeModel
	 */
	@Override
	@GetMapping(GET_DATA_SCOPE_BY_MAPPER)
	public DataScopeModel getDataScopeByMapper(String mapperId, String roleId) {
		List<Object> args = new ArrayList<>(Collections.singletonList(mapperId));
		List<Long> roleIds = Func.toLongList(roleId);
		args.addAll(roleIds);
		DataScopeModel dataScope = null;
		List<DataScopeModel> list = jdbcTemplate.query(DataScopeConstant.dataByMapper(roleIds.size()), args.toArray(), new BeanPropertyRowMapper<>(DataScopeModel.class));
		if (CollectionUtil.isNotEmpty(list)) {
			dataScope = list.iterator().next();
		}
		return dataScope;
	}

	/**
	 * 获取数据权限
	 *
	 * @param code 数据权限资源编号
	 * @return DataScopeModel
	 */
	@Override
	@GetMapping(GET_DATA_SCOPE_BY_CODE)
	public DataScopeModel getDataScopeByCode(String code) {
		DataScopeModel dataScope = null;
		List<DataScopeModel> list = jdbcTemplate.query(DataScopeConstant.DATA_BY_CODE, new Object[]{code}, new BeanPropertyRowMapper<>(DataScopeModel.class));
		if (CollectionUtil.isNotEmpty(list)) {
			dataScope = list.iterator().next();
		}
		return dataScope;
	}

	/**
	 * 获取部门子级
	 *
	 * @param deptId 部门id
	 * @return deptIds
	 */
	@Override
	@GetMapping(GET_DEPT_ANCESTORS)
	public List<Long> getDeptAncestors(Long deptId) {
		return jdbcTemplate.queryForList(DataScopeConstant.DATA_BY_DEPT, new Object[]{deptId}, Long.class);
	}
}
