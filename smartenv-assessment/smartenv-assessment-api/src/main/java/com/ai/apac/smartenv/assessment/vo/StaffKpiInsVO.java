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
package com.ai.apac.smartenv.assessment.vo;

import com.ai.apac.smartenv.assessment.dto.KpiInsDetailsMongoDBDTO;
import com.ai.apac.smartenv.assessment.entity.StaffKpiIns;
import com.ai.apac.smartenv.assessment.entity.StaffKpiInsDetail;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 考核实例表，存放每个人的kpi视图实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "StaffKpiInsVO对象", description = "考核实例表，存放每个人的kpi")
public class StaffKpiInsVO extends StaffKpiIns {
	private static final long serialVersionUID = 1L;

	private List<KpiInsDetailsMongoDBDTO> kpiInsDetailsMongoDBDTOList;

	private String maxScore;

	private String scoreType;

	private String scoreTypeName;

	private String scorerName;

	private String staffName;

	private String stationName;

	private String deptName;

	private String statusName;



}
