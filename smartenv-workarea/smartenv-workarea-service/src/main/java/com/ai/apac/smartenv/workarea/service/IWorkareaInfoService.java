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
package com.ai.apac.smartenv.workarea.service;

import com.ai.apac.smartenv.workarea.entity.WorkareaDetail;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.vo.UserVO;
import com.ai.apac.smartenv.workarea.vo.WorkareaInfoVO;
import com.ai.apac.smartenv.workarea.vo.WorkareaViewVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;

import java.io.IOException;
import java.util.List;

/**
 * 工作区域信息 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IWorkareaInfoService extends BaseService<WorkareaInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param workareaInfo
	 * @return
	 */
	IPage<WorkareaInfoVO> selectWorkareaInfoPage(IPage<WorkareaInfoVO> page, WorkareaInfoVO workareaInfo);

	boolean saveOrUpdateDetail(WorkareaDetail workareaDetail) throws ServiceException;

	boolean workAreaInfo2BigData(String tenantId) throws IOException;

	boolean removeAllInfo(List<Long> ids) throws ServiceException;

	List<WorkareaInfoVO> getWorkAreaInfoPages(WorkareaRel workareaRel, WorkareaInfo workareaInfo) throws ServiceException;

	List<WorkareaViewVO> getAreaListByPersonId(String entityId, String tenantId) throws ServiceException;

	WorkareaInfo selectHWorkareaInfo(WorkareaInfo workareaInfo) throws ServiceException;
	/**
	 * 路线或区域绑定车辆或人员
	 *
	 * @param workareaRelList
	 * @return
	 */
	boolean bindOrUnbind(List<WorkareaRel> workareaRelList, BladeUser bladeUser) throws ServiceException;

	/**
	 * 车辆或人员绑定路线或区域
	 *
	 * @param workareaIds,workareaRel
	 * @return
	 */
	boolean bindWorkareas(List<String> workareaIds,WorkareaRel workareaRel) throws ServiceException;
	/**
	 * 车辆或人员重新绑定路线或区域
	 *
	 * @param ids,workareaRel
	 * @return
	 */
	boolean reBindWorkarea(List<String> ids,WorkareaRel workareaRel) throws ServiceException;
	/**
	 * 车辆或人员解绑区域或路线
	 *
	 * @param ids,workareaRel
	 * @return
	 */
	boolean unbindWorkareas(List<String> ids,WorkareaRel workareaRel) throws ServiceException;

	/**
	 *
	 * @Function: IWorkareaInfoService::unbindWorkarea
	 * @Description: 根据实体id和type解绑
	 * @param entityId
	 * @param entityType
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 上午10:40:06
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Boolean unbindWorkarea(Long entityId, Long entityType);

	Boolean syncDriverWorkArea(Long entityId,Long personId,String flag, BladeUser bladeUser);

	List<UserVO> eventPerson(String workareaId);

	/**
	 * 因为车辆路线变动而粗发的驾驶员的路线变动
	 *
	 * @param entityId
	 * @param workAreaId
	 * @param flag
	 */
	void syncDriverWorkAreaRel(Long entityId, Long workAreaId, String flag);

	void syncDriverWorkAreaRelAsync(Long entityId, Long workAreaId, String flag, BladeUser bladeUser);

    Boolean addWorkareaInfoByTrack(WorkareaInfo workareaInfo, Long entityType, Long entityId, Long startTime, Long endTime) throws Exception;

    Boolean batchChangeRegion4WorkArea(List<Long> areaIds, Long targetRegionId,String regionManager);

}
