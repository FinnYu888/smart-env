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
package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IDeptService extends IService<Dept> {

	/**
	 * 懒加载部门列表
	 *
	 * @param tenantId
	 * @param parentId
	 * @param param
	 * @return
	 */
	List<DeptVO> lazyList(String tenantId, Long parentId, Map<String, Object> param);

	/**
	 * 树形结构
	 *
	 * @param tenantId
	 * @return
	 */
	List<DeptVO> tree(String tenantId);

	/**
	 * 懒加载树形结构
	 *
	 * @param tenantId
	 * @param parentId
	 * @return
	 */
	List<DeptVO> lazyTree(String tenantId, Long parentId);

	/**
	 * 获取部门名
	 *
	 * @param deptIds
	 * @return
	 */
	List<String> getDeptNames(String deptIds);

	/**
	 * 获取部门全名
	 *
	 * @param deptIds
	 * @return
	 */
	List<String> getDeptFullNames(String deptIds);

	/**
	 * 获取子部门
	 *
	 * @param deptId
	 * @return
	 */
	List<Dept> getDeptChild(Long deptId);

	/**
	 * 删除部门
	 *
	 * @param ids
	 * @return
	 */
	boolean removeDept(String ids);

	/**
	 * 提交
	 *
	 * @param dept
	 * @return
	 */
	boolean submit(Dept dept);

	/**
	 * 根据条件查询部门
	 * @param dept
	 * @return
	 */
	List<Dept> selectDept(Dept dept);

	/**
	 * 根据主键查询部门,忽略逻辑删除状态
	 * @param deptId
	 * @return
	 */
	Dept getDeptById(Long deptId);

	/**
	 * 获取所有子部门id
	 * @param deptId
	 * @return
	 */
	List<Long> getAllChildDepts(Long deptId, List<Long> deptIdList);
}
