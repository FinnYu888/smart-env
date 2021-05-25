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
package com.ai.apac.smartenv.alarm.service;

import com.ai.apac.smartenv.alarm.dto.AlarmInfoCountDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleResultVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoVO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 告警基本信息表 服务类
 *
 * @author Blade
 * @since 2020-02-18
 */
public interface IAlarmInfoService extends BaseService<AlarmInfo> {

	/**
	 * 刷mongodb测试方法
	 * @return
	 */
    boolean pushDataToMongodb();

	/**
	 * 更新确认信息到mongodb
	 * @param alarmInfo
	 */
	void updateMongoDBByAlarmId(AlarmInfo alarmInfo);

	/**
	 * 更新主动告警data到mongodb
	 * @param alarmInfo
	 */
	void updateInitiativeAlarmToMongo(AlarmInfo alarmInfo);

	/**
	 * 大数据同步告警信息数据处理
	 * @param alarmInfo
	 * @return
	 */
	boolean handleBigDataAlarmInfo(AlarmInfo alarmInfo) throws Exception;

	/**
	 * 先从monggo取数据，没有再从数据库查
	 * @param id
	 * @return
	 */
    AlarmInfoHandleInfoVO detailByIdFromMongo(@NotNull Long id);

    /**
	 * 根据主键查告警信息
	 * @param id
	 * @return
	 */
	AlarmInfoHandleInfoVO detailById(@NotNull Long id);

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmInfo
	 * @return
	 */
	IPage<AlarmInfoVO> selectAlarmInfoPage(IPage<AlarmInfoVO> page, AlarmInfoVO alarmInfo);

	/**
	 * 自定义插入
	 * @param alarmInfo
	 * @return
	 */
    boolean insertNewAlarmInfo(AlarmInfo alarmInfo);

    /**
	 * 根据条件查询告警信息，并处理封装页面需要展示的字段，不分页
	 * @param alarmInfoQueryDTO
	 * @return
	 */
    List<AlarmInfoHandleInfoVO> listAlarmHandleInfoNoPage(AlarmInfoQueryDTO alarmInfoQueryDTO);

    /**
	 * 根据条件查询告警信息，并处理封装页面需要展示的字段，分页
	 * @param alarmInfoQueryDTO
	 * @param page
	 * @return
	 */
	IPage<AlarmInfoHandleInfoVO> listAlarmHandleInfoPage(AlarmInfoQueryDTO alarmInfoQueryDTO, Query page);

	/**
	 * 按条件统计告警数量
	 * @param alarmInfoQueryDTO
	 * @return
	 */
    Long countAlarmInfoByCondition(AlarmInfoQueryDTO alarmInfoQueryDTO);

    /**
	 * 导出车辆或者人员信息
	 * @param alarmInfoQueryDTO
	 */
    void exportAlarmInfo(AlarmInfoQueryDTO alarmInfoQueryDTO);

	/**
	 * 查询当天告警数量
	 * @return
	 */
	Integer countAlarmInfoAmount(AlarmInfo alarmInfo);

    Integer countAlarmInfoAmountByEntityIds(AlarmInfoCountDTO alarmInfoCountDTO);

	List<AlarmInfoScreenViewVO> getLastAlarmInfosDaily(Long nums,String tenantId);

	void batchHandle(AlarmInfoHandleResultVO alarmInfoHandleResultVO);

	/**
	 * 消费点创kafka主动安全告警
	 */
	void consumeInitiativeAlarm();

	/**
	 * 处理主动告警消息
	 * @param initiativeAlarmInfo
	 */
	void handlerInitiativeAlarm(JSONObject initiativeAlarmInfo) throws Exception;
}
