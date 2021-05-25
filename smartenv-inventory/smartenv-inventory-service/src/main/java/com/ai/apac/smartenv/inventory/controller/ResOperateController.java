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


import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResOperateQuery;
import com.ai.apac.smartenv.inventory.service.IResInfoService;
import com.ai.apac.smartenv.inventory.vo.ResOperateQueryVO;
import com.ai.apac.smartenv.inventory.wrapper.ResOperateQueryWrapper;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.DictBiz;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.smartenv.cache.util.SmartCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.vo.ResOperateVO;
import com.ai.apac.smartenv.inventory.wrapper.ResOperateWrapper;
import com.ai.apac.smartenv.inventory.service.IResOperateService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-27
 */
@RestController
@AllArgsConstructor
@RequestMapping("resoperate")
@Api(value = "资源操作记录接口", tags = "资源操作记录接口")
public class ResOperateController extends BladeController {

	private IResOperateService resOperateService;
	private IDictBizClient dictBizClient;
	private IResInfoService resInfoService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resOperate")
	public R<ResOperateVO> detail(ResOperate resOperate) {
		ResOperate detail = resOperateService.getOne(Condition.getQueryWrapper(resOperate));
		return R.data(ResOperateWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入resOperate")
	public R<IPage<ResOperateVO>> list(ResOperate resOperate, Query query) {
		IPage<ResOperate> pages = resOperateService.page(Condition.getPage(query), Condition.getQueryWrapper(resOperate));
		return R.data(ResOperateWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入resOperate")
	public R<IPage<ResOperateVO>> page(ResOperateVO resOperate, Query query) {
		IPage<ResOperateVO> pages = resOperateService.selectResOperatePage(Condition.getPage(query), resOperate);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resOperate")
	public R save(@Valid @RequestBody ResOperate resOperate) {
		return R.status(resOperateService.save(resOperate));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resOperate")
	public R update(@Valid @RequestBody ResOperateVO resOperate)throws ServiceException {
		//校验修改数量
		Integer newAmont = resOperate.getAmount();//新入库数量
		ResOperate oldResOperate = resOperateService.getById(resOperate.getId());
		if (null == oldResOperate) {
			throw new ServiceException("入库记录不存在");

		}
		Integer oldAmount = oldResOperate.getAmount();
		Long oldInventoryId = oldResOperate.getInventoryId();
		Long newInventoryId = resOperate.getInventoryId();
		//查询当前物资库存数量
		LambdaQueryWrapper<ResInfo> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(ResInfo::getResType,resOperate.getResType());
		queryWrapper.eq(ResInfo::getResSpecId,resOperate.getResSpec());
		queryWrapper.eq(ResInfo::getInventoryId,oldInventoryId);
		ResInfo resInfo = resInfoService.getOne(queryWrapper);
		if (null == resInfo) {
			throw new ServiceException("没找到对应的物资库存记录");
		}
		Integer inventoryAmount = resInfo.getAmount();
		Integer newInventoryAmount = 0;
		ResInfo newResInfo = null;
		if (newInventoryId.equals(oldInventoryId) ) {// 原仓库只需要修改库存数量
			if (newAmont == oldAmount) {
				resInfo =null;// 数量不变，不需要更新库存信息
			}else {
				newInventoryAmount = inventoryAmount - (oldAmount-newAmont);
				//当前库存数量-（当前记录入库数量-当前记录修改后的入库数量）>=0
				if (newInventoryAmount < 0) {
					throw new ServiceException("该数量不能使当前库存数量小于0，当前库存数量:"+inventoryAmount);
				}
				resInfo.setAmount(newInventoryAmount);
			}

		}else {
			if (inventoryAmount-oldAmount<0) {
				throw new ServiceException("修改了仓库后，会使原仓库库存数量小于0，该物资在原库存数量:"+inventoryAmount);
			}
			resInfo.setAmount(inventoryAmount-oldAmount);
			queryWrapper = new LambdaQueryWrapper();
			queryWrapper.eq(ResInfo::getResType,resOperate.getResType());
			queryWrapper.eq(ResInfo::getResSpecId,resOperate.getResSpec());
			queryWrapper.eq(ResInfo::getInventoryId,newInventoryAmount);
			newResInfo = resInfoService.getOne(queryWrapper);
			if (null == newResInfo) {
				;
			}else {
				newResInfo.setAmount(newResInfo.getAmount()+newAmont);
			}
		}

		//拼装工号信息
		if (StringUtils.isNotEmpty(resOperate.getPurchasingAgentId())) {
			resOperate.setPurchasingAgent(resOperate.getPurchasingAgent()+"("+resOperate.getPurchasingAgentId()+")");
		}
		return R.status(resInfoService.updateResOperate(resOperate,resInfo,newResInfo));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入resOperate")
	public R submit(@Valid @RequestBody ResOperate resOperate)throws Exception {

		return R.status(resOperateService.saveOrUpdate(resOperate));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids)throws Exception {
		//校验修改数量

		ResOperate resOperate = resOperateService.getById(Func.toLong(ids));
		if (null == resOperate) {
			throw new ServiceException("入库记录不存在");
		}
		Integer recodeAmount = resOperate.getAmount();
		//查询当前物资库存数量
		LambdaQueryWrapper<ResInfo> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(ResInfo::getResType,resOperate.getResType());
		queryWrapper.eq(ResInfo::getResSpecId,resOperate.getResSpec());
		queryWrapper.eq(ResInfo::getInventoryId,resOperate.getInventoryId());
		ResInfo resInfo = resInfoService.getOne(queryWrapper);
		if (null == resInfo) {
			throw new ServiceException("没找到对应的物资库存记录");
		}
		Integer inventoryAmount = resInfo.getAmount();
		Integer newInventoryAmount = inventoryAmount - recodeAmount;
		//当前库存数量-（当前记录入库数量-当前记录修改后的入库数量）>=0
		if (newInventoryAmount < 0) {
			throw new ServiceException("删除该入库记录会使当前该物资库存数量小于0，当前库存可用数量:"+inventoryAmount);
		}
		resInfo.setAmount(newInventoryAmount);
		return R.status(resInfoService.deleteLogicResOperate(resOperate,resInfo));
	}
	/**
	 * 查询资料操作记录
	 */
	@ApiLog(value = "查询资料操作记录")
	@PostMapping("/listResOperatorPage")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "查询资料操作记录", notes = "传入ResOperateVO")
	public R<IPage<ResOperateQueryVO>> listResOperatorPage(@RequestBody ResOperateVO resOperate, Query query, BladeUser user) {

		QueryWrapper<ResOperateVO> queryWrapper = Condition.getQueryWrapper(resOperate);
		if (Func.isNotEmpty(resOperate.getResType())) {
			queryWrapper.eq("oper.res_type",resOperate.getResType());
		}
		if (Func.isNotEmpty(resOperate.getResourceId())) {
			queryWrapper.eq("oper.resource_id",resOperate.getResourceId());
		}
		if (Func.isNotEmpty(resOperate.getResSpec())) {
			queryWrapper.eq("oper.res_spec",resOperate.getResSpec());
		}
		if (StringUtil.isNotBlank(resOperate.getPurchasingAgent())) {
			queryWrapper.like("oper.purchasing_agent",resOperate.getPurchasingAgent());
		}
		if (StringUtil.isNotBlank(resOperate.getOperateType())) {
			queryWrapper.eq("oper.operate_type",resOperate.getOperateType());
		}
		queryWrapper.gt("oper.amount",0);
		if (null != user) queryWrapper.eq("oper.tenant_id",user.getTenantId());
		//入库时间
		if (null != resOperate.getStartDate()) queryWrapper.ge("oper.create_time",resOperate.getStartDate());
		if (null != resOperate.getEndDate()) queryWrapper.le("oper.create_time",resOperate.getEndDate());

		queryWrapper.orderByDesc("oper.create_time");
        IPage<ResOperateQuery> pages = resOperateService.listResOperatorPage(Condition.getPage(query), queryWrapper);
        List<ResOperateQuery> list = pages.getRecords();
        if (null != list && list.size()>0) {
			Map<String,String> inventoryNameMap = new HashMap<>();
			List<DictBiz> listStoreageName = dictBizClient.getTenantCodeDict(AuthUtil.getTenantId(),InventoryConstant.StorageName.CODE).getData();
			if (CollectionUtil.isNotEmpty(listStoreageName)) {
				listStoreageName.forEach(storeageName->{
					inventoryNameMap.put(storeageName.getDictKey(),storeageName.getDictValue());
				});

			}

        	list.forEach(resOperateQuery -> {
        		resOperateQuery.setInventoryName(inventoryNameMap.get(resOperateQuery.getInventoryId().toString()));
        		resOperateQuery.setResourceSourceName(DictCache.getValue(InventoryConstant.ResourceSource.CODE,resOperateQuery.getResourceSource()));
        		resOperateQuery.setOperateTypeName(DictCache.getValue(InventoryConstant.ResBusinessType.CODE,resOperateQuery.getOperateType()));
			});
		}
		return R.data(ResOperateQueryWrapper.build().pageVO(pages));
	}

}
