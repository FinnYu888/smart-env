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
package com.ai.apac.smartenv.assessment.controller;

import com.ai.apac.smartenv.assessment.dto.KpiTplBandDTO;
import com.ai.apac.smartenv.assessment.dto.KpiTplDefDTO;
import com.ai.apac.smartenv.assessment.dto.KpiTplDetailDTO;
import com.ai.apac.smartenv.assessment.entity.*;
import com.ai.apac.smartenv.assessment.service.*;
import com.ai.apac.smartenv.assessment.vo.DefaultBandTpl;
import com.ai.apac.smartenv.assessment.vo.KpiTplBandVO;
import com.ai.apac.smartenv.assessment.vo.KpiTplDetailVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTplBandWrapper;
import com.ai.apac.smartenv.assessment.wrapper.KpiTplDetailWrapper;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.vo.KpiTplDefVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTplDefWrapper;
import org.springblade.core.boot.ctrl.BladeController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 考核模板定义表 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpitpldef")
@Api(value = "考核模板定义表", tags = "考核模板定义表")
public class KpiTplDefController extends BladeController {

	private IKpiTplDefService kpiTplDefService;
	private IKpiTplBandService kpiTplBandService;
	private IKpiTplDetailService kpiTplDetailService;

	private IKpiDefService kpiDefService;

	private IDictClient dictClient;

	private IKpiTplCatalogService kpiTplCatalogService;

	private IKpiCatalogService kpiCatalogService;



//	@GetMapping("/noPageList/{kpiTplName}")
	@RequestMapping(value = {"/noPageList/{kpiTplName}","/noPageList"})
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取不分页列表", notes = "获取不分页列表")
	@ApiLog(value = "获取考核模板不分页列表")
	public R<List<KpiTplDefVO>> noPageList(@PathVariable(required = false) String kpiTplName){
		KpiTplDef queryCondition=new KpiTplDef();
		QueryWrapper<KpiTplDef> queryWrapper = Condition.getQueryWrapper(queryCondition);
		if (kpiTplName!=null){
			queryWrapper.like("kpi_tpl_name",kpiTplName);
		}
		List<KpiTplDef> pages = kpiTplDefService.list( queryWrapper);
		List<KpiTplDefVO> kpiTplDefVOList = KpiTplDefWrapper.build().listVO(pages);
		if (CollectionUtil.isNotEmpty(kpiTplDefVOList)){
			kpiTplDefVOList.forEach(record->{
				QueryWrapper<KpiTplDetail> detailWrapper=new QueryWrapper<>();
				record.setDetailsDTOs(KpiTplDetailWrapper.build().listVO(kpiTplDetailService.list(detailWrapper)));
				String score_type = dictClient.getValue("score_type", record.getScoreType().toString()).getData();
				DefaultBandTpl bandTpl = JSON.parseObject(score_type, DefaultBandTpl.class);
				record.setScoreName(bandTpl.getValueName());
				KpiTplCatalog catalog = kpiTplCatalogService.getById(record.getKpiTplCatalogId());
				if (catalog!=null){
					record.setCatalogName(catalog.getCatalogName());
				}

			});

		}
		return R.data(kpiTplDefVOList);
	}




	@GetMapping("/getDefaultBand")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取默认模板", notes = "传入kpiTplDef")
	@ApiLog(value = "获取考核模板默认模板")
	public R<List<DefaultBandTpl>> getDefaultBand(){
		List<DefaultBandTpl> defaultBandTpls=new ArrayList<>();
		List<Dict> score_type = dictClient.getList("score_type").getData();
		if (CollectionUtil.isNotEmpty(score_type)){
			for (Dict dict : score_type) {
				String dictValue = dict.getDictValue();
				DefaultBandTpl bandTpl = JSON.parseObject(dictValue, DefaultBandTpl.class);
				bandTpl.setValue(dict.getDictKey());
				defaultBandTpls.add(bandTpl);
			}
		}
		return R.data(defaultBandTpls);
	}


	/**
	 * 详情
	 */
	@GetMapping("/detail/{id}")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入kpiTplDef")
	@ApiLog(value = "获取考核模板详情")
	public R<KpiTplDefDTO> detail(@PathVariable String id) {


		KpiTplDef detail = kpiTplDefService.getById(id);
		if (detail!=null){
			KpiTplDefDTO defDTO = BeanUtil.copy(detail,KpiTplDefDTO.class);
			KpiTplDetail detailQuery=new KpiTplDetail();
			detailQuery.setKpiTplId(Long.parseLong(id));
			QueryWrapper<KpiTplDetail> detailWrapper=new QueryWrapper<>(detailQuery);
			List<KpiTplDetail> list = kpiTplDetailService.list(detailWrapper);
			if(CollectionUtil.isNotEmpty(list)){
				List<KpiTplDetailDTO> kpiTplDetailDTOS = BeanUtil.copyProperties(list, KpiTplDetailDTO.class);
				if (CollectionUtil.isNotEmpty(kpiTplDetailDTOS)) {
				    for (KpiTplDetailDTO detailDTO :kpiTplDetailDTOS ) {
						Long kpiId = detailDTO.getKpiId();
						KpiDef kpiDef = kpiDefService.getById(kpiId);
						Long kpiCatalogId = kpiDef.getKpiCatalog();
						KpiCatalog kpiCatalog = kpiCatalogService.getById(kpiCatalogId);
						detailDTO.setKpiCatalogName(kpiCatalog.getCatalogName());
						detailDTO.setKpiDescription(kpiDef.getKpiDescription());
					}
				}
				defDTO.setDetailsDTOs(kpiTplDetailDTOS);
			}

			KpiTplBand bandQuery=new KpiTplBand();
			bandQuery.setKpiTplId(id);
			QueryWrapper<KpiTplBand> bandWrapper=new QueryWrapper<>(bandQuery);
			List<KpiTplBand> bandList = kpiTplBandService.list(bandWrapper);
			if (CollectionUtil.isNotEmpty(bandList)){
				List<KpiTplBandDTO> kpiTplBandDTOS = BeanUtil.copyProperties(bandList, KpiTplBandDTO.class);
				defDTO.setBandDTOS(kpiTplBandDTOS);
			}
			String score_type = dictClient.getValue("score_type", defDTO.getScoreType().toString()).getData();
			DefaultBandTpl bandTpl = JSON.parseObject(score_type, DefaultBandTpl.class);
			defDTO.setScoreName(bandTpl.getValueName());
			return R.data(defDTO);
		}

		return R.data(null);
	}

	/**
	 * 分页 考核模板定义表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页查询", notes = "传入kpiTplDef")
	@ApiLog(value = "获取考核模板分页列表")
	public R<IPage<KpiTplDefVO>> list(KpiTplDef kpiTplDef, Query query) {
		KpiTplDef queryCondition=new KpiTplDef();
		BeanUtil.copyProperties(kpiTplDef,queryCondition);
		queryCondition.setKpiTplName(null);
		QueryWrapper<KpiTplDef> queryWrapper = Condition.getQueryWrapper(queryCondition);
		if (StringUtil.isNotBlank(kpiTplDef.getKpiTplName())){
			queryWrapper.like("kpi_tpl_name",kpiTplDef.getKpiTplName());
		}
		IPage<KpiTplDef> pages = kpiTplDefService.page(Condition.getPage(query), Condition.getQueryWrapper(kpiTplDef));
		IPage<KpiTplDefVO> kpiTplDefVOIPage = KpiTplDefWrapper.build().pageVO(pages);
		if (CollectionUtil.isNotEmpty(pages.getRecords())){
			kpiTplDefVOIPage.getRecords().forEach(record->{


				KpiTplDetail queryEntity=new KpiTplDetail();
				queryEntity.setKpiTplId(record.getId());
				QueryWrapper<KpiTplDetail> detailWrapper=new QueryWrapper<>(queryEntity);

				record.setDetailsDTOs(KpiTplDetailWrapper.build().listVO(kpiTplDetailService.list(detailWrapper)));

				String score_type = dictClient.getValue("score_type", record.getScoreType().toString()).getData();
				DefaultBandTpl bandTpl = JSON.parseObject(score_type, DefaultBandTpl.class);
				record.setScoreName(bandTpl.getValueName());
				KpiTplCatalog catalog = kpiTplCatalogService.getById(record.getKpiTplCatalogId());
				if (catalog!=null){
					record.setCatalogName(catalog.getCatalogName());
				}

			});

		}
		return R.data(kpiTplDefVOIPage);
	}


//	/**
//	 * 自定义分页 考核模板定义表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "自定义", notes = "传入kpiTplDef")
//	public R<IPage<KpiTplDefVO>> page(KpiTplDefVO kpiTplDef, Query query) {
//		IPage<KpiTplDefVO> pages = kpiTplDefService.selectKpiTplDefPage(Condition.getPage(query), kpiTplDef);
//		return R.data(pages);
//	}

	/**
	 * 新增 考核模板定义表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiTplDef")
	@ApiLog(value = "新增考核模板")
	public R save(@Valid @RequestBody KpiTplDefDTO kpiTplDefDTO) {
		KpiTplDef kpiTplDef=new KpiTplDef();


		if (kpiTplDefDTO.getScoreType()!=null){
			String score_type = dictClient.getValue("score_type", kpiTplDefDTO.getScoreType().toString()).getData();
			DefaultBandTpl bandTpl = JSON.parseObject(score_type, DefaultBandTpl.class);
			Double maxScore = bandTpl.getMaxScore();
			kpiTplDefDTO.setTotalScore(maxScore.intValue());
		}


		BeanUtils.copyProperties(kpiTplDefDTO,kpiTplDef);
		boolean defSave = kpiTplDefService.save(kpiTplDef);
		Long id = kpiTplDef.getId();
		List<KpiTplBandDTO> bandDTOS = kpiTplDefDTO.getBandDTOS();
		boolean blandSave=false;
		boolean detailSave=false;
		if (CollectionUtil.isNotEmpty(bandDTOS)){
			List<KpiTplBand> kpiTplBands = BeanUtil.copyProperties(bandDTOS, KpiTplBand.class);
			for (KpiTplBand tplBand :kpiTplBands ) {
				tplBand.setKpiTplId(id.toString());
				blandSave = kpiTplBandService.save(tplBand);
			}
		}
		if (CollectionUtil.isNotEmpty(kpiTplDefDTO.getDetailsDTOs())){

//			kpiTplDefDTO.getDetailsDTOs().forEach(detail->{
//
//				double weigh = Double.parseDouble(detail.getWeightingStr());
//				BigDecimal b = new BigDecimal(weigh);
//				String weighStr = b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
//
//
//				detail.setWeighting(weighStr);
//
//			});


			List<KpiTplDetail> kpiTplDetails = BeanUtil.copyProperties(kpiTplDefDTO.getDetailsDTOs(), KpiTplDetail.class);
			for (KpiTplDetail tplDetail :kpiTplDetails ) {
				tplDetail.setKpiTplId(id);
				KpiDef kpiDef = kpiDefService.getById(tplDetail.getKpiId());
//				BeanUtil.copyProperties(kpiDef,tplDetail);
				tplDetail.setKpiRemark(kpiDef.getKpiRemark());
				tplDetail.setAppraisalCriteria(kpiDef.getAppraisalCriteria());
				tplDetail.setKpiId(kpiDef.getId());
				tplDetail.setKpiName(kpiDef.getKpiName());


//				tplDetail.setWeighting(kpiDef.getWeighting());//权重自己填

				detailSave = kpiTplDetailService.save(tplDetail);

			}
		}


		return R.data(defSave&&blandSave&&detailSave);
	}

	/**
	 * 修改 考核模板定义表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入kpiTplDef")
	@ApiLog(value = "更新考核模板")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R update(@Valid @RequestBody KpiTplDefDTO kpiTplDefDTO) {
		KpiTplDef kpiTplDef = BeanUtil.copyProperties(kpiTplDefDTO, KpiTplDef.class);
		Boolean status=true;

		if (kpiTplDefDTO.getScoreType()!=null){
			String score_type = dictClient.getValue("score_type", kpiTplDefDTO.getScoreType().toString()).getData();
			DefaultBandTpl bandTpl = JSON.parseObject(score_type, DefaultBandTpl.class);
			Double maxScore = bandTpl.getMaxScore();
			kpiTplDefDTO.setTotalScore(maxScore.intValue());
		}
		if (CollectionUtil.isNotEmpty(kpiTplDefDTO.getBandDTOS())){


			KpiTplBand queryEntity=new KpiTplBand();
			queryEntity.setKpiTplId(kpiTplDef.getId().toString());
			QueryWrapper<KpiTplBand> bandQuery=new QueryWrapper<>(queryEntity);

			List<KpiTplBand> list = kpiTplBandService.list(bandQuery);
			if (CollectionUtil.isNotEmpty(list)){
				List<Long> longlist=new ArrayList<>();
				list.forEach(band->{
					longlist.add(band.getId());
				});
				kpiTplBandService.deleteLogic(longlist);
			}
			for (KpiTplBand tplBand : kpiTplDefDTO.getBandDTOS()) {
				tplBand.setKpiTplId(kpiTplDef.getId().toString());
				status=status&&kpiTplBandService.save(tplBand);
			}
		}

		if (CollectionUtil.isNotEmpty(kpiTplDefDTO.getDetailsDTOs())){
			@NotEmpty List<Long> longList=new ArrayList<>();
			KpiTplDetail queryEntity=new KpiTplDetail();
			queryEntity.setKpiTplId(kpiTplDef.getId());
			QueryWrapper<KpiTplDetail> queryWrapper=new QueryWrapper<>(queryEntity);
			List<KpiTplDetail> list = kpiTplDetailService.list(queryWrapper);
			if (CollectionUtil.isNotEmpty(list)){

				list.forEach(detail->{
					longList.add(detail.getId());
				});
				kpiTplDetailService.deleteLogic(longList);
			}
			for (KpiTplDetail tplDetail : kpiTplDefDTO.getDetailsDTOs()) {
				tplDetail.setKpiTplId(kpiTplDefDTO.getId());
				KpiDef kpiDef = kpiDefService.getById(tplDetail.getKpiId());
//				BeanUtil.copyProperties(kpiDef,tplDetail);
				tplDetail.setKpiRemark(kpiDef.getKpiRemark());
				tplDetail.setAppraisalCriteria(kpiDef.getAppraisalCriteria());
				tplDetail.setKpiId(kpiDef.getId());
				tplDetail.setKpiName(kpiDef.getKpiName());


				status=status&&kpiTplDetailService.save(tplDetail);
			}
		}
		return R.status(status&&kpiTplDefService.updateById(kpiTplDef));
	}

//	/**
//	 * 新增或修改 考核模板定义表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入kpiTplDef")
//	public R submit(@Valid @RequestBody KpiTplDef kpiTplDef) {
//		return R.status(kpiTplDefService.saveOrUpdate(kpiTplDef));
//	}

	
	/**
	 * 删除 考核模板定义表
	 */
	@DeleteMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	@ApiLog(value = "删除考核模板")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> list = Func.toLongList(ids);
		Boolean status=true;
		for (Long id : list) {

			KpiTplDef tpl = kpiTplDefService.getById(id);
			KpiTplBand bandQuery=new KpiTplBand();
			bandQuery.setKpiTplId(tpl.getId().toString());
			QueryWrapper<KpiTplBand> bandWrapper=new QueryWrapper<>(bandQuery);
			List<KpiTplBand> bandList = kpiTplBandService.list(bandWrapper);
			KpiTplDetail tplDetailQuery=new KpiTplDetail();
			tplDetailQuery.setKpiTplId(tpl.getId());
			QueryWrapper<KpiTplDetail> detailWrapper=new QueryWrapper<>(tplDetailQuery);
			List<KpiTplDetail> tplDetails = kpiTplDetailService.list(detailWrapper);
			if (CollectionUtil.isNotEmpty(bandList)){
				for (KpiTplBand tplBand : bandList) {
					status=status&&kpiTplBandService.deleteLogic(Func.toLongList(tplBand.getId().toString()));
				}
			}

			if (CollectionUtil.isNotEmpty(tplDetails)){
				for (KpiTplDetail tplDetail : tplDetails) {
					status=status&&kpiTplDetailService.deleteLogic(Func.toLongList(tplDetail.getId().toString()));

				}
			}
		}
		return R.status(status&&kpiTplDefService.deleteLogic(Func.toLongList(ids)));
	}

	
}
