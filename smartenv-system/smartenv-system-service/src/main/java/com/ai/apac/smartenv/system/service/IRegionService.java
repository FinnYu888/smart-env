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
package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.BigScreenInfoVO;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
import com.ai.apac.smartenv.system.vo.BusiRegionVO;
import com.ai.apac.smartenv.system.vo.RegionVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IRegionService extends BaseService<Region> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param region
	 * @return
	 */
	IPage<RegionVO> selectRegionPage(IPage<RegionVO> page, RegionVO region);

	/**
	 * 删除区域
	 * @param regionIds
	 * @return
	 */
	boolean removeRegion(String regionIds);

	/**
	 * 保存区域
	 * @param busiRegionVO
	 * @return
	 */
	boolean savaOrUpdateRegionNew(BusiRegionVO busiRegionVO);

	/**
	 * 查询业务区域
	 * @param regionId
	 * @return
	 */
	BusiRegionVO queryBusiRegionList(Long regionId);

	/**
	 * 大屏根据业务区域查询告警、事件、人员、车辆
	 * @param regionId
	 * @return
	 */
	BigScreenInfoVO queryBigScreenInfoCountByRegion(Long regionId,String tenantId);

	BigScreenInfoVO queryBigScreenInfoCountByAllRegion(String tenantId);


	/**
	 * 查询包含的业务片区列表
	 * @param regionId
	 * @return
	 */
	BusiRegionTreeVO queryChildBusiRegionList(Long regionId);
	/**
	 * 查询所有业务片区
	 */
	List<BusiRegionVO> queryAllBusiRegionAndNodes(String regionType,String tenantId);

	/**
	 * 查询所有业务片区
	 */
	List<Region> queryAllBusiRegionList(String regionType,String tenantId);
    /**
     * 查询所有上级是行政区域的业务区域
     */
	List<Region> queryBusiRegionListForBS(String regionType,String tenantId);

}
