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
package com.ai.apac.smartenv.person.controller;

import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.vo.PersonGetVehicleCVO;
import com.ai.apac.smartenv.person.vo.PersonGetVehicleVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.vo.PersonVehicleRelVO;
import com.ai.apac.smartenv.person.wrapper.PersonVehicleRelWrapper;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.service.IPersonVehicleRelService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;

/**
 * 人员与车辆关系表 控制器
 *
 * @author Blade
 * @since 2020-02-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personvehiclerel")
@Api(value = "人员与车辆关系表", tags = "人员与车辆关系表接口")
public class PersonVehicleRelController extends BladeController {

	private IPersonVehicleRelService personVehicleRelService;
	private IPersonService personService;
	private IVehicleClient vehicleClient;
	private IWorkareaRelClient workareaRelClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入personVehicleRel")
	public R<PersonVehicleRelVO> detail(PersonVehicleRel personVehicleRel) {
		PersonVehicleRel detail = personVehicleRelService.getOne(Condition.getQueryWrapper(personVehicleRel));
		return R.data(PersonVehicleRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 人员与车辆关系表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入personVehicleRel")
	public R<IPage<PersonVehicleRelVO>> list(PersonVehicleRel personVehicleRel, Query query) {
		IPage<PersonVehicleRel> pages = personVehicleRelService.page(Condition.getPage(query), Condition.getQueryWrapper(personVehicleRel));
		return R.data(PersonVehicleRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 人员与车辆关系表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入personVehicleRel")
	public R<IPage<PersonVehicleRelVO>> page(PersonVehicleRelVO personVehicleRel, Query query) {
		IPage<PersonVehicleRelVO> pages = personVehicleRelService.selectPersonVehicleRelPage(Condition.getPage(query), personVehicleRel);
		return R.data(pages);
	}

	/**
	 * 新增 人员与车辆关系表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入personVehicleRel")
	public R save(@Valid @RequestBody PersonVehicleRel personVehicleRel) {
		return R.status(personVehicleRelService.save(personVehicleRel));
	}

	/**
	 * 修改 人员与车辆关系表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入personVehicleRel")
	public R update(@Valid @RequestBody PersonVehicleRel personVehicleRel) {
		return R.status(personVehicleRelService.updateById(personVehicleRel));
	}

	/**
	 * 新增或修改 人员与车辆关系表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入personVehicleRel")
	public R submit(@Valid @RequestBody PersonVehicleRel personVehicleRel) {
		return R.status(personVehicleRelService.saveOrUpdate(personVehicleRel));
	}

	
	/**
	 * 删除 人员与车辆关系表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(personVehicleRelService.batchRemove(Func.toLongList(ids)));
	}

	/**
	 * 车辆绑定驾驶员
	 */
	@PostMapping("bindDriver")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "车辆绑定驾驶员", notes = "")
	public R bindDriver(@RequestBody PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser) {
		personVehicleRelService.vehicleBindPerson(personVehicleRelVO, bladeUser);
		return R.status(true);
	}
	
	/**
	 * 驾驶员绑定车辆FacilityManageControlle
	 */
	@PostMapping("personBindVehicle")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "驾驶员绑定车辆", notes = "")
	public R personBindVehicle(@RequestBody PersonVehicleRelVO personVehicleRelVO, BladeUser bladeUser) {
		personVehicleRelService.personBindVehicle(personVehicleRelVO, bladeUser);
		return R.status(true);
	}


	/**
	 * 驾驶员绑定车辆FacilityManageControlle
	 */
	@GetMapping("/getRelVehicle")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "获取当前登录人绑定的车辆列表", notes = "")
	public R<List<PersonGetVehicleVO>>  getRelVehicle(String userId) {
		//根据userId获取personid
		if(userId==null||StringUtil.isBlank(userId)){
			userId =getUser().getUserId().toString();
		}
		BladeUser user = AuthUtil.getUser();
		boolean isAdmin = false;
		if("admin".equals(user.getRoleGroup())||"administrator".equals(user.getRoleGroup())){
			isAdmin=true;
		}
		if(isAdmin){
			//找出所有已绑定驾驶员的车辆
			VehicleInfoVO vehicleInfo = new VehicleInfoVO();
			vehicleInfo.setTenantId(user.getTenantId());
			List<VehicleInfo> vehicleInfos = vehicleClient.listVehicle(vehicleInfo).getData();
			if(null==vehicleInfos||vehicleInfos.size()==0){return null;}
			List<PersonGetVehicleVO> relist = new ArrayList<>();
			for (VehicleInfo vInfo : vehicleInfos) {
				PersonGetVehicleVO p = new PersonGetVehicleVO();
				p.setVehicleId(vInfo.getId());
				p.setPlateNmuber(vInfo.getPlateNumber());
				relist.add(p);
			}
			return  R.data(relist);
		}
		//找出关联的车辆id
		List<Long> vehicleIds = personVehicleRelService.getRelVehiclesByuserId(Long.valueOf(userId));
		List<PersonGetVehicleVO> relist = new ArrayList<>();
		if(null!=vehicleIds){
			for (Long vehicleId : vehicleIds) {
				VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, vehicleId);
				if(null!=vehicleInfo) {
					PersonGetVehicleVO vehicleVO = new PersonGetVehicleVO();
					vehicleVO.setVehicleId(vehicleId);
					vehicleVO.setPlateNmuber(vehicleInfo.getPlateNumber());
					relist.add(vehicleVO);
				}
			}
		}

		return  R.data(relist);
	}


	/**
	 * 驾驶员绑定车辆FacilityManageControlle
	 */
	@GetMapping("/getRelPerson")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据传入的车辆id获取绑定的驾驶员信息", notes = "")
	public R<PersonGetVehicleCVO>  getRelPerson(String vehicleId) {
		if(StringUtil.isBlank(vehicleId)){
			return R.fail("驾驶员id为空");
		}
		//找出关联的车辆id
		List<PersonVehicleRel>  personVehicleRels = personVehicleRelService.getPersonByVehicle (Long.valueOf(vehicleId));
		List<PersonGetVehicleVO> relist = new ArrayList<>();
		if(null!=personVehicleRels){
			for (PersonVehicleRel personVehicleRel : personVehicleRels) {
				Person person = PersonCache.getPersonById (null,personVehicleRel.getPersonId() );
				if(null!=person) {
					PersonGetVehicleVO vehicleVO = new PersonGetVehicleVO();
					vehicleVO.setPersonId(person.getId());
					if(StringUtil.isBlank(person.getJobNumber())){
						vehicleVO.setPersonName(person.getPersonName());
					}else {
						vehicleVO.setPersonName(person.getPersonName()+"("+person.getJobNumber()+")");
					}
					relist.add(vehicleVO);
				}
			}
		}

		PersonGetVehicleCVO returnBean = new PersonGetVehicleCVO();
		returnBean.setPersonlist(relist);
		VehicleInfo vehicleInfo = VehicleCache.getVehicleById(AuthUtil.getTenantId(), Long.valueOf(vehicleId));
		if(null!=vehicleInfo && StringUtil.isNotBlank(vehicleInfo.getFuelType())){
			String dictKey = vehicleInfo.getFuelType()+vehicleInfo.getRoz();
			String oilName = DictCache.getValue(VehicleConstant.VehicleRefuel.REFUEL_OIL,dictKey);
			if (StringUtil.isNotBlank(oilName)){
				returnBean.setDefultOilType(dictKey);
				returnBean.setDefultOilTypeName(oilName);
			}

		}

		return  R.data(returnBean);
	}


}
