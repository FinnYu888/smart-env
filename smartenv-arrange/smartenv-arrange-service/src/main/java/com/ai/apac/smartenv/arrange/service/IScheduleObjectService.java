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
package com.ai.apac.smartenv.arrange.service;

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectTimeVO;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 敏捷排班表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IScheduleObjectService extends BaseService<ScheduleObject> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param scheduleObject
	 * @return
	 */
	IPage<ScheduleObjectVO> selectScheduleObjectPage(IPage<ScheduleObjectVO> page, ScheduleObjectVO scheduleObject);

	/**
	 * 
	 * @Function: IScheduleObjectService::refreshArrange
	 * @Description: 排班某一天修改后，重新计算开始时间和结束时间
	 * @param entityId
	 * @param entityType
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月15日 上午10:56:25 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void refreshArrange(Long entityId, String entityType);

	/**
	 * 
	 * @Function: IScheduleObjectService::deleteLogicSameObject
	 * @Description: 删除有排班的一天
	 * @param scheduleObject
	 * @param everyday
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月15日 上午11:03:35 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
//	void deleteLogicSameObject(ScheduleObjectVO scheduleObject, LocalDate everyday);

	/**
	 * 
	 * @Function: IScheduleObjectService::listUnfinishScheduleByEntity
	 * @Description: 查询未完成的考勤
	 * @param entityId
	 * @param entityType
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月20日 下午8:07:18 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<ScheduleObject> listUnfinishScheduleByEntity(Long entityId, String entityType);

	/**
	 * 
	 * @Function: IScheduleObjectService::checkNowNeedWork
	 * @Description: 校验是否需要工作
	 * @param entityId
	 * @param entityType
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月20日 下午8:30:48 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Boolean checkNeedWork(Long entityId, String entityType, LocalDateTime checkTime);

	/**
	 * 
	 * @Function: IScheduleObjectService::removeArrange
	 * @Description: 删除考勤
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @param qScheduleObjects 
	 * @date: 2020年2月22日 下午4:40:16 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean removeArrange(List<QScheduleObject> qScheduleObjects);


	/**
	 * 
	 * @Function: IScheduleObjectService::submitArrange
	 * @Description: 设置考勤
	 * @param scheduleObject
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @param bladeUser 
	 * @date: 2020年2月22日 下午4:49:25 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void submitArrange(ScheduleObjectVO scheduleObject, BladeUser bladeUser);

	int checkArrange(ScheduleObjectVO scheduleObject);

	/**
	 * 
	 * @Function: IScheduleObjectService::getArrange
	 * @Description: 查询月份考勤
	 * @param entityId
	 * @param month
	 * @param entityType
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月24日 下午1:46:50 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<ScheduleObjectVO> getArrange(Long entityId, String month, String entityType);

	List<ScheduleObject> listAllByVO(ScheduleObjectVO scheduleObject);

	/**
	 * 
	 * @Function: IScheduleObjectService::changeScheduleObject
	 * @Description: 临时调班
	 * @param scheduleObject
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月25日 下午2:14:36 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void changeScheduleObject(ScheduleObjectVO scheduleObject);

	/**
	 * 
	 * @Function: IScheduleObjectService::listScheduleObjectTimeByDate
	 * @Description: 获取当天考勤信息
	 * @param localDate
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月29日 下午4:30:40 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<ScheduleObjectTimeVO> listScheduleObjectTimeByDate(LocalDate localDate);

	/**
	 * 
	 * @Function: IScheduleObjectService::unbindSchedule
	 * @Description: 根据实体id和类型解绑
	 * @param entityId
	 * @param entityType
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 上午10:50:41 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Boolean unbindSchedule(Long entityId, String entityType);

	/**
	 * 
	 * @Function: IScheduleObjectService::countForToday
	 * @Description: 当天上班数
	 * @param vehicle
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午2:39:38 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Integer countForToday(String entityType,String tenantId, List<Long> entityIdList);

    List<ScheduleObject> listForTime(Date time, String entityType, String tenantId, List<Long> entityIdList);

    List<ScheduleObject> listForToday(String entityType, String tenantId, List<Long> entityIdList);

	Boolean checkTodayNeedWork(Long entityId, String entityType);

	/**
	 * 
	 * @Function: IScheduleObjectService::pageByDate
	 * @Description: 分页查询，应出勤的
	 * @param scheduleDate
	 * @param entityType
	 * @param tenantId
	 * @param entityIdList
	 * @param query
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月25日 下午4:16:42 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	IPage<ScheduleObject> pageByDate(LocalDate scheduleDate, String entityType, String tenantId, List<Long> entityIdList, Query query);

	/**
	 * 
	 * @Function: IScheduleObjectService::countByDate
	 * @Description: 应出勤的数量
	 * @param scheduleDate
	 * @param entityType
	 * @param tenantId
	 * @param entityIdList
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月25日 下午4:25:31 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Integer countByDate(LocalDate scheduleDate, String entityType, String tenantId, List<Long> entityIdList);

	IPage<QScheduleObjectVO> listArrange(QScheduleObjectVO qScheduleObject, Query query, boolean isHistory);

	void updateArrange(QScheduleObjectVO qScheduleObject, BladeUser bladeUser);

	void syncArrangeToBigData(List<ScheduleObject> bigDataList, String optFlag, Schedule schedule);

	HashSet<Long> checkScheduleObjectSet(ScheduleObjectVO scheduleObject);

	List<LocalDate> getBetweenDate(LocalDate scheduleBeginTime, LocalDate scheduleEndTime);

	ScheduleObject getByIdWithDel(Long scheduleObjectId);

}
