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
package com.ai.apac.smartenv.vehicle.service.impl;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.flow.feign.IFlowClient;
import com.ai.apac.smartenv.flow.feign.IFlowTaskAllotClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintMilestone;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrderMilestone;
import com.ai.apac.smartenv.vehicle.mapper.VehicleMaintOrderMapper;
import com.ai.apac.smartenv.vehicle.service.IVahicleMaintMilestoneService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;
import com.ai.apac.smartenv.vehicle.service.IVehicleMaintOrderService;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderApproveVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-08-06
 */
@Service
@Slf4j
public class VehicleMaintOrderServiceImpl extends BaseServiceImpl<VehicleMaintOrderMapper, VehicleMaintOrder> implements IVehicleMaintOrderService {
	@Autowired
	IVahicleMaintMilestoneService maintMilestoneService;
	@Autowired
	IFlowClient flowClient;
	@Autowired
	IFlowTaskAllotClient flowTaskAllotClient;
	@Autowired
	private IPersonClient personClient;
	@Autowired
	IVehicleInfoService vehicleInfoService;
	@Autowired
	private IDictBizClient dictBizClient;
	@Override
	public IPage<VehicleMaintOrderVO> selectVehicleMaintOrderPage(IPage<VehicleMaintOrderVO> page, VehicleMaintOrderVO vehicleMaintOrder) {
		return page.setRecords(baseMapper.selectVehicleMaintOrderPage(page, vehicleMaintOrder));
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void saveVehcileMaintOrder(VehicleMaintOrder vo, Person captainPerson, Person managePerson){
		//检查流程是否配置
		if(!flowTaskAllotClient.checkFlowInfoConfig(VehicleConstant.VehicleMaintenanceProcess.CODE,AuthUtil.getTenantId()).getData()) {
			throw new ServiceException("请先检查流程节点人员配置。");
		}
		Map param = new HashMap<>();

		VehicleInfo vehicleInfo = VehicleCache.getVehicleById(AuthUtil.getTenantId(), vo.getVehicleId());
		param.put(VehicleConstant.VehicleMaintenanceProcess.agree, 0);
		param.put(VehicleConstant.VehicleMaintenanceProcess.SECOND_USER_PERSON,managePerson);
		param.put(VehicleConstant.VehicleMaintenanceProcess.VEHICLE_ID,vo.getVehicleId());
		param.put(VehicleConstant.VehicleMaintenanceProcess.VEHICLE_STATE,vehicleInfo.getStatus());
		String processInstId = (String) flowClient.startProcessInstanceByKey(VehicleConstant.VehicleMaintenanceProcess.CODE,VehicleConstant.VehicleMaintenanceProcess.CODE,param).getData();


		VehicleMaintMilestone vehicleMaintMilestone = new VehicleMaintMilestone();
		vehicleMaintMilestone.setTaskId(processInstId);
		vehicleMaintMilestone.setTaskDefineName(VehicleConstant.VehicleMaintenanceProcess.FIRST_PERSON);

		//保存申请单
		vo.setStatus(VehicleConstant.VehicleMaintanceStatus.SUBMIT);
		vo.setWorkflowId(processInstId);
		vo.setExt1(vehicleInfo.getStatus().toString());

		save(vo);

		//保存申请里程碑
		vehicleMaintMilestone.setOrderId(vo.getId());
		vehicleMaintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.TODO);
		vehicleMaintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.TODO);
		maintMilestoneService.save(vehicleMaintMilestone);
		//根据配置创建待执行任务
		flowTaskAllotClient.createFlowTask(VehicleConstant.VehicleMaintenanceProcess.CODE,VehicleConstant.VehicleMaintenanceProcess.FIRST_PERSON,vo.getId(),AuthUtil.getTenantId());
		if (VehicleConstant.Maint_Type.MAINT.equals(vo.getApplyType())) {
			//更新车辆状态为维修中
			vehicleInfoService.updateVehicleStateById(VehicleConstant.VehicleState.MAINTAIN,vo.getVehicleId());
		}

	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void vechileMainFirstApprove(VehicleMaintOrderApproveVO maintOrderApproveVO) {
		VehicleMaintOrder maintOrder = complateNode(maintOrderApproveVO,VehicleConstant.VehicleMaintenanceProcess.FIRST_PERSON);
		if (VehicleConstant.VehicleMaintanceStatus.REFUSE.equals(maintOrderApproveVO.getDoneResult()) ) {
			return;
		}
		//创建填写预算节点
		VehicleMaintMilestone vehicleMaintMilestone = new VehicleMaintMilestone();
		vehicleMaintMilestone.setAssignmentId(maintOrder.getApplyPersonId().toString());
		Person person = personClient.getPerson(maintOrder.getApplyPersonId()).getData();
		vehicleMaintMilestone.setAssignmentJobNum(person.getJobNumber());
		vehicleMaintMilestone.setAssignmentName(person.getPersonName());
		vehicleMaintMilestone.setTaskId(maintOrder.getWorkflowId());
		vehicleMaintMilestone.setTaskDefineName(VehicleConstant.VehicleMaintenanceProcess.BUDGET);
		vehicleMaintMilestone.setOrderId(maintOrder.getId());
		vehicleMaintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.TODO);
		vehicleMaintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.TODO);
		maintMilestoneService.save(vehicleMaintMilestone);


	}

	@Override
	public VehicleMaintOrderMilestone queryVehicleMaintOrderMilestone(Long orderId) {
		Map<String,Long> paramMap = new HashMap<>();
		paramMap.put("orderId",orderId);
		List<VehicleMaintOrderMilestone> orderList= baseMapper.selectVehicleMaintOrderMilestone(paramMap);
		if (null != orderList && orderList.size()>0) {
			return orderList.get(0);
		}
		return null;
	}

	@Override
	public IPage<VehicleMaintOrder> queryVehicleMaintOrderPage(IPage<VehicleMaintOrder> page, QueryWrapper queryWrapper) {

		return baseMapper.selectPage(page,queryWrapper);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void vechileMainBudget(VehicleMaintOrder vehicleMaintOrder) {
		VehicleMaintOrder order = getById(vehicleMaintOrder.getId());
		//更新预算信息和订单状态
		order.setMaintAddress(vehicleMaintOrder.getMaintAddress());
		order.setMaintDate(vehicleMaintOrder.getMaintDate());
		order.setMaintAmount(vehicleMaintOrder.getMaintAmount());
		order.setMaintContext(vehicleMaintOrder.getMaintContext());
		order.setMaintPicture(vehicleMaintOrder.getMaintPicture());
		order.setStatus(VehicleConstant.VehicleMaintanceStatus.BUDGET);
		this.updateById(order);
		//更新预算填写过程
		LambdaQueryWrapper<VehicleMaintMilestone> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(VehicleMaintMilestone::getOrderId,vehicleMaintOrder.getId());
		queryWrapper.eq(VehicleMaintMilestone::getTaskDefineName,VehicleConstant.VehicleMaintenanceProcess.BUDGET);
		queryWrapper.eq(VehicleMaintMilestone::getStatus, VehicleConstant.VehicleMaintanceStatus.TODO);
		VehicleMaintMilestone maintMilestone = maintMilestoneService.getOne(queryWrapper);
		if (null == maintMilestone) {
			throw new ServiceException("未找到待处理任务");
		}

		Map<String,Object> paramMap = Kv.newMap();
		flowClient.completeTask("",VehicleConstant.VehicleMaintenanceProcess.BUDGET,maintMilestone.getTaskId(),"",paramMap);

		//更新审批状态
		maintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.BUDGET);
		maintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.BUDGET);
		maintMilestoneService.updateById(maintMilestone);

		//创建经理审批节点
		//String manageId = DictBizCache.getValue(AuthUtil.getTenantId(), VehicleConstant.VehicleMaintenanceProcess.CODE,VehicleConstant.VehicleMaintenanceProcess.SECOND_USER);
		//String manageId = dictBizClient.getTenantCodeDictValue(AuthUtil.getTenantId(),VehicleConstant.VehicleMaintenanceProcess.CODE,VehicleConstant.VehicleMaintenanceProcess.SECOND_USER).getData();

		//Person managePerson = personClient.getPerson(Func.toLong(manageId)).getData();
		VehicleMaintMilestone vehicleMaintMilestone = new VehicleMaintMilestone();
		/*vehicleMaintMilestone.setAssignmentId(managePerson.getId().toString());
		vehicleMaintMilestone.setAssignmentJobNum(managePerson.getJobNumber());
		vehicleMaintMilestone.setAssignmentName(managePerson.getPersonName());*/
		vehicleMaintMilestone.setTaskId(vehicleMaintOrder.getWorkflowId());
		vehicleMaintMilestone.setTaskDefineName(VehicleConstant.VehicleMaintenanceProcess.SECOND_PERSON);
		vehicleMaintMilestone.setOrderId(vehicleMaintOrder.getId());
		vehicleMaintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.TODO);
		vehicleMaintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.TODO);
		maintMilestoneService.save(vehicleMaintMilestone);
		//创建待处理任务
		flowTaskAllotClient.createFlowTask(VehicleConstant.VehicleMaintenanceProcess.CODE, VehicleConstant.VehicleMaintenanceProcess.SECOND_PERSON,order.getId(),AuthUtil.getTenantId());
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public IPage<VehicleMaintOrder> queryVehicleMaintOrderApprovePage(Query query, QueryWrapper queryWrapper) {

		List<VehicleMaintOrderMilestone> list = baseMapper.queryVehicleMaintOrder(queryWrapper);
		if (CollectionUtil.isNotEmpty(list)) {
			Set<Long> orderIdList = new HashSet<>();
			list.forEach(orderMilestone-> {
				orderIdList.add(orderMilestone.getId());
			});
			QueryWrapper<VehicleMaintOrder> lambdaQueryWrapper = new QueryWrapper<>();
			lambdaQueryWrapper.in("id",orderIdList);
			return baseMapper.selectPage(Condition.getPage(query),lambdaQueryWrapper);
		}
		return Condition.getPage(query);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void vechileMainManageApprove(VehicleMaintOrderApproveVO maintOrderApproveVO) {
		VehicleMaintOrder order = complateNode(maintOrderApproveVO,VehicleConstant.VehicleMaintenanceProcess.SECOND_PERSON);
		if (VehicleConstant.VehicleMaintanceStatus.REFUSE.equals(maintOrderApproveVO.getDoneResult()) ) {
			return;
		}
		//创建填写预算节点
		VehicleMaintMilestone vehicleMaintMilestone = new VehicleMaintMilestone();
		vehicleMaintMilestone.setAssignmentId(order.getApplyPersonId().toString());

		vehicleMaintMilestone.setAssignmentJobNum(order.getApplyJobNum());
		vehicleMaintMilestone.setAssignmentName(order.getApplyPersonName());
		vehicleMaintMilestone.setTaskId(order.getWorkflowId());
		vehicleMaintMilestone.setTaskDefineName(VehicleConstant.VehicleMaintenanceProcess.TO_FINISH);
		vehicleMaintMilestone.setOrderId(order.getId());
		vehicleMaintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.TODO);
		vehicleMaintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.TODO);
		maintMilestoneService.save(vehicleMaintMilestone);

	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void vechileMainToFinish(VehicleMaintOrder vehicleMaintOrder) {
		VehicleMaintOrder order = getById(vehicleMaintOrder.getId());
		//更新预算信息和订单状态
		order.setMaintFinishContext(vehicleMaintOrder.getMaintFinishContext());
		order.setMaintFinishDate(vehicleMaintOrder.getMaintFinishDate());
		order.setMaintFinishPrice(vehicleMaintOrder.getMaintFinishPrice());
		order.setMaintFinishPicture(vehicleMaintOrder.getMaintFinishPicture());

		order.setStatus(VehicleConstant.VehicleMaintanceStatus.FINISH);
		this.updateById(order);
		//更新预算填写过程
		LambdaQueryWrapper<VehicleMaintMilestone> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(VehicleMaintMilestone::getOrderId,vehicleMaintOrder.getId());
		queryWrapper.eq(VehicleMaintMilestone::getTaskDefineName,VehicleConstant.VehicleMaintenanceProcess.TO_FINISH);
		queryWrapper.eq(VehicleMaintMilestone::getStatus, VehicleConstant.VehicleMaintanceStatus.TODO);
		VehicleMaintMilestone maintMilestone = maintMilestoneService.getOne(queryWrapper);
		if (null == maintMilestone) {
			throw new ServiceException("未找到待处理任务");
		}
		//更新审批状态
		maintMilestone.setStatus(VehicleConstant.VehicleMaintanceStatus.FINISH);
		maintMilestone.setDoneResult(VehicleConstant.VehicleMaintanceStatus.FINISH);
		maintMilestoneService.updateById(maintMilestone);
		//更新任务
		Map<String,Object> paramMap = Kv.newMap();
		flowClient.completeTask("",VehicleConstant.VehicleMaintenanceProcess.TO_FINISH,maintMilestone.getTaskId(),"",paramMap);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void cancelOrder(String id) {
		VehicleMaintOrder order = getById(Func.toLong(id));
		if (null == order) {
			throw new ServiceException("订单不存在");
		}
		if (VehicleConstant.VehicleMaintanceStatus.SUBMIT != order.getStatus()) {
			throw new ServiceException("订单不允许取消");
		}
		order.setStatus(VehicleConstant.VehicleMaintanceStatus.CANCLE);
		this.updateById(order);
		//更新车辆状态未原状态
		vehicleInfoService.updateVehicleStateById(Func.toInt(order.getExt1()),order.getVehicleId());
		//删除待处理任务


	}

	@Override
	public IPage<VehicleMaintOrder> queryVehicleMaintOrderRecord(IPage<VehicleMaintOrder> page, QueryWrapper<VehicleMaintOrder> queryWrapper) {
		return baseMapper.selectPage(page,queryWrapper);
	}


	private VehicleMaintOrder complateNode(VehicleMaintOrderApproveVO maintOrderApproveVO,String currentTask){
		VehicleMaintOrder maintOrder = getById(maintOrderApproveVO.getOrderId());
		if (null == maintOrder) {
			throw new ServiceException("该订单不存在");
		}

		LambdaQueryWrapper<VehicleMaintMilestone> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(VehicleMaintMilestone::getOrderId,maintOrderApproveVO.getOrderId());
		queryWrapper.eq(VehicleMaintMilestone::getTaskDefineName,currentTask);
		queryWrapper.eq(VehicleMaintMilestone::getStatus, VehicleConstant.VehicleMaintanceStatus.TODO);
		VehicleMaintMilestone maintMilestone = maintMilestoneService.getOne(queryWrapper);
		if (null == maintMilestone) {
			throw new ServiceException("未找到待处理任务");
		}


		//更新订单状态
		maintOrder.setStatus(maintOrderApproveVO.getDoneResult());
		updateById(maintOrder);
		//更新处理人信息
		PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
		if (null != personUserRel) {
			Long personId = personUserRel.getPersonId();
			Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
			if (null != person) {
				maintMilestone.setAssignmentId(personId.toString());
				maintMilestone.setAssignmentJobNum(person.getJobNumber());
				maintMilestone.setAssignmentName(person.getPersonName());
			}
		}
		//更新审批状态
		maintMilestone.setStatus(maintOrderApproveVO.getDoneResult());
		maintMilestone.setDoneResult(maintOrderApproveVO.getDoneResult());
		maintMilestone.setDoneRemark(maintOrderApproveVO.getDoneRemark());
		maintMilestoneService.updateById(maintMilestone);

		//更新状态
		Map<String,Object> paramMap = Kv.newMap();
		paramMap.put(VehicleConstant.VehicleMaintenanceProcess.agree,maintOrderApproveVO.getDoneResult());
		paramMap.put(VehicleConstant.VehicleMaintenanceProcess.remark,maintOrderApproveVO.getDoneRemark());
		//更新待处理人任务
		flowTaskAllotClient.finishTask(VehicleConstant.VehicleMaintenanceProcess.CODE,maintOrder.getWorkflowId(),paramMap,maintOrder.getId(),currentTask);
		return maintOrder;
	}
}
