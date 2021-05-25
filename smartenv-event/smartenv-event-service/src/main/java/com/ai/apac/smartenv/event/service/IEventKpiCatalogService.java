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
package com.ai.apac.smartenv.event.service;

import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 * 考核指标分类 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IEventKpiCatalogService extends BaseService<EventKpiCatalog> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @return
	 */
	IPage<EventKpiCatalogVO> selectEventKpiCatalogPage(IPage<EventKpiCatalogVO> page, EventKpiCatalogVO eventKpiCatalogVO);

	/**
	 *
	 * @Function: IKpiCatalogService::checkKpiCatalogName
	 * @Description: 校验名称是否已存在
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午4:55:17
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean checkEventKpiCatalogName(EventKpiCatalog eventKpiCatalog);

	/**
	 *
	 * @Function: IKpiCatalogService::page
	 * @Description: 分页查询
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
	IPage<EventKpiCatalog> page(EventKpiCatalog eventKpiCatalog, Query query);

	/**
	 *
	 * @Function: IKpiCatalogService::saveKpiCatalog
	 * @Description: 保存考核指标分类
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午12:16:20
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean saveEventKpiCatalog(EventKpiCatalog eventKpiCatalog);

	/**
	 *
	 * @Function: IKpiCatalogService::updateKpiCatalogById
	 * @Description: 更新考核指标分类
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午12:19:46
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean updateEventKpiCatalogById(EventKpiCatalog eventKpiCatalog);

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
	void removeEventKpiCatalog(List<Long> longList);

	/**
	 *
	 * @Function: IKpiCatalogService::listAll
	 * @Description: 查询所有
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月5日 下午3:53:10
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<EventKpiCatalog> listAll(EventKpiCatalog eventKpiCatalog);

	List<EventKpiCatalogVO> tree(Long eventKpiTplId);

    List<EventKpiCatalogVO> treeNoMergerd(Long eventKpiTplId);

    void getChildCatalogIdList(Long pId, List<Long> catalogIdList);


}
