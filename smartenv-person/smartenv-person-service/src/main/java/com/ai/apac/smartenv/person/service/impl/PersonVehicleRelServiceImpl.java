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
package com.ai.apac.smartenv.person.service.impl;

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.mapper.PersonVehicleRelMapper;
import com.ai.apac.smartenv.person.service.IPersonUserRelService;
import com.ai.apac.smartenv.person.service.IPersonVehicleRelService;
import com.ai.apac.smartenv.person.vo.PersonVehicleRelVO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;

import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 人员与车辆关系表 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Service
@AllArgsConstructor
public class PersonVehicleRelServiceImpl extends BaseServiceImpl<PersonVehicleRelMapper, PersonVehicleRel> implements IPersonVehicleRelService {


	private IWorkareaRelClient workareaRelClient;
	private IPersonUserRelService personUserRelService;

	@Override
	public Boolean batchRemove(List<Long> ids) {
		BladeUser user = AuthUtil.getUser();
		ids.forEach(id->{
			PersonVehicleRel personVehicleRel = baseMapper.selectById(id);
			baseMapper.deleteById(id);
			workareaRelClient.syncDriverWorkArea(personVehicleRel.getVehicleId(),personVehicleRel.getPersonId(),"1",user.getUserId(),user.getDeptId(),user.getTenantId());

		});
		return true;
	}

	@Override
	public IPage<PersonVehicleRelVO> selectPersonVehicleRelPage(IPage<PersonVehicleRelVO> page, PersonVehicleRelVO personVehicleRel) {
		return page.setRecords(baseMapper.selectPersonVehicleRelPage(page, personVehicleRel));
	}

	/**
	 * 根据车辆id查询驾员
	 *
	 * @param vehicleId 车辆id
	 * @return
	 */
	@Override
	public List<PersonVehicleRel> getPersonByVehicle(Long vehicleId) {
		QueryWrapper<PersonVehicleRel> queryWrapper = new QueryWrapper<PersonVehicleRel>();
		queryWrapper.eq("vehicle_id",vehicleId);
		queryWrapper.eq("is_deleted",0);
		queryWrapper.orderByDesc("create_time");
		List<PersonVehicleRel> relList = baseMapper.selectList(queryWrapper);
		return relList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public Boolean unbindPerson(Long vehicleId) {
		List<PersonVehicleRel> personRelList = getPersonByVehicle(vehicleId);
		if (personRelList != null && !personRelList.isEmpty()) {
			personRelList.forEach(personRel -> {
				deleteLogic(Arrays.asList(personRel.getId()));
				BladeUser user = AuthUtil.getUser();
				workareaRelClient.syncDriverWorkArea(personRel.getVehicleId(), personRel.getPersonId(), "1",user.getUserId(),user.getDeptId(),user.getTenantId());
			});
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public Boolean unbindVehicle(Long personId) {
		List<PersonVehicleRel> vehicleRelList = getVehicleByPersonId(personId);
		List<Long> ids = new ArrayList<>();
		if (vehicleRelList != null && !vehicleRelList.isEmpty()) {
			vehicleRelList.forEach(vehicleRel -> {
				ids.add(vehicleRel.getId());
			});
			deleteLogic(ids);
		}
		return true;
	}

	@Override
	public List<PersonVehicleRel> getVehicleByPersonId(Long personId) {
		QueryWrapper<PersonVehicleRel> queryWrapper = new QueryWrapper<PersonVehicleRel>();
		queryWrapper.eq("person_id", personId);
		queryWrapper.eq("is_deleted", 0);
		List<PersonVehicleRel> list = list(queryWrapper);
		return list;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void personBindVehicle(PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser) {
		Long personId = personVehicleRelVO.getPersonId();
		Person person = PersonCache.getPersonById(null, personId);
		if (person == null || person.getId() == null || person.getId() <= 0) {
			throw new ServiceException(personId + "，人员不存在");
		}
		List<Long> vehicleIdList = Func.toLongList(personVehicleRelVO.getVehicleIds());
		/*List<PersonVehicleRel> relList = getVehicleByPersonId(personId);
		if (relList != null && !relList.isEmpty()) {
			throw new ServiceException(person.getPersonName() + "已绑定车辆");
		}
		if (vehicleIdList != null && vehicleIdList.size() > 1) {
			throw new ServiceException("人员只能绑定一辆车");
		}*/
		vehicleIdList.forEach(vehicleId -> {
			VehicleInfo vehicle = VehicleCache.getVehicleById(null, vehicleId);
			if (vehicle == null || vehicle.getId() == null || vehicle.getId() <= 0) {
				throw new ServiceException(vehicleId + "，车辆不存在");
			}
			PersonVehicleRel personVehicleRel = new PersonVehicleRel();
			personVehicleRel.setPersonId(personId);
			personVehicleRel.setVehicleId(vehicleId);
			List<PersonVehicleRel> list = list(Condition.getQueryWrapper(personVehicleRel));
			if (list != null && list.size() > 0) {
				throw new ServiceException(person.getPersonName() + "," + vehicle.getPlateNumber() + ", 车辆和人员已绑定");
			}
			personVehicleRel.setPersonName(person.getPersonName());
			save(personVehicleRel);
			workareaRelClient.syncDriverWorkArea(personVehicleRel.getVehicleId(), personVehicleRel.getPersonId(), "2",
					bladeUser.getUserId(), bladeUser.getDeptId(), bladeUser.getTenantId());

		});
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void vehicleBindPerson(PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser) {
		Long vehicleId = personVehicleRelVO.getVehicleId();
		VehicleInfo vehicle = VehicleCache.getVehicleById(null, vehicleId);
		if (vehicle == null || vehicle.getId() == null || vehicle.getId() <= 0) {
			throw new ServiceException(vehicleId + "，车辆不存在");
		}
		List<Long> personIdList = Func.toLongList(personVehicleRelVO.getPersonIds());
		/*if (vehicle.getKindCode().equals(VehicleConstant.KindCode.NON_MOTOR)) {
			if (personIdList != null && personIdList.size() > 1) {
				throw new ServiceException("非机动车只能绑定一名员工");
			}
			List<PersonVehicleRel> relList = getPersonByVehicle(vehicleId);
			if (relList != null && !relList.isEmpty()) {
				throw new ServiceException("非机动车只能绑定一名员工");
			}
		}*/
		personIdList.forEach(personId -> {
			Person person = PersonCache.getPersonById(null, personId);
			if (person == null || person.getId() == null || person.getId() <= 0) {
				throw new ServiceException(personId + "，人员不存在");
			}
			PersonVehicleRel personVehicleRel = new PersonVehicleRel();
			personVehicleRel.setPersonId(personId);
			personVehicleRel.setVehicleId(vehicleId);
			List<PersonVehicleRel> list = list(Condition.getQueryWrapper(personVehicleRel));
			if (list != null && list.size() > 0) {
				throw new ServiceException("车辆(" + vehicle.getPlateNumber() + ")和人员(" + person.getPersonName() + ")已绑定");
			}
			personVehicleRel.setPersonName(person.getPersonName());
            save(personVehicleRel);

			workareaRelClient.syncDriverWorkArea(personVehicleRel.getVehicleId(),personVehicleRel.getPersonId(),"2", bladeUser.getUserId(),bladeUser.getDeptId(),bladeUser.getTenantId());
		});
	}

	@Override
	public List<Long> getRelVehiclesByuserId(Long userId) {
		PersonUserRel personUserRel = personUserRelService.getRelByUserOrPerson(userId, null);
		if(null==personUserRel){return null;}
		//找出关联的车辆id
		List<PersonVehicleRel> personVehicleRels = this.getVehicleByPersonId(personUserRel.getPersonId());
		if(null==personVehicleRels||personVehicleRels.size()==0){return null;}
		List<Long> returnlist  = new ArrayList<>();
		for (PersonVehicleRel personVehicleRel : personVehicleRels) {
			returnlist.add(personVehicleRel.getVehicleId());
		}
		return returnlist;
	}

}
