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

import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 * 考核指标定义表 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IEventKpiDefService extends BaseService<EventKpiDef> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @return
	 */
	IPage<EventKpiDefVO> selectEventKpiDefPage(IPage<EventKpiDefVO> page, EventKpiDefVO eventKpiDef);

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
	boolean saveEventKpiDef(EventKpiDef eventKpiDef);

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
	boolean updateEventKpiDefById(EventKpiDef eventKpiDef);

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
	IPage<EventKpiDef> page(EventKpiDef eventKpiDef, Query query);


	EventKpiDefVO getEventKpiDef(Long id);

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
	List<EventKpiDef> listAll(EventKpiDef eventKpiDef);

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
	boolean removeEventKpiDef(List<Long> longList);

	Integer countEventKpiDef(EventKpiDef eventKpiDef);

    List<EventKpiCatalogVO> treeKpiDefByPersonId(Long personId);

    List<EventKpiCatalogVO> getKpiDefByEventId(Long eventId);
}
