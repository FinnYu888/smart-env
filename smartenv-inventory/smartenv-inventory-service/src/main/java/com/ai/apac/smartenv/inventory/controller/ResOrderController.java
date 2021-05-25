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
package com.ai.apac.smartenv.inventory.controller;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.constant.ResultCodeConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.flow.feign.IFlowTaskAllotClient;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.service.IResManageService;
import com.ai.apac.smartenv.inventory.service.IResOrderDtlService;
import com.ai.apac.smartenv.inventory.service.IResOrderMilestoneService;
import com.ai.apac.smartenv.inventory.service.IResOrderService;
import com.ai.apac.smartenv.inventory.vo.*;
import com.ai.apac.smartenv.inventory.wrapper.ResOrderWrapper;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-25
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("resorder")
@Api(value = "物资订单管理", tags = "物资订单管理")
public class ResOrderController extends BladeController {
	private IResOrderService resOrderService;
	private IResManageService resManageService;
	IResOrderDtlService resOrderDtlService;
	IResOrderMilestoneService milestoneService;
	IDictClient dictClient;
	IDictBizClient dictBizClient;
	IPersonClient personClient;
	ISysClient sysClient;
	IFlowTaskAllotClient flowTaskAllotClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resOrder")
	public R<ResOrderVO> detail(ResOrder resOrder) {
		ResOrder detail = resOrderService.getOne(Condition.getQueryWrapper(resOrder));
		return R.data(ResOrderWrapper.build().entityVO(detail));
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入resOrder")
	public R<IPage<ResOrderVO>> list(ResOrder resOrder, Query query) {
		IPage<ResOrder> pages = resOrderService.page(Condition.getPage(query), Condition.getQueryWrapper(resOrder));
		return R.data(ResOrderWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入resOrder")
	public R<IPage<ResOrderVO>> page(ResOrderVO resOrder, Query query) {
		IPage<ResOrderVO> pages = resOrderService.selectResOrderPage(Condition.getPage(query), resOrder);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resOrder")
	public R save(@Valid @RequestBody ResOrder resOrder) {
		return R.status(resOrderService.save(resOrder));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resOrder")
	public R update(@Valid @RequestBody ResOrder resOrder) {
		return R.status(resOrderService.updateById(resOrder));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入resOrder")
	public R submit(@Valid @RequestBody ResOrder resOrder) {
		return R.status(resOrderService.saveOrUpdate(resOrder));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(resOrderService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	* 提交审批单查询接口
	*/
    @ApiLog(value = "提交审批单查询接口")
	@PostMapping("/listApplyOrder")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "提交审批单查询接口", notes = "传入orderMileStoneVO")
	public R<IPage<ResApplyQueryResponseVO>> listApplyOrder(@RequestParam(name = "resTypeId",required = false) Long resTypeId, @RequestParam(name = "resSpecId",required = false) Long resSpecId,
                                                            @RequestParam(name = "orderStatus",required = false) String orderStatus, @RequestParam(name = "startTime",required = false)String startTime, @RequestParam(name = "endTime",required = false)String endTime,
															@RequestParam(name = "applyName",required = false) String applyName,	@RequestParam(name = "submitUserId",required = false) String submitUserId,Query query , BladeUser bladeUser) {
		IPage<ResApplyQueryResponseVO> resApplyQueryResponseVOIPage =Condition.getPage(query);
		PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
		Long  personId = personUserRel.getPersonId();


		List<Long> orderIdList = null;
		List<Long> finalOrderIdList = new ArrayList<>();
    	if (null != resSpecId || null != resTypeId) {
    		QueryWrapper queryDtl= new QueryWrapper();
			queryDtl.eq("info.res_spec_id",resSpecId);
			queryDtl.eq("info.res_type_id",resTypeId);
			//queryDtl.in("order_id",todoOrderIdList);
    		List<ResOrderDtlVO> orderDtls = resOrderDtlService.selectResOrderDtlInfoPage(queryDtl);
    		if (CollectionUtil.isEmpty(orderDtls)) {
				return R.data(resApplyQueryResponseVOIPage);
			}


			orderDtls.forEach(resOrderDtl -> {
				finalOrderIdList.add(resOrderDtl.getOrderId());
			});
		}
        //查询订单列表
        QueryWrapper queryWrapper = new QueryWrapper();
		if (CollectionUtil.isNotEmpty(finalOrderIdList)) queryWrapper.in("res.id",finalOrderIdList);
		if (null != orderStatus) queryWrapper.in("res.order_status",orderStatus.split(InventoryConstant.SPLIT_COMMA));
		if (null != applyName) queryWrapper.like("res.cust_name",applyName);
		if (StringUtil.isNotBlank(startTime)) queryWrapper.ge("res.create_time",startTime);
		if (StringUtil.isNotBlank(endTime)) queryWrapper.le("res.create_time",endTime);
		if (StringUtil.isNotBlank(submitUserId)) queryWrapper.eq("res.create_user",submitUserId);
		if (null != bladeUser && StringUtil.isNotBlank(bladeUser.getTenantId())) queryWrapper.eq("res.tenant_id",bladeUser.getTenantId());
		queryWrapper.eq("cust_id",personId);
		 resApplyQueryResponseVOIPage = resOrderService.listApplyOrderPage(Condition.getPage(query),queryWrapper);
		List<ResApplyQueryResponseVO> resApplyQueryResponseVOList = resApplyQueryResponseVOIPage.getRecords();
		if (null == resApplyQueryResponseVOList || 0 == resApplyQueryResponseVOList.size()) {
			return R.data(resApplyQueryResponseVOIPage);
		}

		 orderIdList = new ArrayList<>();
		for (ResApplyQueryResponseVO responseVO:resApplyQueryResponseVOList) {
			orderIdList.add(responseVO.getId());
		}
		//查询订单详情
		queryWrapper = new QueryWrapper();
		queryWrapper.in("dtl.order_id",orderIdList);
		List<ResOrderDtlVO> orderDtlVOS = resOrderDtlService.selectResOrderDtlInfoPage(queryWrapper);
		if (null == orderDtlVOS && orderDtlVOS.size() == 0) {
			return R.data(resApplyQueryResponseVOIPage);
		}
		Map<Long, ResOrderMilestone> applyOrderMap = new HashMap<>();
		//查询审批结果

		if (StringUtil.isNotBlank(orderStatus)){
			queryWrapper = new QueryWrapper();
			queryWrapper.in("order_id",orderIdList);
			//queryWrapper.in("done_result",orderStatus.split(InventoryConstant.SPLIT_COMMA));
			queryWrapper.eq("task_define_name", InventoryConstant.Flow_Key.RES_FLOW_APPLY);
			List<ResOrderMilestone> orderMilestones = milestoneService.list(queryWrapper);
			if (CollectionUtil.isNotEmpty(orderMilestones)) {
				orderMilestones.forEach(resOrderMilestone -> {
					applyOrderMap.put(resOrderMilestone.getOrderId(),resOrderMilestone);
				});
			}

		}
		Map<Long,List<ResOrderDtlVO>> orderDtlVOMap = new HashMap<>();
		for (ResOrderDtlVO dtlVO:orderDtlVOS) {
			List<ResOrderDtlVO> orderDtlVOList = orderDtlVOMap.get(dtlVO.getOrderId());
			if (null == orderDtlVOList) {
				orderDtlVOList = new ArrayList<>();
			}
			orderDtlVOList.add(dtlVO);
			orderDtlVOMap.put(dtlVO.getOrderId(),orderDtlVOList);
		}
		Map<String,Object> orderStatusMap = dictClient.getMap(InventoryConstant.Order_Status.CODE).getData();
		//组装返回对象

		List<ResApplyQueryResponseVO> responseVOList = new ArrayList<>();
		for (ResApplyQueryResponseVO responseVO:resApplyQueryResponseVOList) {

			List<ResOrderDtlVO> orderDtlVOList = orderDtlVOMap.get(responseVO.getId());
			responseVO.setOrderDtlList(orderDtlVOList);
			//审批人
			ResOrderMilestone applyMilestone = applyOrderMap.get(responseVO.getId());
			if (null != applyMilestone && InventoryConstant.Flow_Key.RES_FLOW_APPLY.equals(applyMilestone.getTaskDefineName())) {
				responseVO.setApproverName(applyMilestone.getAssignmentName());
				responseVO.setApproveResult(Func.toStr(orderStatusMap.get(responseVO.getOrderStatus())));
			}
			//审批结果
			responseVO.setOrderStatusName(Func.toStr(orderStatusMap.get(responseVO.getOrderStatus())));
			responseVOList.add(responseVO);		}
		return R.data(resApplyQueryResponseVOIPage.setRecords(responseVOList));
	}
	/**
	 * 查询审批人待审批和已审批的单子
	 */
	@ApiLog(value = "查询审批人待审批和已审批的单子")
	@PostMapping("/listApplyOrderApprove")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询审批人待审批和已审批的单子", notes = "传入orderMileStoneVO")
	public R<IPage<ResApplyQueryResponseVO>> listApplyOrderApprove(@RequestParam(name = "resTypeId",required = false) Long resTypeId, @RequestParam(name = "resSpecId",required = false) Long resSpecId,
															@RequestParam(name = "orderStatus",required = false) String orderStatus, @RequestParam(name = "startTime",required = false)String startTime, @RequestParam(name = "endTime",required = false)String endTime,
															@RequestParam(name = "applyName",required = false) String applyName,	@RequestParam(name = "submitUserId",required = false) String submitUserId,Query query , @RequestParam(name = "taskNode",required = false)String taskNode) {
		IPage<ResApplyQueryResponseVO> resApplyQueryResponseVOIPage =Condition.getPage(query);

		 List<Long> todoOrderIdList = getTodoOrderList(taskNode);
		if (CollectionUtil.isEmpty(todoOrderIdList)) {
			return R.data(Condition.getPage(query));
		}
		List<Long> orderIdList = null;
		List<Long> finalOrderIdList = new ArrayList<>();
		if (null != resSpecId || null != resTypeId) {
			QueryWrapper queryDtl= new QueryWrapper();
			queryDtl.eq("info.res_spec_id",resSpecId);
			queryDtl.eq("info.res_type_id",resTypeId);
			//queryDtl.in("order_id",todoOrderIdList);
			List<ResOrderDtlVO> orderDtls = resOrderDtlService.selectResOrderDtlInfoPage(queryDtl);
			if (CollectionUtil.isEmpty(orderDtls)) {
				return R.data(resApplyQueryResponseVOIPage);
			}


			orderDtls.forEach(resOrderDtl -> {
				finalOrderIdList.add(resOrderDtl.getOrderId());
			});
		}
		//查询订单列表
		QueryWrapper queryWrapper = new QueryWrapper();
		if (CollectionUtil.isNotEmpty(finalOrderIdList)) queryWrapper.in("res.id",finalOrderIdList);
		if (StringUtil.isNotBlank(orderStatus)) queryWrapper.in("res.order_status",orderStatus.split(InventoryConstant.SPLIT_COMMA));
		if (StringUtil.isNotBlank(applyName)) queryWrapper.like("res.cust_name",applyName);
		if (StringUtil.isNotBlank(startTime)) queryWrapper.ge("res.create_time",startTime);
		if (StringUtil.isNotBlank(endTime)) queryWrapper.le("res.create_time",endTime);
		if (StringUtil.isNotBlank(submitUserId)) queryWrapper.eq("res.create_user",submitUserId);
		if (null != AuthUtil.getUser() && StringUtil.isNotBlank(AuthUtil.getTenantId())) queryWrapper.eq("res.tenant_id",AuthUtil.getTenantId());
		queryWrapper.in("res.id",todoOrderIdList);
		resApplyQueryResponseVOIPage = resOrderService.listApplyOrderPage(Condition.getPage(query),queryWrapper);
		List<ResApplyQueryResponseVO> resApplyQueryResponseVOList = resApplyQueryResponseVOIPage.getRecords();
		if (null == resApplyQueryResponseVOList || 0 == resApplyQueryResponseVOList.size()) {
			return R.data(resApplyQueryResponseVOIPage);
		}

		orderIdList = new ArrayList<>();
		for (ResApplyQueryResponseVO responseVO:resApplyQueryResponseVOList) {
			orderIdList.add(responseVO.getId());
		}
		//查询订单详情
		queryWrapper = new QueryWrapper();
		queryWrapper.in("dtl.order_id",orderIdList);
		List<ResOrderDtlVO> orderDtlVOS = resOrderDtlService.selectResOrderDtlInfoPage(queryWrapper);
		if (null == orderDtlVOS && orderDtlVOS.size() == 0) {
			return R.data(resApplyQueryResponseVOIPage);
		}
		Map<Long, ResOrderMilestone> applyOrderMap = new HashMap<>();
		//查询审批结果

		if (StringUtil.isNotBlank(orderStatus)){
			queryWrapper = new QueryWrapper();
			queryWrapper.in("order_id",orderIdList);
			//queryWrapper.in("done_result",orderStatus.split(InventoryConstant.SPLIT_COMMA));
			queryWrapper.eq("task_define_name", InventoryConstant.Flow_Key.RES_FLOW_APPLY);
			List<ResOrderMilestone> orderMilestones = milestoneService.list(queryWrapper);
			if (CollectionUtil.isNotEmpty(orderMilestones)) {
				orderMilestones.forEach(resOrderMilestone -> {
					applyOrderMap.put(resOrderMilestone.getOrderId(),resOrderMilestone);
				});
			}

		}
		Map<Long,List<ResOrderDtlVO>> orderDtlVOMap = new HashMap<>();
		for (ResOrderDtlVO dtlVO:orderDtlVOS) {
			List<ResOrderDtlVO> orderDtlVOList = orderDtlVOMap.get(dtlVO.getOrderId());
			if (null == orderDtlVOList) {
				orderDtlVOList = new ArrayList<>();
			}
			orderDtlVOList.add(dtlVO);
			orderDtlVOMap.put(dtlVO.getOrderId(),orderDtlVOList);
		}
		Map<String,Object> orderStatusMap = dictClient.getMap(InventoryConstant.Order_Status.CODE).getData();
		//组装返回对象

		List<ResApplyQueryResponseVO> responseVOList = new ArrayList<>();
		for (ResApplyQueryResponseVO responseVO:resApplyQueryResponseVOList) {

			List<ResOrderDtlVO> orderDtlVOList = orderDtlVOMap.get(responseVO.getId());
			responseVO.setOrderDtlList(orderDtlVOList);
			//审批人
			ResOrderMilestone applyMilestone = applyOrderMap.get(responseVO.getId());
			if (null != applyMilestone && InventoryConstant.Flow_Key.RES_FLOW_APPLY.equals(applyMilestone.getTaskDefineName())) {
				responseVO.setApproverName(applyMilestone.getAssignmentName());
				responseVO.setApproveResult(Func.toStr(orderStatusMap.get(responseVO.getOrderStatus())));
			}
			//审批结果
			responseVO.setOrderStatusName(Func.toStr(orderStatusMap.get(responseVO.getOrderStatus())));
			responseVOList.add(responseVO);		}
		return R.data(resApplyQueryResponseVOIPage.setRecords(responseVOList));
	}

	private List<Long> getTodoOrderList(String taskNode){
		PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
		Long  personId = personUserRel.getPersonId();

		//查询该登录人的roleid
		String roleIds = AuthUtil.getUser().getRoleId();

		//查询该登录人的岗位
		Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
		Long positionId = null;
		if (null != person) {
			positionId = person.getPersonPositionId();
		}
		log.error("roleids=={},personid==={},positionid=={}",roleIds,personId,positionId);
		List<Long> todoOrderIdList = flowTaskAllotClient.getFlowTask(personId,positionId,roleIds,taskNode).getData();
		return todoOrderIdList;
	}
	/**
	 * 物资申请详情接口
	 */
    @ApiLog(value = "查询物资申请详情接口")
	@GetMapping("/resApplyOrderDetail")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询物资申请详情接口", notes = "orderId")
	public R<ResApplyDetailVO> resApplyOrderDetail(@RequestParam(name = "orderId",required = true) Long orderId) {

		ResApplyDetailVO detailVO = resOrderService.resApplyOrderDetail(orderId);
		//申请里程碑
		LambdaQueryWrapper<ResOrderMilestone> milestoneLambdaQueryWrapper = new LambdaQueryWrapper();
		milestoneLambdaQueryWrapper.eq(ResOrderMilestone::getOrderId,orderId);
		milestoneLambdaQueryWrapper.orderByDesc(ResOrderMilestone::getUpdateTime);
		milestoneLambdaQueryWrapper.orderByDesc(ResOrderMilestone::getCreateTime);
		List<ResOrderMilestone> orderMilestoneVOList = milestoneService.list(milestoneLambdaQueryWrapper);
		if (CollectionUtil.isNotEmpty(orderMilestoneVOList) && StringUtil.isNotBlank(detailVO.getProcessInstanceId())) {
			List<ResOrderMilestoneVO> milestoneVOS = new ArrayList<>();
			for (ResOrderMilestone orderMilestone:orderMilestoneVOList) {
				ResOrderMilestoneVO vo = new ResOrderMilestoneVO();
				BeanUtil.copy(orderMilestone,vo);
				vo.setProcessInstanceId(detailVO.getProcessInstanceId());
				milestoneVOS.add(vo);
			}
			detailVO.setOrderMilestoneVOList(milestoneVOS);
		}

		//订单流程
		detailVO.setResApplyFlows(dictClient.getList(InventoryConstant.Flow_Key.RES_APPLY_FLOW).getData());
		//
		return R.data(detailVO);
	}
	/**
	 * 物资申请订单取消接口
	 */
	@ApiLog(value = "物资申请订单取消接口")
	@GetMapping("/cancelResOrder")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "物资申请订单取消接口", notes = "orderId")
	public R<ResApplyDetailVO> cancelResOrder(@RequestParam(name = "orderId",required = true) Long orderId) {

		return R.status(resOrderService.cancelResOrder(orderId));

	}

	/**
	 * 物资申请订单取消接口
	 */
	@ApiLog(value = "最新6条待处理任务")
	@GetMapping("/last6ResOrder")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "最新6条待处理任务", notes = "最新6条待处理任务")
	public R<List<ResOrder4HomeVO>> last6ResOrder() {
		String tenantId = AuthUtil.getTenantId();
		return R.data(resOrderService.lastResOrders(tenantId,AuthUtil.getUserId().toString()));

	}

	@ApiLog(value = "小程序最新5条待处理任务")
	@GetMapping("/resOrder4Mini")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "小程序最新5条待处理任务", notes = "小程序最新5条待处理任务")
	public R<List<ResOrder4MiniVO>> getResOrder4Mini(@RequestParam(name = "num",required = true) Integer num) {
		return R.data(resOrderService.lastResOrder4Mini(num));
	}


	@ApiLog(value = "小程序待处理任务总数")
	@GetMapping("/countResOrder4Mini")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "小程序待处理任务总数", notes = "小程序待处理任务总数")
	public R<Integer> countResOrder4Mini() {
		return R.data(resOrderService.countResOrder4Mini());
	}

	/**
	 * 打印出库单
	 */
    @ApiLog(value = "打印出库单")
	@GetMapping("/printApplyOrder")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "打印出库单", notes = "orderId")
	public void printApplyOrder(@RequestParam(name = "orderId",required = true) Long orderId,BladeUser user) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		JasperPrint jasperPrint;
		ResApplyDetailVO applyDetailVO = resOrderService.resApplyOrderDetail(orderId);
		if (null == applyDetailVO) {
			throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_INVENTORY_ORDER));
		}

		try {

			InputStream inputStream = this.getClass().getResourceAsStream("/jasper/ResApplyOrder.jrxml");

			List<ResOrderDtlVO> resOrderDtlVOS = applyDetailVO.getResOrderDtlVOList();
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(resOrderDtlVOS);
			Map<String,Object> params = new HashMap<>();
			if (null != user) {
				R<Tenant> tenantR = sysClient.getTenant(user.getTenantId());
				if (null != tenantR&& ResultCodeConstant.ResponseCode.SUCCESS == tenantR.getCode() && null != tenantR.getData()){
					params.put("companyName",tenantR.getData().getTenantName());
				}

				params.put("operaterName",user.getUserName());
			}

			params.put("orderId",applyDetailVO.getOrderId());
			params.put("applyName",applyDetailVO.getApplyName());
			params.put("operateTime", TimeUtil.getFormattedDate(TimeUtil.getSysDate(),TimeUtil.YYYY_MM_DD_HH_MM_SS));
			params.put("departmentName", applyDetailVO.getApplyDepartment());
			params.put("inventoryName",DictBizCache.getValue(AuthUtil.getTenantId(),InventoryConstant.StorageName.CODE,InventoryConstant.StorageName.LOCAL_STORAGE));
			params.put("totalNum",resOrderDtlVOS.size());
			jasperPrint = JasperFillManager.fillReport(JasperCompileManager.compileReport(inputStream), params, dataSource);
			response.setCharacterEncoding("UTF-8");
			response.setDateHeader("Expires", 0); // 清除页面缓存
//        response.setHeader("Content-Disposition",
//                "attachment;" + "filename=" + new String((fileName + "." + suffix).getBytes(), "ISO-8859-1"));
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition","inline;filename=storeOut.pdf");
			JRPdfExporter pdfExporter = new JRPdfExporter();
			pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
			pdfExporter.exportReport();
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
}
