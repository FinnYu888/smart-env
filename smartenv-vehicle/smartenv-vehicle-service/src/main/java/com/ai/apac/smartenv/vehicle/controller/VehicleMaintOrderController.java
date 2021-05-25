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
package com.ai.apac.smartenv.vehicle.controller;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.flow.feign.IFlowTaskAllotClient;
import com.ai.apac.smartenv.flow.feign.IFlowTaskPendingClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintMilestone;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrderMilestone;
import com.ai.apac.smartenv.vehicle.service.IVahicleMaintMilestoneService;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.ai.apac.smartenv.vehicle.service.IVehicleMaintOrderService;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintMilestoneNode;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderMilestoneVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleMaintOrderWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-08-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("vehiclemaintorder")
@Api(value = "车辆维修订单", tags = "车辆维修订单管理")
@Slf4j
public class VehicleMaintOrderController extends BladeController {

    private IVehicleMaintOrderService vehicleMaintOrderService;
    private IEntityCategoryClient categoryClient;
    private IVehicleCategoryService vehicleCategoryService;
    private IVahicleMaintMilestoneService maintMilestoneService;
    private IFlowTaskPendingClient flowTaskPendingClient;
    private IFlowTaskAllotClient flowTaskAllotClient;
    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入vehicleMaintOrder")
    public R<VehicleMaintOrderVO> detail(VehicleMaintOrder vehicleMaintOrder) {
        VehicleMaintOrder detail = vehicleMaintOrderService.getOne(Condition.getQueryWrapper(vehicleMaintOrder));
        return R.data(VehicleMaintOrderWrapper.build().entityVO(detail));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入vehicleMaintOrder")
    public R<IPage<VehicleMaintOrderVO>> list(VehicleMaintOrder vehicleMaintOrder, Query query) {
        IPage<VehicleMaintOrder> pages = vehicleMaintOrderService.page(Condition.getPage(query), Condition.getQueryWrapper(vehicleMaintOrder));
        return R.data(VehicleMaintOrderWrapper.build().pageVO(pages));
    }


    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "传入vehicleMaintOrder")
    public R<IPage<VehicleMaintOrderVO>> page(VehicleMaintOrderVO vehicleMaintOrder, Query query) {
        IPage<VehicleMaintOrderVO> pages = vehicleMaintOrderService.selectVehicleMaintOrderPage(Condition.getPage(query), vehicleMaintOrder);
        return R.data(pages);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入vehicleMaintOrder")
    public R save(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder) {
        return R.status(vehicleMaintOrderService.save(vehicleMaintOrder));
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入vehicleMaintOrder")
    public R update(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder) {
        return R.status(vehicleMaintOrderService.updateById(vehicleMaintOrder));
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入vehicleMaintOrder")
    public R submit(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder) {
        return R.status(vehicleMaintOrderService.saveOrUpdate(vehicleMaintOrder));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(vehicleMaintOrderService.deleteLogic(Func.toLongList(ids)));
    }

    /**
    * 查询车辆维修记录
    * @author 66578
    */
    @GetMapping("/queryVehicleMaintOrderRecord")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "查询车辆维修记录", notes = "查询车辆维修记录")
    public R<IPage<VehicleMaintOrderVO>> queryVehicleMaintOrderRecord(Query query,
                                                                        String status,
                                                                     String id) {
        QueryWrapper<VehicleMaintOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_Id",id);
        if (StringUtils.isNotEmpty(status)) {
         queryWrapper.in("status",Func.toIntList(status));
        }
        IPage<VehicleMaintOrder> pages = vehicleMaintOrderService.queryVehicleMaintOrderRecord(Condition.getPage(query), queryWrapper);
        IPage<VehicleMaintOrderVO> pageVO = VehicleMaintOrderWrapper.build().pageVO(pages);
        List<VehicleMaintOrderVO> maintOrderVOS = pageVO.getRecords();

        if (CollectionUtil.isNotEmpty(maintOrderVOS)) {
            Map<String,String> vehicleTypeMap = new HashMap<>();
            List<VehicleCategory> vehicleCategories = vehicleCategoryService.list();
            //List<EntityCategory> entityCategories = categoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.VEHICLE.toString()).getData();
            if (CollectionUtil.isNotEmpty(vehicleCategories)) {
                vehicleCategories.forEach(vehicleCategory -> {
                    vehicleTypeMap.put(vehicleCategory.getCategoryCode(),vehicleCategory.getCategoryName());
                });

            }

            maintOrderVOS.forEach(maintorde->{
                maintorde.setMaintTypeName(DictCache.getValue(maintorde.getApplyType(),maintorde.getMaintType()));
                maintorde.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,maintorde.getApplyType()));
                maintorde.setVehicleKindName(vehicleTypeMap.get(maintorde.getVehicleKind().toString()));
            });
        }
        return R.data(pageVO);
    }
    /**
    * 根据状态查询申请单，只查询申请单信息
    * @author 66578
    */

    @GetMapping("/queryVehicleMaintOrderStatus")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据状态查询审批单", notes = "传入状态ids")
  /*  public R<IPage<VehicleMaintOrderVO>> queryVehicleMaintOrderStatus( Query query,
                                                                      @RequestParam String status,@RequestParam(name = "查询人员personIds", required = false) String personIds,
            @RequestParam(name = "开始时间", required = false) String beginDate,@RequestParam(name = "结束时间", required = false) String endDate,
            @RequestParam String maintType, @RequestParam(name = "车辆名称", required = false) String vehicleName,
          @RequestParam(name = "车辆大类", required = false) String vehicleKind,@RequestParam(name = "车辆小类", required = false) String vehicleType
                                                                      ) {*/
    public R<IPage<VehicleMaintOrderVO>> queryVehicleMaintOrderStatus(Query query,
                  String status, String applyPersonId,
                   String startTime, String endTime,
                   String maintType, String vehicleName,
                  String vehicleKind, String vehicleType,String applyType
    ) {
        LambdaQueryWrapper<VehicleMaintOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(VehicleMaintOrder::getStatus, Func.toIntList(status));
        if (StringUtils.isEmpty(applyPersonId)){
            //查询当前用户
            PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
            if (null != personUserRel && null != personUserRel.getPersonId()) {
                applyPersonId = personUserRel.getPersonId().toString();
                queryWrapper.in(VehicleMaintOrder::getApplyPersonId, Func.toLongList(applyPersonId));
            }
        }else {
            log.error("当前操作员未找到绑定人员信息");
            return R.data(Condition.getPage(query));
        }

        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            queryWrapper.between(VehicleMaintOrder::getCreateTime,startTime,endTime);
        }
        if (StringUtils.isNotEmpty(maintType)) {
            queryWrapper.eq(VehicleMaintOrder::getMaintType,maintType);
        }
        if (StringUtils.isNotEmpty(applyType)) {
            queryWrapper.eq(VehicleMaintOrder::getApplyType,applyType);
        }
        if (StringUtils.isNotEmpty(vehicleName)) {
            queryWrapper.like(VehicleMaintOrder::getVehicleName,vehicleName);
        }
        if (StringUtils.isNotEmpty(vehicleKind)) {
            queryWrapper.eq(VehicleMaintOrder::getVehicleKind,vehicleKind);
        }
        if (StringUtils.isNotEmpty(vehicleType)) {
            queryWrapper.eq(VehicleMaintOrder::getVehicleType,vehicleType);
        }


        queryWrapper.orderByDesc(VehicleMaintOrder::getCreateTime);
        IPage<VehicleMaintOrder> vehicleMaintOrderIPage= vehicleMaintOrderService.page(Condition.getPage(query),queryWrapper);
        List<VehicleMaintOrderVO> vehicleMaintOrderVOS = null;
        IPage<VehicleMaintOrderVO> vehicleMaintOrderVOIPage = Condition.getPage(query);
        vehicleMaintOrderVOIPage.setTotal(vehicleMaintOrderIPage.getTotal());
        vehicleMaintOrderVOIPage.setSize(vehicleMaintOrderIPage.getSize());
        if (null != vehicleMaintOrderIPage && null != vehicleMaintOrderIPage.getRecords()) {
            List<VehicleMaintOrder> vehicleMaintOrders = vehicleMaintOrderIPage.getRecords();
             vehicleMaintOrderVOS = new ArrayList<>();
            Map<String,String> vehicleTypeMap = new HashMap<>();

            List<VehicleCategory> vehicleCategories = vehicleCategoryService.list();

             //List<EntityCategory> entityCategories = categoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.VEHICLE.toString()).getData();
            if (CollectionUtil.isNotEmpty(vehicleCategories)) {
                vehicleCategories.forEach(vehicleCategory -> {
                     vehicleTypeMap.put(vehicleCategory.getCategoryCode(),vehicleCategory.getCategoryName());
                 });

            }
            for (VehicleMaintOrder vehicleMaintOrder : vehicleMaintOrders) {
                VehicleMaintOrderVO vehicleMaintOrderVO = new VehicleMaintOrderVO();
                BeanUtil.copyProperties(vehicleMaintOrder,vehicleMaintOrderVO);
                vehicleMaintOrderVO.setMaintTypeName(DictCache.getValue(vehicleMaintOrder.getApplyType(),vehicleMaintOrder.getMaintType()));
                vehicleMaintOrderVO.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,vehicleMaintOrder.getApplyType()));
                vehicleMaintOrderVO.setVehicleKindName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleKind().toString()));
                vehicleMaintOrderVO.setVehicleTypeName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleType().toString()));

                vehicleMaintOrderVOS.add(vehicleMaintOrderVO);
            }
            vehicleMaintOrderVOIPage.setRecords(vehicleMaintOrderVOS);
        }

        return R.data(vehicleMaintOrderVOIPage);
    }
    /**
     * 根据订单id查询订单详情，包含审批记录
     * @author 66578
     */
    @GetMapping("/queryVehicleMaintOrderDetail")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "根据订单id查询订单详情，包含审批记录", notes = "传入订单id")
    public R<VehicleMaintOrderMilestoneVO> queryVehicleMaintOrderDetail(@ApiParam(value = "订单号", required = true)@RequestParam("orderId") Long orderId) {

        VehicleMaintOrderMilestone orderMileStone = vehicleMaintOrderService.queryVehicleMaintOrderMilestone(orderId);
        if (null == orderMileStone) return null;
        VehicleMaintOrderMilestoneVO orderMilestoneVO = new VehicleMaintOrderMilestoneVO();

        BeanUtil.copyProperties(orderMileStone,orderMilestoneVO);
        orderMilestoneVO.setMaintTypeName(DictCache.getValue(orderMileStone.getApplyType(),orderMileStone.getMaintType()));
        orderMilestoneVO.setVehicleKindName(categoryClient.getCategoryName(orderMileStone.getVehicleKind()).getData());
        orderMilestoneVO.setVehicleTypeName(categoryClient.getCategoryName(orderMileStone.getVehicleType()).getData());
        orderMilestoneVO.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,orderMileStone.getApplyType()));
        orderMilestoneVO.setApplyPersonDeptName(DeptCache.getDeptName(orderMileStone.getApplyPersonDept()));
        return R.data(orderMilestoneVO);
    }
    /**
    * 查询申请人订单列表
    * @author 66578
    */
    @PostMapping("/queryVehicleMaintOrderList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询申请人订单列表", notes = "传入订单id")
    public R<IPage<VehicleMaintOrderMilestoneVO>> queryVehicleMaintOrderList(Query query, Long personId,
                                                                    String orderStatus) {
        if (null == personId){
            PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
            personId = personUserRel.getPersonId();
        }

        QueryWrapper<VehicleMaintOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.apply_person_id",personId);
        if (StringUtil.isNotBlank(orderStatus)) {
            queryWrapper.in("a.status",Func.toIntList(orderStatus));
        }

        queryWrapper.orderByDesc("a.create_time");
        IPage<VehicleMaintOrder> orderMileStone = vehicleMaintOrderService.queryVehicleMaintOrderPage(Condition.getPage(query),queryWrapper);
        IPage<VehicleMaintOrderMilestoneVO> orderPage = Condition.getPage(query);
        if (null == orderMileStone || CollectionUtil.isNotEmpty(orderMileStone.getRecords())) {
            return R.data(orderPage);
        }
        orderPage.setTotal(orderMileStone.getTotal());
        orderPage.setPages(orderMileStone.getPages());
        List<VehicleMaintOrderMilestoneVO> milestoneVOS = new ArrayList<>();
        for (VehicleMaintOrder milestone:orderMileStone.getRecords()) {
            VehicleMaintOrderMilestoneVO maintOrderMilestoneVO = new VehicleMaintOrderMilestoneVO();
            BeanUtil.copyProperties(milestone,maintOrderMilestoneVO);
            maintOrderMilestoneVO.setMaintTypeName(DictCache.getValue(milestone.getApplyType(),milestone.getMaintType()));
            maintOrderMilestoneVO.setVehicleKindName(categoryClient.getCategoryName(milestone.getVehicleKind()).getData());
            maintOrderMilestoneVO.setVehicleTypeName(categoryClient.getCategoryName(milestone.getVehicleType()).getData());
            maintOrderMilestoneVO.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,milestone.getApplyType()));
            milestoneVOS.add(maintOrderMilestoneVO);
        }
        orderPage.setRecords(milestoneVOS);
        return R.data(orderPage);
    }

    /**
     * 查询该用户下待审批单子
     * @author 66578
     */
    @GetMapping("/queryVehicleMaintOrderApproveList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询该用户下待审批单子", notes = "查询该用户下待审批单子")
/*    public R<IPage<VehicleMaintOrderMilestoneVO>> queryVehicleMaintOrderApproveList(Query query,@ApiParam(value = "审批人Id", required = false)@RequestParam("personId") Long personId,
                                                                           @ApiParam(value = "订单状态，多个用逗号隔开", required = false)@RequestParam("orderStatus") String orderStatus,
     @ApiParam(value = "处理状态,未处理-1，已完成不，可以不传", required = false)@RequestParam("doneStatus") Integer doneStatus) {*/
    public R<IPage<VehicleMaintOrderMilestoneVO>> queryVehicleMaintOrderApproveList(Query query, String applyPersonId,  String orderStatus,Integer doneStatus,String startTime,String endTime,
                                                                                    String applyType,String maintType,String applyPersonName,String vehicleKind,String vehicleType,String vehicleName) {
       //查找当前登录人关联的personId


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

        List<Long> orderIdList = flowTaskAllotClient.getFlowTask(personId,positionId,roleIds,null).getData();
        if (CollectionUtil.isEmpty(orderIdList)) {
            return R.data(Condition.getPage(query));
        }
        QueryWrapper<VehicleMaintOrderMilestone> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("a.id",orderIdList);
        if (StringUtil.isNotBlank(orderStatus)) {
            queryWrapper.in("a.status",Func.toIntList(orderStatus));
        }
        if (StringUtils.isNotEmpty(startTime)){
            queryWrapper.ge("a.create_time",startTime);
        }
        if (StringUtils.isNotEmpty(endTime)){
            queryWrapper.le("a.create_time",endTime);
        }
        if (StringUtils.isNotEmpty(applyType)) {
            queryWrapper.eq("a.apply_type",applyType);
        }
        if (StringUtils.isNotEmpty(maintType)) {
            queryWrapper.eq("a.maint_type",maintType);
        }
        if (StringUtils.isNotEmpty(vehicleName)) {
            queryWrapper.like("a.vehicle_name",vehicleName);
        }
        if (StringUtils.isNotEmpty(applyPersonName)) {
            queryWrapper.like("a.apply_person_name",applyPersonName);
        }
        if (StringUtils.isNotEmpty(vehicleKind)) {
            queryWrapper.eq("a.vehicle_kind",vehicleKind);
        }
        if (StringUtils.isNotEmpty(vehicleType)) {
            queryWrapper.eq("a.vehicle_type",vehicleType);
        }
        if (StringUtils.isNotEmpty(applyPersonId)) {
            queryWrapper.eq("a.apply_person_id",applyPersonId);
        }
        if (VehicleConstant.VehicleMaintanceStatus.TODO == doneStatus) {
            queryWrapper.eq("b.done_result",doneStatus);
        }else {
            queryWrapper.ne("b.done_result",VehicleConstant.VehicleMaintanceStatus.TODO);
        }



        queryWrapper.orderByDesc("a.create_time");
        IPage<VehicleMaintOrder> orderMileStonePage = vehicleMaintOrderService.queryVehicleMaintOrderApprovePage(query,queryWrapper);
        List<VehicleMaintOrder> orderMilestones =  orderMileStonePage.getRecords();
        IPage<VehicleMaintOrderMilestoneVO> orderMilestoneVOIPage = Condition.getPage(query);
        orderMilestoneVOIPage.setTotal(orderMileStonePage.getTotal());
        orderMilestoneVOIPage.setSize(orderMileStonePage.getSize());
        if (CollectionUtil.isNotEmpty(orderMilestones)) {

            Map<String,String> vehicleTypeMap = new HashMap<>();
            List<VehicleCategory> vehicleCategories = vehicleCategoryService.list();
            //List<EntityCategory> entityCategories = categoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.VEHICLE.toString()).getData();
            if (CollectionUtil.isNotEmpty(vehicleCategories)) {
                vehicleCategories.forEach(vehicleCategory -> {
                    vehicleTypeMap.put(vehicleCategory.getCategoryCode(),vehicleCategory.getCategoryName());
                });

            }
            List<VehicleMaintOrderMilestoneVO> vehicleMaintOrderVOS= new ArrayList<>();
            for (VehicleMaintOrder vehicleMaintOrder : orderMilestones) {
                VehicleMaintOrderMilestoneVO vehicleMaintOrderVO = new VehicleMaintOrderMilestoneVO();
                BeanUtil.copyProperties(vehicleMaintOrder,vehicleMaintOrderVO);
                vehicleMaintOrderVO.setMaintTypeName(DictCache.getValue(vehicleMaintOrder.getApplyType(),vehicleMaintOrder.getMaintType()));

                vehicleMaintOrderVO.setVehicleKindName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleKind().toString()));
                vehicleMaintOrderVO.setVehicleTypeName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleType().toString()));
                vehicleMaintOrderVO.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,vehicleMaintOrder.getApplyType()));
                vehicleMaintOrderVOS.add(vehicleMaintOrderVO);
            }
            orderMilestoneVOIPage.setRecords(vehicleMaintOrderVOS);
        }

        return R.data(orderMilestoneVOIPage);
    }
    /**
     * 查询该用户已经审批的单子
     * @author 66578
     */
    @GetMapping("/queryVehicleMaintOrderFinishApproveList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询该用户已经审批的单子", notes = "查询该用户已经审批的单子")
    public R<IPage<VehicleMaintOrderMilestoneVO>> queryVehicleMaintOrderFinishApproveList(Query query,   String startTime,String endTime,String applyPersonId,
                                                                                          String applyType,String maintType,String applyPersonName,String vehicleKind,String vehicleType,String vehicleName) {
        //查找当前登录人关联的personId

            PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
        Long personId = personUserRel.getPersonId();

        QueryWrapper<VehicleMaintOrderMilestone> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(startTime)){
            queryWrapper.ge("a.create_time",startTime);
        }

        if (StringUtils.isNotEmpty(endTime)){
            queryWrapper.le("a.create_time",endTime);
        }
        if (StringUtils.isNotEmpty(applyType)) {
            queryWrapper.eq("a.apply_type",applyType);
        }
        if (StringUtils.isNotEmpty(maintType)) {
            queryWrapper.eq("a.maint_type",maintType);
        }
        if (StringUtils.isNotEmpty(vehicleName)) {
            queryWrapper.like("a.vehicle_name",vehicleName);
        }
        if (StringUtils.isNotEmpty(applyPersonId)) {
            queryWrapper.eq("a.apply_person_id",applyPersonId);
        }
        if (StringUtils.isNotEmpty(applyPersonName)) {
            queryWrapper.like("a.apply_person_name",applyPersonName);
        }
        if (StringUtils.isNotEmpty(vehicleKind)) {
            queryWrapper.eq("a.vehicle_kind",vehicleKind);
        }
        if (StringUtils.isNotEmpty(vehicleType)) {
            queryWrapper.eq("a.vehicle_type",vehicleType);
        }
        queryWrapper.eq("b.assignment_id",personId);
        String doneStatus = VehicleConstant.VehicleMaintanceStatus.APPROVE_ONE+","+ VehicleConstant.VehicleMaintanceStatus.APPROVE_TWO;
        queryWrapper.in("b.done_result",Func.toIntList(doneStatus));
        String orderStatus = doneStatus+ ","+VehicleConstant.VehicleMaintanceStatus.BUDGET;
        queryWrapper.in("a.status",Func.toIntList(orderStatus));
        queryWrapper.orderByDesc("a.create_time");

        IPage<VehicleMaintOrder> orderMileStonePage = vehicleMaintOrderService.queryVehicleMaintOrderApprovePage(query,queryWrapper);
        List<VehicleMaintOrder> orderMilestones =  orderMileStonePage.getRecords();
        IPage<VehicleMaintOrderMilestoneVO> orderMilestoneVOIPage = Condition.getPage(query);
        orderMilestoneVOIPage.setTotal(orderMileStonePage.getTotal());
        orderMilestoneVOIPage.setSize(orderMileStonePage.getSize());
        if (CollectionUtil.isNotEmpty(orderMilestones)) {

            Map<String,String> vehicleTypeMap = new HashMap<>();
            List<VehicleCategory> vehicleCategories = vehicleCategoryService.list();
            //List<EntityCategory> entityCategories = categoryClient.getCategoryByType(CommonConstant.ENTITY_TYPE.VEHICLE.toString()).getData();
            if (CollectionUtil.isNotEmpty(vehicleCategories)) {
                vehicleCategories.forEach(vehicleCategory -> {
                    vehicleTypeMap.put(vehicleCategory.getCategoryCode(),vehicleCategory.getCategoryName());
                });

            }
            List<VehicleMaintOrderMilestoneVO> vehicleMaintOrderVOS= new ArrayList<>();
            for (VehicleMaintOrder vehicleMaintOrder : orderMilestones) {
                VehicleMaintOrderMilestoneVO vehicleMaintOrderVO = new VehicleMaintOrderMilestoneVO();
                BeanUtil.copyProperties(vehicleMaintOrder,vehicleMaintOrderVO);
                vehicleMaintOrderVO.setMaintTypeName(DictCache.getValue(vehicleMaintOrder.getApplyType(),vehicleMaintOrder.getMaintType()));
                vehicleMaintOrderVO.setApplyTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE,vehicleMaintOrder.getApplyType()));
                vehicleMaintOrderVO.setVehicleKindName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleKind().toString()));
                vehicleMaintOrderVO.setVehicleTypeName(vehicleTypeMap.get(vehicleMaintOrder.getVehicleType().toString()));

                vehicleMaintOrderVOS.add(vehicleMaintOrderVO);
            }
            orderMilestoneVOIPage.setRecords(vehicleMaintOrderVOS);
        }

        return R.data(orderMilestoneVOIPage);
    }
    /**
     * 车辆档案-维修记录
     * @return
     */
    @GetMapping("/vehicleFiles/maintOrder/page")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "车辆档案-维修记录", notes = "传入vehicleMaintOrder")
    @ApiLog(value = "查询车辆档案-维修记录")
	public R<IPage<VehicleMaintOrderVO>> pageMaintOrder(VehicleMaintOrder vehicleMaintOrder, Query query) {
		// 维修记录
		QueryWrapper<VehicleMaintOrder> maintOrderQueryWrapper = new QueryWrapper<>();
		maintOrderQueryWrapper.lambda().eq(VehicleMaintOrder::getVehicleId, vehicleMaintOrder.getVehicleId());
		maintOrderQueryWrapper.orderByDesc("maint_date");
		IPage<VehicleMaintOrder> page = vehicleMaintOrderService.page(Condition.getPage(query), maintOrderQueryWrapper);
		IPage<VehicleMaintOrderVO> pageVO = VehicleMaintOrderWrapper.build().pageVO(page);
		List<VehicleMaintOrderVO> records = pageVO.getRecords();
		if (records != null) {
			for (VehicleMaintOrderVO record : records) {
				record.setMaintTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE, record.getMaintType()));
			}
			pageVO.setRecords(records);
		}
		return R.data(pageVO);
	}
	/**
	* 查询该用户有没有处理订单该节点权限
	* @author 66578
	*/
    @GetMapping("/getTaskDonePermission")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询该用户有没有处理订单该节点权限", notes = "查询该用户有没有处理订单该节点权限")
    public R<Boolean> getTaskDonePermission(@ApiParam(value = "订单id", required = true)@RequestParam("orderId") Long orderId,
                                            @ApiParam(value = "当前订单节点", required = true)@RequestParam("taskNode") String taskNode) {
        PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
        Long personId = personUserRel.getPersonId();
        //查询该登录人的roleid
        String roleIds = AuthUtil.getUser().getRoleId();
        //查询该登录人的岗位
        Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
        Long positionId = null;
            if (null != person) {
            positionId = person.getPersonPositionId();
        }
        return flowTaskAllotClient.getTaskDonePermission(orderId,taskNode,personId,positionId,roleIds);
    }
    /**
     * 车辆档案-维修记录
     * @return
     */
    @GetMapping("/vehicleFiles/maintOrder/detail")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "车辆档案-维修记录详情", notes = "传入vehicleMaintOrder")
    @ApiLog(value = "查询车辆档案-维修记录")
    public R<VehicleMaintOrderVO> detailMaintOrder(VehicleMaintOrder vehicleMaintOrder, BladeUser user) {
    	// 维保信息
    	VehicleMaintOrder detail = vehicleMaintOrderService.getOne(Condition.getQueryWrapper(vehicleMaintOrder));
    	VehicleMaintOrderVO maintOrderVO = VehicleMaintOrderWrapper.build().entityVO(detail);
    	maintOrderVO.setMaintTypeName(DictCache.getValue(VehicleConstant.Maint_Type.CODE, maintOrderVO.getMaintType()));
    	
    	// 进度信息
    	QueryWrapper<VehicleMaintMilestone> maintMilestoneQueryWrapper = new QueryWrapper<>();
    	maintMilestoneQueryWrapper.lambda().eq(VehicleMaintMilestone::getOrderId, vehicleMaintOrder.getId());
    	maintMilestoneQueryWrapper.lambda().eq(VehicleMaintMilestone::getIsDeleted, 0);
    	maintMilestoneQueryWrapper.orderByDesc("create_time");
		List<VehicleMaintMilestone> maintMilestoneList = maintMilestoneService.list(maintMilestoneQueryWrapper);
		
		List<VehicleMaintMilestoneNode> nodeList = new ArrayList<>();
		VehicleMaintMilestoneNode todoNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.TODO);
		todoNode.setNodeTime(detail.getCreateTime());
		Person person = PersonCache.getPersonById(user.getTenantId(), detail.getApplyPersonId());
		todoNode.setPersonName(person.getPersonName() + "(" + person.getJobNumber() + ")");
		nodeList.add(todoNode);
		VehicleMaintMilestoneNode approveOneNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.APPROVE_ONE);
		approveOneNode.setNodeName("维保审批");
		approveOneNode.setMilestoneName("审批通过车辆维保申请");
		nodeList.add(approveOneNode);
		VehicleMaintMilestoneNode budgetNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.BUDGET);
		budgetNode.setNodeName("创建预算");
		budgetNode.setMilestoneName("创建车辆维保费用预算申请");
		nodeList.add(budgetNode);
		VehicleMaintMilestoneNode approveTwoNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.APPROVE_TWO);
		approveTwoNode.setNodeName("费用审批");
		approveTwoNode.setMilestoneName("审批通过车辆维保费用预算申请");
		nodeList.add(approveTwoNode);
		VehicleMaintMilestoneNode finishNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.FINISH);
		finishNode.setNodeName("完成");
		finishNode.setMilestoneName("完成车辆维保申请");
		nodeList.add(finishNode);
		VehicleMaintMilestoneNode refuseNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.REFUSE);
		if (refuseNode.getStatus() == 1) {
			refuseNode.setNodeName("拒绝");
			refuseNode.setMilestoneName("拒绝车辆维保申请");
			nodeList.add(refuseNode);
		}
		VehicleMaintMilestoneNode cancleNode = getVehicleMaintMilestoneNode(maintMilestoneList, VehicleConstant.VehicleMaintanceStatus.CANCLE);
		if (cancleNode.getStatus() == 1) {
			cancleNode.setNodeName("取消");
			cancleNode.setMilestoneName("取消车辆维保申请");
			nodeList.add(cancleNode);
		}
		if (refuseNode.getStatus() == 1 || cancleNode.getStatus() == 1) {
			// 有拒绝或取消，移除状态为0
			Iterator<VehicleMaintMilestoneNode> iterator = nodeList.iterator();
			while (iterator.hasNext()) {
				VehicleMaintMilestoneNode next = iterator.next();
				if (next.getStatus() == 0) {
					iterator.remove();
				}
			}
		}
		/*order-status = 1     -1
		order-status = 2	 -1,2
		order-status = 3	 -1,3,2
		order-status = 4	 -1,4,3,2
		order-status = 5	 5,4,3,2*/
		// 创建车辆维保申请，待审批，-1，    	1
		// 审批一通过，2，-1					2
		// 填预算，2，3，-1					3
		// 审批二通过，2，3，4，-1			4
		// 完成，2，3，4，5					5
		maintOrderVO.setMaintMilestoneNodes(nodeList);
		return R.data(maintOrderVO);
    	
    }

	private VehicleMaintMilestoneNode getVehicleMaintMilestoneNode(List<VehicleMaintMilestone> maintMilestoneList, Integer doneResult) {
		VehicleMaintMilestoneNode node = new VehicleMaintMilestoneNode();
		if (doneResult == VehicleConstant.VehicleMaintanceStatus.TODO) {
			node.setNodeName("创建申请");
			node.setMilestoneName("创建车辆维保申请");
			node.setStatus(1);
			return node;
		}
		node.setStatus(0);
		for (VehicleMaintMilestone vehicleMaintMilestone : maintMilestoneList) {
			if (doneResult == vehicleMaintMilestone.getDoneResult()
					&& (doneResult == VehicleConstant.VehicleMaintanceStatus.APPROVE_ONE
							|| doneResult == VehicleConstant.VehicleMaintanceStatus.APPROVE_TWO)) {
				node.setResult("通过");
				node.setRemark(vehicleMaintMilestone.getDoneRemark());
				node.setStatus(1);
			} else if (doneResult == vehicleMaintMilestone.getDoneResult()
					&& (doneResult == VehicleConstant.VehicleMaintanceStatus.BUDGET
							|| doneResult == VehicleConstant.VehicleMaintanceStatus.FINISH
							|| doneResult == VehicleConstant.VehicleMaintanceStatus.CANCLE)) {
				node.setStatus(1);
			} else if (doneResult == vehicleMaintMilestone.getDoneResult() && doneResult == VehicleConstant.VehicleMaintanceStatus.REFUSE) {
				node.setResult("拒绝");
				node.setRemark(vehicleMaintMilestone.getDoneRemark());
				node.setStatus(1);
			} else if (doneResult == vehicleMaintMilestone.getDoneResult()) {
				/*node.setNodeName("创建申请");
				node.setMilestoneName("创建车辆维保申请");*/
			}
			if (node.getStatus() == 1) {
				node.setNodeTime(vehicleMaintMilestone.getUpdateTime());
				PersonUserRel personUser = PersonUserRelCache.getRelByUserId(vehicleMaintMilestone.getUpdateUser());
				if (personUser != null && personUser.getPersonId() != null) {
					Person person = PersonCache.getPersonById(AuthUtil.getTenantId(), personUser.getPersonId());
					if (person != null && person.getId() != null) {
						node.setPersonName(person.getPersonName() + "(" + person.getJobNumber() + ")");
					}
				}
				break;
			}
		}
		return node;
	}
}
