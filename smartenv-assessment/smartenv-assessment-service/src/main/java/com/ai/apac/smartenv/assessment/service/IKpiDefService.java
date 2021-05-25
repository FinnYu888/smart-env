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
package com.ai.apac.smartenv.assessment.service;

import com.ai.apac.smartenv.assessment.entity.KpiDef;
import com.ai.apac.smartenv.assessment.vo.KpiDefVO;

import java.util.List;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 考核指标定义表 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IKpiDefService extends BaseService<KpiDef> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param kpiDef
	 * @return
	 */
	IPage<KpiDefVO> selectKpiDefPage(IPage<KpiDefVO> page, KpiDefVO kpiDef);

	/**
	 * 
	 * @Function: IKpiDefService::saveKpiDef
	 * @Description: 保存
	 * @param kpiDef
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午8:20:37 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean saveKpiDef(KpiDef kpiDef);

	/**
	 * 
	 * @Function: IKpiDefService::updateKpiDefById
	 * @Description: 更新
	 * @param kpiDef
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午8:20:43 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean updateKpiDefById(KpiDef kpiDef);

	/**
	 * 
	 * @Function: IKpiDefService::page
	 * @Description: 分页查询
	 * @param kpiDef
	 * @param query
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午8:20:52 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	IPage<KpiDef> page(KpiDef kpiDef, Query query);

	/**
	 * 
	 * @Function: IKpiDefService::listAll
	 * @Description: 查询所有
	 * @param kpiDef
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午8:20:58 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<KpiDef> listAll(KpiDef kpiDef);

	/**
	 * 
	 * @Function: IKpiDefService::removeKpiDef
	 * @Description: 逻辑删除
	 * @param longList
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午8:21:07 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean removeKpiDef(List<Long> longList);

	Integer countKpiDef(KpiDef kpiDef);

}
