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

import com.ai.apac.smartenv.assessment.dto.KpiGradeDTO;
import com.ai.apac.smartenv.assessment.dto.StaffKpiInsDTO;
import com.ai.apac.smartenv.assessment.dto.StaffKpiInsModel;
import com.ai.apac.smartenv.assessment.dto.StaffKpiInsQueryDTO;
import com.ai.apac.smartenv.assessment.entity.KpiTarget;
import com.ai.apac.smartenv.assessment.entity.KpiTplBand;
import com.ai.apac.smartenv.assessment.service.IKpiTargetService;
import com.ai.apac.smartenv.assessment.service.IKpiTplBandService;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.entity.StaffKpiIns;
import com.ai.apac.smartenv.assessment.vo.StaffKpiInsVO;
import com.ai.apac.smartenv.assessment.wrapper.StaffKpiInsWrapper;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 考核实例表，存放每个人的kpi 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/staffkpiins")
@Api(value = "考核实例表，存放每个人的kpi", tags = "考核实例表，存放每个人的kpi接口")
public class StaffKpiInsController extends BladeController {

	private IStaffKpiInsService staffKpiInsService;

	private IKpiTargetService kpiTargetService;

	private IKpiTplBandService kpiTplBandService;

	/**
	 * 自定义分页 考核实例表，存放每个人的kpi
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入staffKpiIns")
	@ApiLog(value = "分页查询考核评估")
	public R<IPage<StaffKpiInsVO>> page(StaffKpiInsQueryDTO staffKpiInsQueryDTO, Query query) {
		IPage<StaffKpiInsVO> pages = staffKpiInsService.selectStaffKpiInsPage(Condition.getPage(query), staffKpiInsQueryDTO);
		return R.data(pages);
	}

	/**
	 * 修改 考核实例表，存放每个人的kpi
	 */
	@GetMapping("/export")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "导出考核打分", notes = "导出考核打分")
	@ApiLog(value = "导出考核打分")
	public void export(StaffKpiInsQueryDTO staffKpiInsQueryDTO) {
		List<StaffKpiInsModel> staffKpiInsModelList = staffKpiInsService.selectStaffKpiIns(staffKpiInsQueryDTO);
		if(staffKpiInsModelList.size() > 0 ){
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = requestAttributes.getResponse();
			OutputStream out = null;
			try {
				response.reset(); // 清除buffer缓存
				// 指定下载的文件名
				String fileName = "考核打分导出";
				out = response.getOutputStream();
				response.setContentType("application/x-msdownload;charset=utf-8");
				response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
				ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
				Sheet sheet1 = new Sheet(1, 0, StaffKpiInsModel.class);
				sheet1.setSheetName("sheet1");
				writer.write(staffKpiInsModelList, sheet1);
				writer.finish();

			} catch (IOException e) {
				throw new ServiceException("导出考核记录异常");
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					throw new ServiceException("导出考核记录异常");
				}
			}
		}else{
			throw new ServiceException("无考核记录可导出");

		}
	}


	/**
	 * 修改 考核实例表，存放每个人的kpi
	 */
	@PostMapping("/score")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "考核评估打分", notes = "传入staffKpiIns")
	@ApiLog(value = "考核评估打分")
	public R update(@Valid @RequestBody StaffKpiInsDTO staffKpiInsDTO) {
		return R.status(staffKpiInsService.updateStaffKpiInsScore(staffKpiInsDTO));
	}


	/**
	 * 修改 考核实例表，存放每个人的kpi
	 */
	@PostMapping("/grade")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "考核等级计算", notes = "考核等级计算")
	@ApiLog(value = "考核等级计算")
	public R<String> gradeIns(@Valid @RequestBody KpiGradeDTO kpiGradeDTO) {
		String grade = "";
		Double score = Double.parseDouble(kpiGradeDTO.getScore());
		KpiTarget kpiTarget = kpiTargetService.getById(kpiGradeDTO.getKpiTargetId());
		String kpiTplDefId = kpiTarget.getKpiTplId().toString();

		KpiTplBand queryEntity=new KpiTplBand();
		queryEntity.setKpiTplId(kpiTplDefId);
		QueryWrapper<KpiTplBand> bandQuery=new QueryWrapper<>(queryEntity);

		List<KpiTplBand> list = kpiTplBandService.list(bandQuery);

		for(KpiTplBand kpiTplBand:list){
			if(kpiTplBand.getMaxScore() >= score && score >= kpiTplBand.getMinScore()){
				return R.data(kpiTplBand.getBandLevel());
			}
		}

		return R.data("");
	}


	
}
