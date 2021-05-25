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
package com.ai.apac.smartenv.inventory.service.impl;

import cn.hutool.core.date.DateTime;
import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.flow.feign.IFlowTaskAllotClient;
import com.ai.apac.smartenv.inventory.entity.*;
import com.ai.apac.smartenv.inventory.mapper.ResOrderMapper;
import com.ai.apac.smartenv.inventory.service.*;
import com.ai.apac.smartenv.inventory.vo.*;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.RoleMenu;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Service
@AllArgsConstructor
public class ResOrderServiceImpl extends BaseServiceImpl<ResOrderMapper, ResOrder> implements IResOrderService {
	IResManageService resManageService;
	IResOrderDtlService resOrderDtlService;
	IResTypeService resTypeService;
	IResSpecService resSpecService;
	IResOrderMilestoneService milestoneService;
	IPersonClient personClient;
	ISysClient sysClient;
	IUserClient userClient;
	IFlowTaskAllotClient flowTaskAllotClient;


	@Override
	public IPage<ResOrderVO> selectResOrderPage(IPage<ResOrderVO> page, ResOrderVO resOrder) {
		return page.setRecords(baseMapper.selectResOrderPage(page, resOrder));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResOrder resApplySubmitOrder(ResInfoApplyVO infoApplyVO) {
		List<ResOrderDtlVO> orderDtlList = infoApplyVO.getOrderDtlList();
		if (CollectionUtil.isEmpty(orderDtlList)) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_RESAPPLY_DTL));
		}
		ResOrder resOrder = new ResOrder();
		resOrder.setCustId(infoApplyVO.getCustId());
		resOrder.setCustName(infoApplyVO.getCustName());
		resOrder.setBusinessType(InventoryConstant.ResBusinessType.RES_APPLY);
		resOrder.setDescription(infoApplyVO.getApplyDescription());
		resOrder.setOrderStatus(InventoryConstant.Order_Status.SUBMIT);
		resOrder.setUpdateTime(TimeUtil.addOrMinusSecond(TimeUtil.getSysDate().getTime(),-5));

		resOrder.setCreateUser(infoApplyVO.getCreateUser());
		resOrder.setCreateDept(infoApplyVO.getCreateDept());
		resOrder.setUpdateUser(infoApplyVO.getUpdateUser());
		resOrder.setTenantId(infoApplyVO.getTenantId());

		this.save(resOrder);
		for (ResOrderDtlVO resOrderDtlVO:orderDtlList) {
			resOrderDtlVO.setOrderId(resOrder.getId());
			resOrderDtlVO.setTenantId(resOrder.getTenantId());
			resOrderDtlVO.setStatus(1);
			resOrderDtlVO.setIsDeleted(0);
			resOrderDtlVO.setIsDeleted(0);
		}
		resOrderDtlService.saveBatch(BeanUtil.copy(orderDtlList, ResOrderDtl.class));

		//创建订单里程碑
		ResOrderMilestone orderMilestone = new ResOrderMilestone();
		orderMilestone.setOrderId(resOrder.getId());
		orderMilestone.setAssignmentId(Func.toStr(infoApplyVO.getCustId()));
		orderMilestone.setAssignmentName(infoApplyVO.getCustName());
		orderMilestone.setTaskDefineName(InventoryConstant.Flow_Key.RES_FLOW_START);
		orderMilestone.setDoneResult(Func.toStr(InventoryConstant.Order_Status.SUBMIT));
		orderMilestone.setTenantId(resOrder.getTenantId());
		orderMilestone.setCreateUser(infoApplyVO.getCreateUser());
		orderMilestone.setCreateDept(infoApplyVO.getCreateDept());
		orderMilestone.setUpdateUser(infoApplyVO.getUpdateUser());
		orderMilestone.setTenantId(infoApplyVO.getTenantId());

		Date now = DateUtil.now();
		orderMilestone.setCreateTime(now);
		orderMilestone.setUpdateTime(TimeUtil.addOrMinusSecond(TimeUtil.getSysDate().getTime(),-5));
		if (orderMilestone.getStatus() == null) {
			orderMilestone.setStatus(1);
		}

		orderMilestone.setIsDeleted(0);
		milestoneService.saveOrderMileStone(orderMilestone);

		return resOrder;
	}

	@Override
	public void applyBefore(Long orderId, String taskId) {

	}

	@Override
	public IPage<ResApplyQueryResponseVO> listApplyOrderPage(IPage<Object> page, QueryWrapper queryWrapper) {
		queryWrapper.eq("res.is_deleted",0);
		return page.setRecords(baseMapper.listApplyOrderPage(page,queryWrapper));
	}

	@Override
	public Boolean updateOrderStatus(Long orderId, String processId, Integer status) {
		return baseMapper.updateOrderStatus(orderId,processId,status);
	}

	@Override
	public Boolean cancelResOrder(Long orderId) {
		ResOrder resOrder = getById(orderId);
		if(null == resOrder) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_INVENTORY_ORDER));
		}
		if (InventoryConstant.Order_Status.SUBMIT != resOrder.getOrderStatus().intValue() && InventoryConstant.Order_Status.APPROVE != resOrder.getOrderStatus().intValue()) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_INVENTORY_CANCEL));
		}
		resOrder.setOrderStatus(InventoryConstant.Order_Status.CANCEL);
		//删除订单里程碑信息
		List<ResOrderMilestone> milestones = milestoneService.list(new LambdaQueryWrapper<ResOrderMilestone>().eq(ResOrderMilestone::getOrderId,orderId));
		if (CollectionUtil.isNotEmpty(milestones)) {
			List<Long> ids = new ArrayList<>();
			milestones.forEach(resOrderMilestone -> {
				if (InventoryConstant.Flow_Key.RES_FLOW_APPLY.equals(resOrderMilestone.getTaskDefineName())) {
					//更新里程碑信息为已撤单
					resOrderMilestone.setDoneResult(Func.toStr(InventoryConstant.Order_Status.CANCEL));
					milestoneService.updateOrderMilestoneByCond(resOrderMilestone);
				}


			});
		}

		return updateById(resOrder);
	}

	public List<ResOrder4HomeVO> lastResOrders(String tenantId,String userId) {
		List<ResOrder4HomeVO> resOrder4HomeVOList = new ArrayList<ResOrder4HomeVO>();

		List<Long> todoOrderIdList = new ArrayList<Long>();
		PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(Long.parseLong(userId));
		Long  personId = personUserRel.getPersonId();

		User user = UserCache.getUser(Long.parseLong(userId));
		//查询该登录人的roleid
		String roleIds = user.getRoleId();

		//查询该登录人的岗位
		Person person = PersonCache.getPersonById(tenantId,personId);
		Long positionId = null;
		if (null != person) {
			positionId = person.getPersonPositionId();
		}
		List<Long> todoOrderIdList1 = flowTaskAllotClient.getFlowTask(personId,positionId,roleIds,"Delivery").getData();
		if(ObjectUtil.isNotEmpty(todoOrderIdList1) && todoOrderIdList1.size() > 0){
			todoOrderIdList.addAll(todoOrderIdList1);
		}
		List<Long> todoOrderIdList2 = flowTaskAllotClient.getFlowTask(personId,positionId,roleIds,"Apply").getData();
		if(ObjectUtil.isNotEmpty(todoOrderIdList2) && todoOrderIdList2.size() > 0){
			todoOrderIdList.addAll(todoOrderIdList2);
		}

		if(todoOrderIdList.size() == 0){
			return resOrder4HomeVOList;
		}

		List<Integer> orderStatusList = new ArrayList<>();
		orderStatusList.add(2);
		orderStatusList.add(5);

		QueryWrapper<ResOrder> wrapper = new QueryWrapper<ResOrder>();
		Date startTime = cn.hutool.core.date.DateUtil.beginOfDay(Calendar.getInstance().getTime());
		Date endTime = DateTime.now();

		wrapper.lambda().in(ResOrder::getOrderStatus,orderStatusList).in(ResOrder::getId,todoOrderIdList)
		.between(ResOrder::getUpdateTime, new Timestamp(startTime.getTime()),new Timestamp(endTime.getTime()))
				.orderByDesc(ResOrder::getUpdateTime)
				.last("limit 0 , 6");
		List<ResOrder> resOrderList = this.list(wrapper);
		if(resOrderList.size() > 0 ){
			resOrderList.forEach(resOrder -> {
				ResOrder4HomeVO resOrder4HomeVO = Objects.requireNonNull(BeanUtil.copy(resOrder, ResOrder4HomeVO.class));
				resOrder4HomeVO.setOrderId(resOrder.getId().toString());
				QueryWrapper<ResOrderDtl> resOrderDtlQueryWrapper = new QueryWrapper<ResOrderDtl>();
				resOrderDtlQueryWrapper.lambda().eq(ResOrderDtl::getOrderId,resOrder.getId());
				List<ResOrderDtl> resOrderDtls =  resOrderDtlService.list(resOrderDtlQueryWrapper);
				ResType resType = resTypeService.getById(resOrderDtls.get(0).getResTypeId());
				resOrder4HomeVO.setResTypeName(resType.getTypeName());
				ResSpec resSpec = resSpecService.getById(resOrderDtls.get(0).getResSpecId());
				resOrder4HomeVO.setResSpecName(resSpec.getSpecName());
				resOrder4HomeVO.setRelUserName(user.getName());
				resOrder4HomeVOList.add(resOrder4HomeVO);
			});
		}
		return resOrder4HomeVOList;

	}

	@Override
	public Boolean canInvApprove() {
		Long id = SystemConstant.MenuId.INV_APPROVE;
		List<RoleMenu> roleMenuList = sysClient.getRolesByMenuId(id).getData();
		User user = userClient.userInfoById(AuthUtil.getUserId()).getData();
		List<String> roles = Arrays.asList(user.getRoleId().split(","));
		for(String role:roles){
			for(RoleMenu roleMenu : roleMenuList){
				if(roleMenu.getRoleId().toString().equals(role)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<ResOrder4MiniVO> lastResOrder4Mini(Integer num) {
		List<ResOrder4MiniVO> resOrder4MiniVOList = new ArrayList<ResOrder4MiniVO>();
		QueryWrapper<ResOrder> wrapper = new QueryWrapper<ResOrder>();
		List<Integer> orderStatusList = new ArrayList<>();
		orderStatusList.add(2);
		wrapper.lambda().eq(ResOrder::getBusinessType,InventoryConstant.ResBusinessType.RES_APPLY).in(ResOrder::getOrderStatus,orderStatusList).orderByDesc(ResOrder::getUpdateTime).last("limit 0 ,"+num);
		List<ResOrder> resOrderList = this.list(wrapper);
		resOrderList.forEach(resOrder -> {
			ResOrder4MiniVO resOrder4MiniVO = new ResOrder4MiniVO();
			String personName = personClient.getPerson(resOrder.getCustId()).getData().getPersonName();
			String NickName = "NA";
			if(StringUtil.isNotBlank(personName)){
				NickName = personName.length() > 2?personName.substring(personName.length()-2):personName;
			}
			resOrder4MiniVO.setCustName(personName);
			resOrder4MiniVO.setCustId(resOrder.getCustId().toString());
			resOrder4MiniVO.setNickName(NickName);
			resOrder4MiniVO.setId(resOrder.getId().toString());
			resOrder4MiniVO.setResOrderName(resOrder.getDescription());
			resOrder4MiniVO.setResOrderTime(TimeUtil.formDateToTimestamp(resOrder.getUpdateTime()));
			resOrder4MiniVO.setBusinessType(resOrder.getBusinessType());
			resOrder4MiniVO.setBusinessTypeName(DictCache.getValue("order_business_type",resOrder.getBusinessType()));
			QueryWrapper<ResOrderDtl> wrapper_ = new QueryWrapper<ResOrderDtl>();
			wrapper_.lambda().eq(ResOrderDtl::getOrderId,resOrder.getId());
			List<ResOrderDtl> resOrderDtlList = resOrderDtlService.list(wrapper_);
			Integer resNum = 0;
			for(ResOrderDtl resOrderDtl:resOrderDtlList){
				resNum = resNum + resOrderDtl.getAmount();
			}
			resOrder4MiniVO.setResNum(resNum.toString());
			resOrder4MiniVOList.add(resOrder4MiniVO);
		});
		return resOrder4MiniVOList;
	}

	@Override
	public Integer countResOrder4Mini() {
		QueryWrapper<ResOrder> wrapper = new QueryWrapper<ResOrder>();
		List<Integer> orderStatusList = new ArrayList<>();
		orderStatusList.add(2);
		wrapper.lambda().eq(ResOrder::getBusinessType,InventoryConstant.ResBusinessType.RES_APPLY).in(ResOrder::getOrderStatus,orderStatusList);
		return this.count(wrapper);
	}

	@Override
	public ResApplyDetailVO resApplyOrderDetail(Long orderId) {
		ResApplyDetailVO applyDetailVO = new ResApplyDetailVO();
		ResOrder resOrder = getById(orderId);
		if (null == resOrder) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_INVENTORY_ORDER));
		}
		applyDetailVO.setOrderId(orderId);
		applyDetailVO.setApplyName(resOrder.getCustName());
		applyDetailVO.setApplyTime(resOrder.getCreateTime());
		applyDetailVO.setRemark(resOrder.getDescription());
		applyDetailVO.setProcessInstanceId(resOrder.getWorkflowId());
		//获取申请人部门信息,
		try {
			Person person = personClient.getPerson(resOrder.getCustId()).getData();
			if (null != person) {
				applyDetailVO.setApplyDepartment(sysClient.getDeptName(person.getPersonDeptId()).getData());
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		//申请物资详情
		QueryWrapper<ResOrderDtl> queryWrapper = new QueryWrapper();
		queryWrapper.eq("dtl.order_id",orderId);
		List<ResOrderDtlVO> orderMilestoneList = resOrderDtlService.selectResOrderDtlInfoPage(queryWrapper);
		applyDetailVO.setResOrderDtlVOList(orderMilestoneList);


		return applyDetailVO;
	}

}
