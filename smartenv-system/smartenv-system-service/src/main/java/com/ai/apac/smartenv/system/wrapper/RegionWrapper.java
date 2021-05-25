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
package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.RegionVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-01-16
 */
public class RegionWrapper extends BaseEntityWrapper<Region, RegionVO>  {

	public static RegionWrapper build() {
		return new RegionWrapper();
 	}

	@Override
	public RegionVO entityVO(Region region) {
		RegionVO regionVO = BeanUtil.copy(region, RegionVO.class);
		String regionLevelName = DictCache.getValue("region_level", Func.toInt(region.getRegionLevel()));
		regionVO.setRegionLevelName(regionLevelName);
		return regionVO;
	}

	public List<INode> listNodeVO(List<Region> list) {
		if(CollectionUtil.isNotEmpty(list)){
			List<INode> collect = list.stream().map(region -> {
				RegionVO regionVO = BeanUtil.copy(region, RegionVO.class);
				return regionVO;
			}).collect(Collectors.toList());
			return ForestNodeMerger.merge(collect);
		}
		return null;
	}
}
