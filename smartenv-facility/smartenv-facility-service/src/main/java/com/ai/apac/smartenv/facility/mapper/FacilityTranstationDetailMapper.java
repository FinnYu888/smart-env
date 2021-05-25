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
package com.ai.apac.smartenv.facility.mapper;

import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import com.ai.apac.smartenv.facility.entity.TranstationEveryDay;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface FacilityTranstationDetailMapper extends BaseMapper<FacilityTranstationDetail> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param facilityTranstationDetail
	 * @return
	 */
	List<FacilityTranstationDetailVO> selectFacilityTranstationDetailPage(IPage page, FacilityTranstationDetailVO facilityTranstationDetail);

	FacilityTranstationDetail getCurrentDayWorkInfo(@Param("facilityId") Long facilityId);

	/**
	*按天统计中转站转运数量
	*/
	List<TranstationEveryDay> staticsTranstationEveryDay(IPage page, @Param("facilityId") Long facilityId, @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);

	List<GarbageAmountDaily> lastDaysGarbageAmount(@Param("garbageType") String garbageType, @Param("startDate") String startDate,
													@Param("endDate") String endDate,@Param("tenantId") String tenantId);


	List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountGroupByRegion(@Param("startDate") String startDate,
																		   @Param("endDate") String endDate, @Param("tenantId") String tenantId);
	/**
	*按条件查询明细，并分页
	*/
	List<FacilityTranstationDetailVO> listfacilityTranstationDetail(IPage page, @Param("facilityId") Long facilityId, @Param("startDate") String startDate,
																	@Param("endDate") String endDate,@Param("garbageType") String garbageType);


}
