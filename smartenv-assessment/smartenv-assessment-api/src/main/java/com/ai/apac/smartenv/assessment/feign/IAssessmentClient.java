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
import com.ai.apac.smartenv.common.constant.ApplicationConstant;

import java.util.List;

import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: IAssessmentClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年3月5日 下午3:25:08 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年3月5日     zhaoaj           v1.0.0               修改原因
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_ASSESSMENT_NAME
)
public interface IAssessmentClient {

	String API_PREFIX = "/client";
	String LIST_ALL_KPI_CATALOG = API_PREFIX + "/list-all-kpi-catalog";
	String LIST_ALL_KPI_DEF = API_PREFIX + "/list-all-kpi-def";
	String KPI_CATALOG_BY_ID = API_PREFIX + "/kpi-catalog-by-id";
	String KPI_DEF_BY_ID = API_PREFIX + "/kpi-def-by-id";
	String KPI_TPL_DETAIL_BY_ID = API_PREFIX + "/kpi-tpl-detail-by-id";
	String KPI_TPL_BY_ID = API_PREFIX + "/kpi-tpl-by-id";

	String END_KPI_TARGET = API_PREFIX + "/end-kpi-target";
	String LIST_END_KPI_TARGET_RECENTLY = API_PREFIX + "/list-end-kpi-target-recently";
	String LIST_KPI_INS_DETAILS_BY_KPI_INS_ID = API_PREFIX + "/list-kpi-ins-details-by-kpi-ins-id";

    @GetMapping(LIST_ALL_KPI_CATALOG)
	R<List<KpiCatalog>> listAllKpiCatalog();

	@GetMapping(KPI_CATALOG_BY_ID)
	R<KpiCatalog> getKpiCatalogById(@RequestParam("kpiCatalogId") Long kpiCatalogId);

	@GetMapping(LIST_ALL_KPI_DEF)
	R<List<KpiDef>> listAllKpiDef();
	
	@GetMapping(KPI_DEF_BY_ID)
	R<KpiDef> getKpiDefById(@RequestParam("kpiDefId") Long kpiDefId);

    @GetMapping(END_KPI_TARGET)
    R<Boolean> endKpiTarget();


	@GetMapping(KPI_TPL_DETAIL_BY_ID)
	R<KpiTplDetail> getKpiTplDetailById(@RequestParam("id") Long id);

	@GetMapping(KPI_TPL_BY_ID)
	R<KpiTplDef> getKpiTplDefById(@RequestParam("id") Long id);

	@GetMapping(LIST_END_KPI_TARGET_RECENTLY)
	R<List<StaffKpiIns>> listEndKpiTargetRecently(@RequestParam("days") Integer days, @RequestParam("tenantId") String tenantId);

	@GetMapping(LIST_KPI_INS_DETAILS_BY_KPI_INS_ID)
	R<List<StaffKpiInsDetail>> listKpiInsDatailsByKpiInsId(@RequestParam("kpiInsId") Long kpiInsId, @RequestParam("tenantId") String tenantId);
}
