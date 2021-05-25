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
package com.ai.apac.smartenv.system.mapper;

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.RegionVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface RegionMapper extends BaseMapper<Region> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param region
	 * @return
	 */
	List<RegionVO> selectRegionPage(IPage page, RegionVO region);

	/**
	 * 自定义列表查询
	 *
	 * @param region
	 * @return
	 */
	List<RegionVO> selectRegionList(RegionVO region);

	List<Region> selectRegionListForBS(@Param("ew") QueryWrapper queryWrapper);

}
