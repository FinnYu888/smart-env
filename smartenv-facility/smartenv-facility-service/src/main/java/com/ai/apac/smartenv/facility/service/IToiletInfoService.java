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
package com.ai.apac.smartenv.facility.service;

import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *  服务类
 *
 * @author Blade
 * @since 2020-09-16
 */
public interface IToiletInfoService extends BaseService<ToiletInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param toiletInfo
	 * @return
	 */
	IPage<ToiletInfoVO> selectToiletInfoPage(IPage<ToiletInfoVO> page, ToiletInfoVO toiletInfo);

	ToiletInfoVO getToiletDetailsById(Long id);

	ToiletInfoVO getToiletViewById(Long id);

	/**
	 * 根据自定义条件查询公厕信息
	 * @param queryDTO
	 * @return
	 */
    List<ToiletInfoVO> listToiletInfosByCondition(ToiletQueryDTO queryDTO);


	Boolean thirdToiletInfoAsync(ToiletInfo toiletInfo, String actionType) ;

}
