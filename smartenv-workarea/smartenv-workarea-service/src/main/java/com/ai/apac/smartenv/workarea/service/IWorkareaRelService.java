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
package com.ai.apac.smartenv.workarea.service;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.vo.*;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.sql.Timestamp;
import java.util.List;

/**
 * 工作区域关联表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IWorkareaRelService extends BaseService<WorkareaRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param workareaRel
	 * @return
	 */
	IPage<WorkareaRelVO> selectWorkareaRelPage(IPage<WorkareaRelVO> page, WorkareaRelVO workareaRel);

	List<WorkareaRel> selectWorkareaRelHList(WorkareaRel workareaRel);

	List<WorkareaRel> queryWorkareaRelHList(WorkareaRel workareaRel, Timestamp startTime, Timestamp endTime);

	List<Person> userInfoByAreaIdAndDeptId(String deptId, String workareaId, String entityType) throws Exception;

	List<PersonNode> userInfoByAreaId(String workareaId, String entityType, String tenentId, String nodeName) throws Exception;


	List<BoundPersonVO> boundUser(String workareaId, String entityType) throws Exception;

	List<UserVO> eventPerson(String workareaId, String entityType) throws Exception;

	List<VehicleVO> vehicleInfoByAreaIdAndDeptId(String deptId, String workareaId, String entityType) throws Exception;

	List<VehicleNode> vehicleInfoByAreaId(String workareaId, String entityType, String tenentId, String nodeName) throws Exception;

	List<BoundVehicleVO> boundVehicle(String workareaId, String entityType) throws Exception;
}
