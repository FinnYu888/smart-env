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

import com.ai.apac.smartenv.event.dto.EventKpiDefDTO;
import com.ai.apac.smartenv.event.dto.EventKpiTplDefDTO;
import com.ai.apac.smartenv.event.dto.EventKpiTplRelDTO;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.entity.EventKpiTplDef;
import com.ai.apac.smartenv.event.entity.EventKpiTplT;
import com.ai.apac.smartenv.event.vo.EventKpiDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplDefVO;
import com.ai.apac.smartenv.event.vo.EventKpiTplRelVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 考核指标定义表 服务类
 *
 * @author Blade
 * @since 2020-02-08
 */
public interface IEventKpiTplDefService extends BaseService<EventKpiTplDef> {

	/**
	 * 自定义分页查询事件指标模板
	 *
	 * @param page
	 * @return
	 */
	IPage<EventKpiTplDefVO> selectEventKpiTplDefPage(IPage<EventKpiTplDefVO> page, EventKpiTplDefVO eventKpiTplDef);


	/**
	 *更新事件指标模板
	 */
	boolean saveEventKpiTplDef(String eventKpiTplId);

	void updateTplRelsCache(String eventKpiTplId,List<EventKpiTplRelDTO> eventKpiTplRelDTOList);

	void updateTplInfoCache(EventKpiTplDefDTO eventKpiTplDefDTO);

	void deleteTplRelsCache(String eventKpiTplId,List<Long> eventKpiIdList);

	void updateTplThresholdCache(String eventKpiTplId,String eventKpiId,double threshold);

	List<EventKpiTplRelDTO> listEventKpiTplRel(Long eventKpiTplId);


	/**
	 * 删除事件指标模板
	 */
	void removeEventKpiTplDef(List<Long> idList);

	void updateTplDefStatus(List<Long> idList,Integer status);


	IPage<EventKpiTplDef> page(EventKpiTplDef eventKpiTplDef, Query query);


	EventKpiTplDefDTO getTplDefDetails(String eventKpiTplId);

	EventKpiTplDefDTO initTplDefCache(String eventKpiTplId);

	void delTplDefCache(String eventKpiTplId);

	void previewEventKpiTpl(String eventKpiTplId, HttpServletResponse response);

	EventKpiTplDefDTO getTplDefCache(String eventKpiTplId,String catalogId,String eventKpiName);


}
