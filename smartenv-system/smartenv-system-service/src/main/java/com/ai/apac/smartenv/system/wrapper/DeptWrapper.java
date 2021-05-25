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
package com.ai.apac.smartenv.system.wrapper;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.vo.DeptVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Chill
 */
public class DeptWrapper extends BaseEntityWrapper<Dept, DeptVO> {

	@Autowired
	private static IPersonClient personClient = null;

	private IPersonClient getPersonClient(){
		if(personClient == null){
			personClient = SpringUtil.getBean(IPersonClient.class);
		}
		return personClient;
	}

	public static DeptWrapper build() {
		return new DeptWrapper();
	}

	@Override
	public DeptVO entityVO(Dept dept) {
		DeptVO deptVO = Objects.requireNonNull(BeanUtil.copy(dept, DeptVO.class));
		if (Func.equals(dept.getParentId(), BladeConstant.TOP_PARENT_ID)) {
			deptVO.setParentName(BladeConstant.TOP_PARENT_NAME);
		} else {
			Dept parent = DeptCache.getDept(dept.getParentId());
			deptVO.setParentName(parent.getDeptName());
		}
		String category = DictCache.getValue("org_category", dept.getDeptCategory());
		deptVO.setDeptCategoryName(category);
		return deptVO;
	}


	public List<INode> listNodeVO(List<Dept> list) {
		if(list == null || list.isEmpty()){
			return new ArrayList<INode>();
		}
		String tenantId = list.get(0).getTenantId();

		//根据租户查询部门员工数量
		R<List<DeptStaffCountDTO>> result = getPersonClient().getDeptPersonCountByTenant(tenantId);
		HashMap<Long,Integer> deptPersonCountMap = new HashMap<Long,Integer>();
		if(result.isSuccess() && result.getData() != null){
			result.getData().stream().forEach(deptStaffCountDTO -> {
				deptPersonCountMap.put(deptStaffCountDTO.getDeptId(),deptStaffCountDTO.getCount());
			});
		}
		HashMap<Long,Dept> deptHashMap = new HashMap<>();

		list.forEach(dept -> {
			deptHashMap.put(dept.getId(),dept);
		});

		for (Dept dept : list) {
			putParentDept(dept, deptHashMap);
		}
		list.clear();
		list.addAll(new ArrayList<Dept>(deptHashMap.values()));
		
		list.forEach(dept -> {

			Integer deptPersonCount = deptPersonCountMap.get(dept.getId());
			if (deptPersonCount==null){
				deptPersonCount=0;
			}
			Long cuurentParentId = dept.getParentId();
			do {
				if (cuurentParentId==null){
					break;
				}

				Integer integer = deptPersonCountMap.get(cuurentParentId);
				if (integer!=null){
					integer=integer+deptPersonCount;
					deptPersonCountMap.put(cuurentParentId,integer);
				}else {
					deptPersonCountMap.put(cuurentParentId,deptPersonCount);

				}
				if (deptHashMap.get(cuurentParentId)!=null){

					cuurentParentId=deptHashMap.get(cuurentParentId).getParentId();
				}


			}while (!cuurentParentId.equals(0L));

		});

		List<INode> collect = list.stream().map(dept -> {
			DeptVO deptVO = BeanUtil.copy(dept, DeptVO.class);
			String category = DictCache.getValue("org_category", dept.getDeptCategory());
			Objects.requireNonNull(deptVO).setDeptCategoryName(category);
			Integer deptPersonCount = deptPersonCountMap.get(dept.getId());
			if(deptPersonCount == null){
				deptPersonCount = 0;
			}
			deptVO.setDeptPersonCount(deptPersonCount);
			return deptVO;
		}).collect(Collectors.toList());
		return ForestNodeMerger.merge(collect);
	}

	private void putParentDept(Dept dept, HashMap<Long, Dept> deptHashMap) {
		Long parentId = dept.getParentId();
		if (!parentId.equals(0L) && deptHashMap.get(parentId) == null) {
			Dept parentDept = DeptCache.getDept(parentId);
			deptHashMap.put(parentDept.getId(), parentDept);
			putParentDept(parentDept, deptHashMap);
		}
	}

	public List<INode> listNodeLazyVO(List<DeptVO> list) {
		List<INode> collect = list.stream().peek(dept -> {
			String category = DictCache.getValue("org_category", dept.getDeptCategory());
			Objects.requireNonNull(dept).setDeptCategoryName(category);
		}).collect(Collectors.toList());
		return ForestNodeMerger.merge(collect);
	}

}
