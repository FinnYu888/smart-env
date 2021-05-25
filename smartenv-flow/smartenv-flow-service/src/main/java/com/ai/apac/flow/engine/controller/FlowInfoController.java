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
package com.ai.apac.flow.engine.controller;

import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.smartenv.flow.constant.FlowConst;
import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.smartenv.flow.vo.FlowInfoDetailVO;
import com.ai.apac.smartenv.flow.vo.FlowInfoNodeVO;
import com.ai.apac.smartenv.flow.vo.FlowTaskAllotVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.flow.entity.FlowInfo;
import com.ai.apac.smartenv.flow.vo.FlowInfoVO;
import com.ai.apac.flow.engine.wrapper.FlowInfoWrapper;
import com.ai.apac.flow.engine.service.IFlowInfoService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-09-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("flowinfo")
@Api(value = "流程信息", tags = "流程信息")
public class FlowInfoController extends BladeController {

	private IFlowInfoService flowInfoService;
	private IFlowTaskAllotService flowTaskAllotService;
	IDictClient dictClient;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入flowInfo")
	public R<FlowInfoVO> detail(FlowInfo flowInfo) {
		FlowInfo detail = flowInfoService.getOne(Condition.getQueryWrapper(flowInfo));
		return R.data(FlowInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入flowInfo")
	public R<IPage<FlowInfoVO>> list(FlowInfo flowInfo, Query query) {
		IPage<FlowInfo> pages = flowInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(flowInfo));
		return R.data(FlowInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入flowInfo")
	public R<IPage<FlowInfoVO>> page(FlowInfoVO flowInfo, Query query) {
		IPage<FlowInfoVO> pages = flowInfoService.selectFlowInfoPage(Condition.getPage(query), flowInfo);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入flowInfo")
	public R save(@Valid @RequestBody FlowInfo flowInfo) {
		return R.status(flowInfoService.save(flowInfo));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入flowInfo")
	public R update(@Valid @RequestBody FlowInfo flowInfo) {
		return R.status(flowInfoService.updateById(flowInfo));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入flowInfo")
	public R submit(@Valid @RequestBody FlowInfo flowInfo) {
		return R.status(flowInfoService.saveOrUpdate(flowInfo));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(flowInfoService.deleteLogic(Func.toLongList(ids)));
	}
	/**
	 * 页面列表查询分页
	 */
	@GetMapping("/queryFlowInfoPage")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "页面列表查询分页", notes = "传入flowInfo")
	public R<IPage<FlowInfoVO>> queryFlowInfoPage(FlowInfo flowInfo, Query query) {
		QueryWrapper<FlowInfo> queryWrapper = new QueryWrapper<FlowInfo>();
		if (StringUtils.isNotEmpty(flowInfo.getFlowCode())) {
			queryWrapper.eq("flow_code",flowInfo.getFlowCode());
		}

		if (StringUtils.isNotEmpty(flowInfo.getFlowName())) {
			queryWrapper.like("flow_name", flowInfo.getFlowName());
		}

		queryWrapper.eq("status",1);

		IPage<FlowInfo> pages = flowInfoService.selectFlowInfoPage(Condition.getPage(query),queryWrapper);
		IPage<FlowInfoVO> pageVo = Condition.getPage(query);
		pageVo.setTotal(pages.getTotal());
		pageVo.setPages(pages.getPages());
		List<FlowInfo> flowInfos = pages.getRecords();
		if (CollectionUtil.isNotEmpty(flowInfos)) {
			List<FlowInfoVO> flowInfoVOS = new ArrayList<>();
			for (FlowInfo info : flowInfos) {
				FlowInfoVO flowInfoVO = new FlowInfoVO();
				BeanUtil.copyProperties(info,flowInfoVO);
				flowInfoVO.setFlowTypeName(DictCache.getValue(FlowConst.FLOW_CODE,info.getFlowCode()));
				if (FlowConst.FLOW_CONFIG_FLAG.yes.equals(info.getConfigFlag())) {
					flowInfoVO.setConfigFlagName(FlowConst.FLOW_CONFIG_FLAG.YES);
				}else {
					flowInfoVO.setConfigFlagName(FlowConst.FLOW_CONFIG_FLAG.NO);
				}
				flowInfoVOS.add(flowInfoVO);
			}
			pageVo.setRecords(flowInfoVOS);
		}
		return R.data(pageVo);
	}
	/**
	 * 根据id，查询配置详情。包含流程配置节点
	 */
	@GetMapping("/queryFlowInfoDetail")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据id，查询配置详情。包含流程配置节点", notes = "传入flowId")
	public R<FlowInfoDetailVO> queryFlowInfoDetail(@ApiParam(value = "流程定义id", required = true)@RequestParam("flowId") String flowId) {
		QueryWrapper queryWrapperInfo = new QueryWrapper();
		queryWrapperInfo.eq("id",flowId);
		queryWrapperInfo.eq("tenant_id",AuthUtil.getTenantId());
		FlowInfo flowInfo = flowInfoService.getOne(queryWrapperInfo);
		if (null == flowInfo) {
			return R.data(null);
		}
		FlowInfoDetailVO flowInfoDetailVO = new FlowInfoDetailVO();
		BeanUtil.copyProperties(flowInfo,flowInfoDetailVO);
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("flow_code",flowInfo.getFlowCode());
		queryWrapper.eq("status",1);
		queryWrapper.eq("tenant_id",AuthUtil.getTenantId());
		queryWrapper.orderByAsc("sort");
		List<FlowTaskAllot> flowTaskAllotS =  flowTaskAllotService.queryFlowTaskAllotList(queryWrapper);
		List<FlowTaskAllotVO> flowTaskAllotVOS = null;
		if (CollectionUtil.isNotEmpty(flowTaskAllotS)) {
			flowTaskAllotVOS = new ArrayList<>();
			for (FlowTaskAllot flowTaskAllot:flowTaskAllotS) {
				FlowTaskAllotVO flowTaskAllotVO = new FlowTaskAllotVO();
				BeanUtil.copyProperties(flowTaskAllot,flowTaskAllotVO);
				StringBuilder doneName = new StringBuilder();
				List<Long> doneValues = Func.toLongList(flowTaskAllot.getDoneValue());
				if (FlowConst.done_type.PERSON.equals(flowTaskAllot.getDoneType())) {
					for (Long doneValue:doneValues ) {
						doneName.append(PersonCache.getPersonById(AuthUtil.getTenantId(),doneValue).getPersonName()).append(",");
					}
				}else if (FlowConst.done_type.STATIION.equals(flowTaskAllot.getDoneType())) {
					for (Long doneValue:doneValues ) {
						doneName.append(StationCache.getStationName(doneValue)).append(",");
					}
				}else if (FlowConst.done_type.ROLE.equals(flowTaskAllot.getDoneType())) {
					for (Long doneValue:doneValues ) {
						doneName.append(RoleCache.getRoleName(doneValue.toString())).append(",");
					}
				}
				if (StringUtils.isNotEmpty(doneName.toString())) {
					flowTaskAllotVO.setDoneValueName(doneName.substring(0,doneName.length()-1));
				}
				flowTaskAllotVOS.add(flowTaskAllotVO);
			}
		}
		flowInfoDetailVO.setTaskAllotVOList(flowTaskAllotVOS);

		return R.data(flowInfoDetailVO);
	}
	/**
	 * 修改流程配置
	 */
	@PostMapping("/modifyFlowInfo")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "修改流程配置", notes = "传入FlowInfoDetailVO")
	public R<Boolean> modifyFlowInfo(@RequestBody FlowInfoDetailVO flowInfoDetailVO) {

		flowInfoService.modifyFlowInfo(flowInfoDetailVO);


		return R.data(true);
	}

	/**
	 * 根据流程类型获取流程节点和对应的配置
	 */
	@GetMapping("/getFlowCodeNode")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据id，查询配置详情。包含流程配置节点", notes = "传入flowCode")
	public R<List<FlowInfoNodeVO>> getFlowCodeNode(@ApiParam(value = "流程定义编码", required = true)@RequestParam("flowCode") String flowCode) {
		//List<Dict> dicts = dictClient.getList(flowCode).getData();
		List<Dict> dicts = DictCache.getList(flowCode);

		List<FlowInfoNodeVO> flowInfoNodeVOS = null;
		if (CollectionUtil.isNotEmpty(dicts)) {
			flowInfoNodeVOS = new ArrayList<>();
			for (Dict dict: dicts ) {
				FlowInfoNodeVO flowInfoNodeVO = new FlowInfoNodeVO();
				flowInfoNodeVO.setNodeCode(dict.getDictKey());
				flowInfoNodeVO.setNodeName(dict.getDictValue());
				QueryWrapper queryWrapper = new QueryWrapper();
				//queryWrapper.eq("flow_code",flowCode);
				queryWrapper.eq("task_node",dict.getDictKey());
				queryWrapper.eq("status",1);
				queryWrapper.eq("tenant_id",AuthUtil.getTenantId());
				FlowTaskAllot flowTaskAllot =  flowTaskAllotService.getOne(queryWrapper);
				if (null != flowTaskAllot) {
					FlowTaskAllotVO flowTaskAllotVO = new FlowTaskAllotVO();
					BeanUtil.copyProperties(flowTaskAllot,flowTaskAllotVO);
					StringBuilder doneName = new StringBuilder();
					List<Long> doneValues = Func.toLongList(flowTaskAllot.getDoneValue());
					if (FlowConst.done_type.PERSON.equals(flowTaskAllot.getDoneType())) {
						for (Long doneValue:doneValues ) {
							doneName.append(PersonCache.getPersonById(AuthUtil.getTenantId(),doneValue).getPersonName()).append(",");
						}
					}else if (FlowConst.done_type.STATIION.equals(flowTaskAllot.getDoneType())) {
						for (Long doneValue:doneValues ) {
							doneName.append(StationCache.getStationName(doneValue)).append(",");
						}
					}else if (FlowConst.done_type.ROLE.equals(flowTaskAllot.getDoneType())) {
						for (Long doneValue:doneValues ) {
							doneName.append(RoleCache.getRoleName(doneValue.toString())).append(",");
						}
					}
					if (StringUtils.isNotEmpty(doneName.toString())) {
						flowTaskAllotVO.setDoneValueName(doneName.substring(0,doneName.length()-1));
					}
					flowInfoNodeVO.setFlowTaskAllot(flowTaskAllotVO);
				}
				flowInfoNodeVOS.add(flowInfoNodeVO);

				}
			}
		return R.data(flowInfoNodeVOS);

	}
}
