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
package com.ai.apac.smartenv.assessment.service.impl;

import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.assessment.dto.KpiInsDetailsMongoDBDTO;
import com.ai.apac.smartenv.assessment.dto.KpiInsMongoDBDTO;
import com.ai.apac.smartenv.assessment.entity.StaffKpiIns;
import com.ai.apac.smartenv.assessment.entity.StaffKpiInsDetail;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsService;
import com.ai.apac.smartenv.assessment.vo.KpiTargetLostPointsVO;
import com.ai.apac.smartenv.assessment.vo.StaffKpiInsDetailVO;
import com.ai.apac.smartenv.assessment.mapper.StaffKpiInsDetailMapper;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsDetailService;
import com.ai.apac.smartenv.common.constant.AssessmentConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工考核实例明细 服务实现类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Service
public class StaffKpiInsDetailServiceImpl extends BaseServiceImpl<StaffKpiInsDetailMapper, StaffKpiInsDetail> implements IStaffKpiInsDetailService {

	private IStaffKpiInsService staffKpiInsService;

	@Override
	public IPage<StaffKpiInsDetailVO> selectStaffKpiInsDetailPage(IPage<StaffKpiInsDetailVO> page, StaffKpiInsDetailVO staffKpiInsDetail) {
		return page.setRecords(baseMapper.selectStaffKpiInsDetailPage(page, staffKpiInsDetail));
	}

	@Override
	public boolean saveBatchStaffKpiInsDetail(List<StaffKpiInsDetail> staffKpiInsDetailList) {
		staffKpiInsDetailList.forEach(staffKpiInsDetail -> {
			save(staffKpiInsDetail);
		});
		return true;
	}



}
