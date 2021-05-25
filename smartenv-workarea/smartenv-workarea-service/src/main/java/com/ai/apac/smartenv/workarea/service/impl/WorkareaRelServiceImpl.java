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
package com.ai.apac.smartenv.workarea.service.impl;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.vo.*;
import com.ai.apac.smartenv.workarea.mapper.WorkareaRelMapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaRelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作区域关联表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {
		ServiceException.class, Exception.class })
public class WorkareaRelServiceImpl extends BaseServiceImpl<WorkareaRelMapper, WorkareaRel>
		implements IWorkareaRelService {

	private IPersonClient personClient;
	private IVehicleClient vehicleClient;
	private IPersonVehicleRelClient personVehicleRelClient;
	private ISysClient sysClient;
	private IEntityCategoryClient entityCategoryClient;

	@Override
	public IPage<WorkareaRelVO> selectWorkareaRelPage(IPage<WorkareaRelVO> page, WorkareaRelVO workareaRel) {
		return page.setRecords(baseMapper.selectWorkareaRelPage(page, workareaRel));
	}

	@Override
	public List<WorkareaRel> selectWorkareaRelHList(WorkareaRel workareaRel) {
		return baseMapper.selectWorkareaRelHList(workareaRel.getEntityId(), workareaRel.getEntityType(),
				workareaRel.getIsDeleted(), workareaRel.getTenantId());
	}

	@Override
	public List<WorkareaRel> queryWorkareaRelHList(WorkareaRel workareaRel, Timestamp startTime, Timestamp endTime) {
		return baseMapper.queryWorkareaRelHList(workareaRel.getEntityId(), workareaRel.getEntityType(),
				workareaRel.getIsDeleted(), workareaRel.getTenantId(), startTime,endTime);
	}

	@Override
	public List<Person> userInfoByAreaIdAndDeptId(String deptId, String workareaId, String entityType)
			throws Exception {
		R<List<Person>> rUsers = personClient.getPersonByDeptId(Func.toLong(deptId));
		List<Person> users = rUsers.getData();
		if (workareaId != null) {
			List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>()
					.eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
			if (workareaRels != null && workareaRels.size() > 0) {
				for (WorkareaRel workareaRel : workareaRels) {
					Long entityId = workareaRel.getEntityId();
					if (users != null && users.size() > 0) {
						for (Person user : users) {
							if (user.getId().equals(entityId)) { // 已绑定该人员
								user.setStatus(10); // 10 与前端约定标识，表示已绑定当前查询的区域或路线
							}
						}
					}
				}
			}
		}
		List<Person> personList = new ArrayList<>();
		if (users != null && users.size() > 0) {
			for (Person user : users) {
				if (user.getIsIncumbency() == PersonConstant.IncumbencyStatus.IN
						|| user.getIsIncumbency() == PersonConstant.IncumbencyStatus.TEMPORARY) {
					personList.add(user);
				}
			}
		}
		return personList;
	}

	@Override
	public List<PersonNode> userInfoByAreaId(String workareaId, String entityType, String tenentId, String nodeName)
			throws Exception {
		String entityIdStr = "";
		if (workareaId != null) {
			List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>().eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
			if (workareaRels != null && !workareaRels.isEmpty()) {
				for (WorkareaRel workareaRel : workareaRels) {
					entityIdStr += String.valueOf(workareaRel.getEntityId()) + ",";
				}
			}
			if (!"".equals(entityIdStr)) {
				entityIdStr = entityIdStr.substring(0, entityIdStr.length() - 1);
			}
		}
		List<PersonNode> nodeList = personClient.treeByDept(nodeName == null ? "" : nodeName, tenentId, entityIdStr).getData();
		return nodeList;
	}

	@Override
	public List<VehicleVO> vehicleInfoByAreaIdAndDeptId(String deptId, String workareaId, String entityType)
			throws Exception {
		R<List<VehicleInfo>> vehicleInfoList = vehicleClient.vehicleInfoByDeptId(Func.toLong(deptId));
		List<VehicleInfo> vehicleInfos = vehicleInfoList.getData();

		if (workareaId != null) {
			List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>().eq("workarea_id",Func.toLong(workareaId)).eq("entity_type",Func.toLong(entityType)));
			if (workareaRels != null && workareaRels.size() > 0) {
				for (WorkareaRel workareaRel : workareaRels) {
					Long entityId = workareaRel.getEntityId();
					if (vehicleInfos != null && vehicleInfos.size() > 0) {
						for (VehicleInfo vehicleInfo : vehicleInfos) {
							if(vehicleInfo.getId().equals(entityId)) { // 已绑定该车辆
								vehicleInfo.setStatus(10); // 10 与前端约定标识，表示已绑定当前查询的区域或路线
							}
						}
					}

				}
			}
		}
		List<VehicleVO> vehicleVOS = new ArrayList<>();
		if (vehicleInfos != null && vehicleInfos.size() > 0) {

			for (VehicleInfo vehicleInfo : vehicleInfos) {
				VehicleVO vehicleVO = new VehicleVO();
				vehicleVO.setId(vehicleInfo.getId());
				vehicleVO.setPlateNumber(vehicleInfo.getPlateNumber());
//				vehicleVO.setDeptName();
				vehicleVO.setDeptId(vehicleInfo.getDeptId());
				vehicleVO.setCategoryName(entityCategoryClient.getCategoryName(vehicleInfo.getEntityCategoryId()).getData());
				vehicleVO.setStatus(vehicleInfo.getStatus());
				/*if(vehicleInfo.getDeptRemoveTime() != null) { // 已经退役的车辆不能选择,剔除掉;
				    if(vehicleInfo.getDeptRemoveTime().compareTo(TimeUtil.getSysDate()) > 0){
						vehicleVOS.add(vehicleVO);
				    }
				}else { // 没有设置退出时间默认展示
					vehicleVOS.add(vehicleVO);
				}*/
				if (vehicleInfo.getIsUsed() == null || vehicleInfo.getIsUsed() == VehicleConstant.VehicleState.IN_USED) {
					vehicleVOS.add(vehicleVO);
				}
			}
		}

		return vehicleVOS;
	}
	
	@Override
	public List<VehicleNode> vehicleInfoByAreaId(String workareaId, String entityType, String tenentId, String nodeName)
			throws Exception {
		String entityIdStr = "";
		if (workareaId != null) {
			List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>().eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
			if (workareaRels != null && !workareaRels.isEmpty()) {
				for (WorkareaRel workareaRel : workareaRels) {
					entityIdStr += String.valueOf(workareaRel.getEntityId()) + ",";
				}
			}
			if (!"".equals(entityIdStr)) {
				entityIdStr = entityIdStr.substring(0, entityIdStr.length() - 1);
			}
		}
		List<VehicleNode> nodeList = vehicleClient.treeByDept(nodeName == null ? "" : nodeName, tenentId, entityIdStr).getData();
		return nodeList;
	}

	@Override
	public List<UserVO> eventPerson(String workareaId, String entityType) throws Exception {
		List<UserVO> userList = new ArrayList<>();
		List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>()
				.eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
		if (workareaRels != null && workareaRels.size() > 0) {
			for (WorkareaRel workareaRel : workareaRels) {
				if ("2".equals(entityType)) { // 车辆查驾驶员
					List<Person> users = personVehicleRelClient.listDriverByVehicleId(workareaRel.getEntityId())
							.getData();
					if (users != null && users.size() > 0) {
						for (Person user : users) {
							UserVO userVO = new UserVO();
							userVO.setRelId(workareaRel.getId());
							userVO.setDeptId(user.getPersonDeptId());
							userVO.setName(user.getPersonName());
							userVO.setId(user.getId());
							userVO.setJobNumber(user.getJobNumber());
							userList.add(userVO);
						}
					}
				} else {
					Person user = PersonCache.getPersonById(null, workareaRel.getEntityId());
					if (user != null && user.getId() != null) {
						UserVO userVO = new UserVO();
						userVO.setRelId(workareaRel.getId());
						userVO.setDeptId(user.getPersonDeptId());
						userVO.setName(user.getPersonName());
						userVO.setId(user.getId());
						userVO.setJobNumber(user.getJobNumber());
						userList.add(userVO);
					}
				}

			}

		}
		return userList;
	}

	@Override
	public List<BoundPersonVO> boundUser(String workareaId, String entityType) throws Exception {
		List<UserVO> userList = new ArrayList<>();
		List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>()
				.eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
		if (workareaRels != null && workareaRels.size() > 0) {
			for (WorkareaRel workareaRel : workareaRels) {
//				R<Person> userR = personClient.getPerson(workareaRel.getEntityId());
				Person user = PersonCache.getPersonById(null, workareaRel.getEntityId());
//				if (userR.getData().getId() != null) {
				if (user != null && user.getId() != null) {
//					Person user = userR.getData();
					UserVO userVO = new UserVO();
					userVO.setRelId(workareaRel.getId());
					userVO.setDeptId(user.getPersonDeptId());
					userVO.setName(user.getPersonName());
					userVO.setId(user.getId());

					userList.add(userVO);
				}
			}

		}
		List<BoundPersonVO> boundPersonVOList = new ArrayList<>();
		userGroupByDept(userList, boundPersonVOList);
		return boundPersonVOList;
	}

	@Override
	public List<BoundVehicleVO> boundVehicle(String workareaId, String entityType) throws Exception {
		List<VehicleVO> vehicleList = new ArrayList<>();
		List<WorkareaRel> workareaRels = this.list(new QueryWrapper<WorkareaRel>()
				.eq("workarea_id", Func.toLong(workareaId)).eq("entity_type", Func.toLong(entityType)));
		if (workareaRels != null && workareaRels.size() > 0) {

			for (WorkareaRel workareaRel : workareaRels) {
//				R<VehicleInfo> vehicleInfoR = vehicleClient.vehicleInfoById(workareaRel.getEntityId());
				VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, workareaRel.getEntityId());
				if (vehicleInfo != null && vehicleInfo.getId() != null) {
//					VehicleInfo vehicleInfo = vehicleInfoR.getData();

					VehicleVO vehicleVO = new VehicleVO();
					vehicleVO.setRelId(workareaRel.getId());
					vehicleVO.setDeptId(vehicleInfo.getDeptId());
//					vehicleVO.setDeptName(deptName);
					vehicleVO.setPlateNumber(vehicleInfo.getPlateNumber());
					vehicleVO.setCategoryName(
							entityCategoryClient.getCategoryName(vehicleInfo.getEntityCategoryId()).getData());
					vehicleVO.setId(vehicleInfo.getId());
					vehicleList.add(vehicleVO);
				}

			}
		}
		List<BoundVehicleVO> boundVehicleVOList = new ArrayList<>();
		vehicleGroupByDept(vehicleList, boundVehicleVOList);
		return boundVehicleVOList;
	}

	private List<BoundVehicleVO> vehicleGroupByDept(List<VehicleVO> vehicleList,
			List<BoundVehicleVO> boundVehicleVOList) {

		if (vehicleList.size() != 0) {
			// 获取到第一个单号
			Long deptId = vehicleList.get(0).getDeptId();
			String deptName = sysClient.getDeptName(deptId).getData();
			BoundVehicleVO boundVehicleVO = new BoundVehicleVO();

			List<VehicleVO> list = new ArrayList<>();

			List<VehicleVO> lf = new ArrayList<>();
			for (VehicleVO vehicleInfo : vehicleList) {
				lf.add(vehicleInfo);
			}

			for (int j = 0; j < lf.size(); j++) {
				if (deptId.equals(lf.get(j).getDeptId())) {
					lf.get(j).setDeptName(deptName);
					list.add(lf.get(j));
					vehicleList.remove(lf.get(j));

				}
			}

			boundVehicleVO.setDeptName(deptName);
			boundVehicleVO.setList(list);
			boundVehicleVOList.add(boundVehicleVO);
			return vehicleGroupByDept(vehicleList, boundVehicleVOList);
		}
		return boundVehicleVOList;
	}

	private List<BoundPersonVO> userGroupByDept(List<UserVO> userList, List<BoundPersonVO> boundPersonVOList) {

		if (userList.size() != 0) {
			//
			Long deptId = userList.get(0).getDeptId();
			String deptName = sysClient.getDeptName(Long.valueOf(deptId)).getData();
			BoundPersonVO boundPersonVO = new BoundPersonVO();

			List<UserVO> list = new ArrayList<>();

			List<UserVO> lf = new ArrayList<>();
			for (UserVO user : userList) {
				lf.add(user);
			}

			for (int j = 0; j < lf.size(); j++) {
				if (deptId.equals(lf.get(j).getDeptId())) {
					lf.get(j).setDeptName(deptName);
					list.add(lf.get(j));
					userList.remove(lf.get(j));

				}
			}

			boundPersonVO.setDeptName(deptName);
			boundPersonVO.setDeptId(Long.valueOf(deptId));
			boundPersonVO.setList(list);
			boundPersonVOList.add(boundPersonVO);
			return userGroupByDept(userList, boundPersonVOList);
		}
		return boundPersonVOList;
	}
}
