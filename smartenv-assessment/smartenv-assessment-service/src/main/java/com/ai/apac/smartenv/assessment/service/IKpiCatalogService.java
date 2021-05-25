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

import com.ai.apac.smartenv.assessment.entity.KpiCatalog;
import com.ai.apac.smartenv.assessment.vo.KpiCatalogVO;

import java.util.List;

import javax.validation.Valid;

import com.ai.apac.smartenv.system.vo.DeptVO;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 考核指标分类 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IKpiCatalogService extends BaseService<KpiCatalog> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param kpiCatalog
	 * @return
	 */
	IPage<KpiCatalogVO> selectKpiCatalogPage(IPage<KpiCatalogVO> page, KpiCatalogVO kpiCatalog);

	/**
	 * 
	 * @Function: IKpiCatalogService::checkKpiCatalogName
	 * @Description: 校验名称是否已存在
	 * @param kpiCatalog
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午4:55:17 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean checkKpiCatalogName(KpiCatalog kpiCatalog);

	/**
	 * 
	 * @Function: IKpiCatalogService::page
	 * @Description: 分页查询
	 * @param kpiCatalog
	 * @param query
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午4:59:24 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	IPage<KpiCatalog> page(KpiCatalog kpiCatalog, Query query);

	/**
	 * 
	 * @Function: IKpiCatalogService::saveKpiCatalog
	 * @Description: 保存考核指标分类
	 * @param kpiCatalog
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午12:16:20 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean saveKpiCatalog(KpiCatalog kpiCatalog);

	/**
	 * 
	 * @Function: IKpiCatalogService::updateKpiCatalogById
	 * @Description: 更新考核指标分类
	 * @param kpiCatalog
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午12:19:46 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean updateKpiCatalogById(KpiCatalog kpiCatalog);

	/**
	 * 
	 * @Function: IKpiCatalogService::removeKpiCatalog
	 * @Description: 逻辑删除
	 * @param longList
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午3:45:46 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void removeKpiCatalog(List<Long> longList);

	/**
	 * 
	 * @Function: IKpiCatalogService::listAll
	 * @Description: 查询所有
	 * @param kpiCatalog
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午3:53:10 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<KpiCatalog> listAll(KpiCatalog kpiCatalog);

	List<KpiCatalogVO> tree();


}
