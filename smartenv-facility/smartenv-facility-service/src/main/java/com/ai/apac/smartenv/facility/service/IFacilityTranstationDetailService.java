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
package com.ai.apac.smartenv.facility.service;

import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import com.ai.apac.smartenv.facility.entity.TranstationEveryDay;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface IFacilityTranstationDetailService extends BaseService<FacilityTranstationDetail> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param facilityTranstationDetail
	 * @return
	 */
	IPage<FacilityTranstationDetailVO> selectFacilityTranstationDetailPage(IPage<FacilityTranstationDetailVO> page, FacilityTranstationDetailVO facilityTranstationDetail);

	/**
	 * 查询中转站当天工作量信息
	 */
	FacilityTranstationDetail getCurrentDayWorkInfo(Long facilityId);

	/**
	 * 按天统计中转站工作量数据
	 */
	FacilityTranstationDetail statisticalEveryDate(Long facilityId, String startDate, String endDate);

	/**
	 * 按天统计
	 */
	IPage<TranstationEveryDay> staticsTranstationEveryDay(IPage page, Long facilityId, String startDate,
														  String endDate);

	/**
	 * 按时间查询
	 */
	IPage<FacilityTranstationDetail> listfacilityTranstationDetail(IPage page, Long facilityId, String startDate,
																   String endDate, String garbageType);

	List<GarbageAmountDaily> lastDaysGarbageAmount(String garbageType, String startDate,
												 String endDate,String tenantId);

	List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountGroupByRegion(String startDate,
																		   String endDate,String tenantId);

	Boolean saveDetail(FacilityTranstationDetail facilityTranstationDetail);
}
