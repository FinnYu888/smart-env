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

import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.EventInfoMongoDto;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.vo.EventAllInfoVO;
import com.ai.apac.smartenv.event.vo.EventAssignedAllVO;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import com.ai.apac.smartenv.system.entity.Region;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;

import java.io.IOException;
import java.util.List;

/**
 * 事件基本信息表 服务类
 *
 * @author Blade
 * @since 2020-02-06
 */
public interface IEventInfoService extends BaseService<EventInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param eventInfo
	 * @return
	 */
	IPage<EventInfoVO> selectEventInfoPage(IPage<EventInfoVO> page, EventInfoVO eventInfo);

	boolean saveEventInfo(EventAllInfoVO eventInfoVO, Integer coordsType,Long personId) throws ServiceException;

	void sendNotice(EventInfo eventInfo, String userIds);

	void sendWechatMessage(List<Long> personIdList, EventInfo eventInfo);

	void eventMessage2Mongo(EventInfo eventInfo);

	boolean updateEventInfo(EventAllInfoVO eventInfoVO, Integer coordsType) throws ServiceException;

	boolean removeEventInfo(List<Long> ids) throws ServiceException;

	boolean updateEventInfoStatus(EventInfo eventInfo) throws ServiceException;

	IPage<EventInfoVO> selectEventInfList(EventInfoVO eventInfo, Query query, BladeUser user, Long personId);

	List<EventInfoVO> listEventInfoByParam(EventQueryDTO eventQueryDTO);

	/**
	 * 根据条件获取事件简要信息
	 *
	 * @param eventQueryDTO@return
	 */
	List<EventInfoVO> listEventInfoByCondition(EventQueryDTO eventQueryDTO);

	EventInfoVO getComplexEventDetail(String eventInfoId, Integer coordsType, BladeUser user) throws IOException;

	EventInfoVO getDetail(String eventInfoId) throws ServiceException;

	boolean reassign(EventAssignedHistory eventAssignedHistory) throws ServiceException;

	boolean eventCheck(EventAssignedAllVO eventAssignedHistory) throws ServiceException;

	Integer countEventDaily(EventInfo eventInfo) throws ServiceException;

	boolean rectification(EventAssignedAllVO eventAssignedHistory);

	List<EventTypeCountVO> countEventGroupByType(Integer days,String tenantId) throws ServiceException;

	/**
	* 查询最近事件
	* @author 66578
	*/
	GreenScreenEventsDTO queryEventInfos(String tenantId);

	/**
	* 更新mongo中事件信息
	* @author 66578
	*/
	public void updateEventInfoMongoDate(EventInfoMongoDto eventInfoVO);

	/**
	 * 根据地址查询地址所在片区信息
	 */
	Region getRegionByAddress(String lat, String lng,String tenantId);
}
