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

import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.vo.FacilityInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-02-11
 */
public interface FacilityInfoMapper extends BaseMapper<FacilityInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param facilityInfo
	 * @return
	 */
	List<FacilityInfoVO> selectFacilityInfoPage(IPage page, FacilityInfoVO facilityInfo);


	List<FacilityInfoVO> selectTranstationTotalList( IPage page,@Param("ew") QueryWrapper queryWrapper);

}
