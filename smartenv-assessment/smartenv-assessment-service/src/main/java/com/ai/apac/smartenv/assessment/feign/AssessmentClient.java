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
package com.ai.apac.smartenv.assessment.feign;

import com.ai.apac.smartenv.assessment.entity.*;
import com.ai.apac.smartenv.assessment.service.*;
import com.ai.apac.smartenv.common.constant.AssessmentConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: AssessmentClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年3月5日 下午3:26:33 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年3月5日     zhaoaj           v1.0.0               修改原因
 */
@RestController
@AllArgsConstructor
public class AssessmentClient implements IAssessmentClient {

	private IKpiCatalogService kpiCatalogService;
	private IKpiDefService kpiDefService;
	private IKpiTargetService kpiTargetService;
	private IKpiTplDetailService kpiTplDetailService;
	private IKpiTplDefService kpiTplDefService;

	private IStaffKpiInsService staffKpiInsService;
	private IStaffKpiInsDetailService staffKpiInsDetailService;


	@Override
	@GetMapping(LIST_ALL_KPI_CATALOG)
	public R<List<KpiCatalog>> listAllKpiCatalog() {
		return R.data(kpiCatalogService.list());
	}

	@Override
	@GetMapping(KPI_CATALOG_BY_ID)
	public R<KpiCatalog> getKpiCatalogById(Long kpiCatalogId) {
		return R.data(kpiCatalogService.getById(kpiCatalogId));
	}

	@Override
	public R<List<KpiDef>> listAllKpiDef() {
		return R.data(kpiDefService.list());
	}

	@Override
	public R<KpiDef> getKpiDefById(Long kpiDefId) {
		return R.data(kpiDefService.getById(kpiDefId));
	}

	@Override
	public R<Boolean> endKpiTarget() {
		return R.data(kpiTargetService.endKpiTarget());
	}

	@Override
	public R<KpiTplDetail> getKpiTplDetailById(Long id) {
		return R.data(kpiTplDetailService.getById(id));
	}

	@Override
	public R<KpiTplDef> getKpiTplDefById(Long id) {
		return R.data(kpiTplDefService.getById(id));
	}

	@Override
	public R<List<StaffKpiIns>> listEndKpiTargetRecently(Integer days, String tenantId) {
		QueryWrapper<StaffKpiIns> queryWrapper = new QueryWrapper<StaffKpiIns>();
		queryWrapper.lambda().eq(StaffKpiIns::getStatus, AssessmentConstant.TargetStatus.END);
		Timestamp startDate = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(TimeUtil.getSysDate().getTime(),-days));
		queryWrapper.lambda().ge(StaffKpiIns::getDeadLine,startDate);
		queryWrapper.lambda().isNotNull(StaffKpiIns::getTotalScore);
		queryWrapper.lambda().eq(StaffKpiIns::getTenantId,tenantId);
		return R.data(staffKpiInsService.list(queryWrapper));
	}

	@Override
	public R<List<StaffKpiInsDetail>> listKpiInsDatailsByKpiInsId(Long kpiInsId, String tenantId) {
		QueryWrapper<StaffKpiInsDetail> queryWrapper = new QueryWrapper<StaffKpiInsDetail>();
		queryWrapper.lambda().eq(StaffKpiInsDetail::getKpiInsId, kpiInsId);
		queryWrapper.lambda().eq(StaffKpiInsDetail::getTenantId,tenantId);
		return R.data(staffKpiInsDetailService.list(queryWrapper));
	}


}
