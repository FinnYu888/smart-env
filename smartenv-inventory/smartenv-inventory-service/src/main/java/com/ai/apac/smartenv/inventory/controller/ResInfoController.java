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
import com.ai.apac.smartenv.inventory.entity.ResInfo;
import com.ai.apac.smartenv.inventory.entity.ResInfoQuery;
import com.ai.apac.smartenv.inventory.service.IResInfoService;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageExcelResultModelVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoPutInStorageVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResInfoVO;
import com.ai.apac.smartenv.inventory.wrapper.ResInfoWrapper;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.DictBiz;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
@RestController
@AllArgsConstructor
@RequestMapping("resinfo")
@Api(value = "物资管理接口", tags = "物资管理接口")
public class ResInfoController extends BladeController {

	private IResInfoService resInfoService;
	private IDictBizClient dictBizClient;
	private IDictClient dictClient;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resInfo")
	public R<ResInfoVO> detail(ResInfo resInfo) {
		ResInfo detail = resInfoService.getOne(Condition.getQueryWrapper(resInfo));
		return R.data(ResInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入resInfo")
	public R<IPage<ResInfoVO>> list(ResInfo resInfo, Query query) {
		IPage<ResInfo> pages = resInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(resInfo));
		return R.data(ResInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入resInfo")
	public R<IPage<ResInfoVO>> page(ResInfoVO resInfo, Query query) {
		IPage<ResInfoVO> pages = resInfoService.selectResInfoPage(Condition.getPage(query), resInfo);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resInfo")
	public R save(@Valid @RequestBody ResInfo resInfo) {
		return R.status(resInfoService.save(resInfo));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resInfo")
	public R update(@Valid @RequestBody ResInfo resInfo) {
		return R.status(resInfoService.updateById(resInfo));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入resInfo")
	public R submit(@Valid @RequestBody ResInfo resInfo) {
		return R.status(resInfoService.saveOrUpdate(resInfo));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(resInfoService.deleteLogic(Func.toLongList(ids)));
	}
	/**
	*物资入库
	*/
	@ApiLog(value = "物资入库")
	@PostMapping("/putInStorage")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "物资入库", notes = "传入ResInfoPutInStorageVO")
	public R putInStorage(@RequestBody ResInfoPutInStorageVO putInStorageVO,BladeUser user) {

		resInfoService.putInStorage(putInStorageVO);
		return R.status(true);
	}
	/**
	*获取库存物资列表
	*/
	@ApiLog(value = "获取库存物资列表")
	@GetMapping("/getResInfolist")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "获取库存物资列表", notes = "传入resInfo")
	public R<IPage<ResInfoQueryVO>> getResInfolist(ResInfoQueryVO resInfoQuery, Query query,BladeUser user) {
		IPage<ResInfoQueryVO> returnResInfoPage =Condition.getPage(query);
		QueryWrapper<ResInfoQueryVO> queryWrapper = Condition.getQueryWrapper(resInfoQuery);
		if (Func.isNotEmpty(resInfoQuery.getInventoryId())) {
			queryWrapper.eq("info.inventory_id",resInfoQuery.getInventoryId());
		}
		if (Func.isNotEmpty(resInfoQuery.getResType())) {
			queryWrapper.eq("info.res_type",resInfoQuery.getResType());
		}
		if (Func.isNotEmpty(resInfoQuery.getResSpecId())) {
			queryWrapper.eq("info.res_spec_id",resInfoQuery.getResSpecId());
		}

		queryWrapper.gt("info.amount",0);


		if (null != user && StringUtil.isNotBlank(user.getTenantId())) queryWrapper.eq("info.tenant_id",user.getTenantId());

		IPage<ResInfoQuery> pages = resInfoService.selectResInfoQueryPage(Condition.getPage(query), queryWrapper);
		List<ResInfoQuery> resInfoQuerys = pages.getRecords();
		if (CollectionUtil.isNotEmpty(resInfoQuerys)) {
			Map<String,String> inventoryNameMap = new HashMap<>();
			List<DictBiz> listStoreageName = dictBizClient.getTenantCodeDict(AuthUtil.getTenantId(),InventoryConstant.StorageName.CODE).getData();
			if (CollectionUtil.isNotEmpty(listStoreageName)) {
				listStoreageName.forEach(storeageName->{
					inventoryNameMap.put(storeageName.getDictKey(),storeageName.getDictValue());
				});

			}
			Map<String,Object> manageStatusNameMap = dictClient.getMap(InventoryConstant.ResManageStatus.CODE).getData();
			//Map<String,Object> operateTypeMap = dictClient.getMap(InventoryConstant.ResManageStatus.CODE).getData();
			List<ResInfoQueryVO> queryVOList = new ArrayList<>();
			for (ResInfoQuery resInfoQueryVO:resInfoQuerys){
				ResInfoQueryVO queryVO = new ResInfoQueryVO();
				BeanUtil.copy(resInfoQueryVO,queryVO);
				queryVO.setInventoryName(inventoryNameMap.get(resInfoQueryVO.getInventoryId().toString()));
				Object manageStatus = manageStatusNameMap.get(resInfoQuery.getManageState());
				queryVO.setManageStateName(null != manageStatus?manageStatus.toString():null);
				queryVOList.add(queryVO);
			}
			returnResInfoPage.setRecords(queryVOList);
			returnResInfoPage.setTotal(pages.getTotal());
		}
		return R.data(returnResInfoPage);
	}

	/**
	 *修改库存数量
	 */
	@ApiLog(value = "修改库存数量")
	@PostMapping("/modifyStorageNumber")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "修改库存数量", notes = "传入resInfoVo")
	public R modifyStorageNumber(@RequestBody ResInfoVO resInfoVO, BladeUser user) {
		return R.status(resInfoService.modifyStorageNumber(resInfoVO,user));
	}
	/**
	 *获取库存列表
	 */
	@ApiLog(value = "获取库存列表")
	@PostMapping("/getInventoryList")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "获取库存列表", notes = "传入resInfoVo")
	public R<List<Dict>> getInventoryList() {
		List<Dict> dictList = null;
		List<DictBiz> dictBizs =  dictBizClient.getTenantCodeDict(AuthUtil.getTenantId(), InventoryConstant.StorageName.CODE).getData();
		if (null != dictBizs && dictBizs.size()>0) {
			dictList = new ArrayList<>();
			for (DictBiz dictBiz : dictBizs) {

				Dict dict = new Dict();
				dict.setDictValue(dictBiz.getDictValue());
				dict.setDictKey(dictBiz.getDictKey());
				dictList.add(dict);

			}
		}
		return R.data(dictList);
	}


	@GetMapping("/batchFileDownLoad")
	@ApiOperationSupport(order = 12)
	@ApiLog(value = "批量物资模板下载")
	@ApiOperation(value = "批量物资终端模板下载", notes = "批量物资终端模板下载")
	public void batchFileDownLoad(BladeUser bladeUser, HttpServletRequest request, HttpServletResponse response) throws Exception {
		resInfoService.downloadDynamicResImportTemplate(bladeUser, request, response);
	}
	
	@SuppressWarnings("finally")
	@PostMapping("/inventoryBatchInput")
	@ApiOperationSupport(order = 13)
	@ApiLog(value = "批量物资入库")
	@ApiOperation(value = "批量物资入库", notes = "批量物资入库")
	public R<ResInfoPutInStorageExcelResultModelVO> inventoryBatchInput(@RequestParam("file") MultipartFile excel) throws Exception {
		return R.data(resInfoService.inventoryBatchInput(excel, AuthUtil.getTenantId()));
	}

	@GetMapping("/importResultExcel")
	@ApiOperationSupport(order = 14)
	@ApiLog(value = "物资批量导入结果下载")
	@ApiOperation(value = "物资批量导入结果下载", notes = "传入key")
	public void importResultExcelDownload(String key) throws Exception {
		resInfoService.importResultExcelDownload(key);
	}

}
