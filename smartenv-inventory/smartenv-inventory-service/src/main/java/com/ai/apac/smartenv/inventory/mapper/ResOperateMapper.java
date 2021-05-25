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
package com.ai.apac.smartenv.inventory.mapper;

import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.ai.apac.smartenv.inventory.entity.ResOperateQuery;
import com.ai.apac.smartenv.inventory.vo.ResOperateQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResOperateVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-02-27
 */
public interface ResOperateMapper extends BaseMapper<ResOperate> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resOperate
	 * @return
	 */
	List<ResOperateVO> selectResOperatePage(IPage page, ResOperateVO resOperate);
	/**
	 * 查询操作记录
	 *
	 * @param page
	 * @param resOperate
	 * @return
	 */
	List<ResOperateQuery> queryResOperatePage(IPage page, @Param("ew")QueryWrapper<ResOperateVO> queryWrapper);
}
