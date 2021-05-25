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

import com.ai.apac.smartenv.alarm.entity.MinicreateAttach;
import com.ai.apac.smartenv.alarm.vo.MinicreateAttachVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 点创主动告警附件表 服务类
 *
 * @author Blade
 * @since 2020-09-10
 */
public interface IMinicreateAttachService extends BaseService<MinicreateAttach> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param minicreateAttach
	 * @return
	 */
	IPage<MinicreateAttachVO> selectMinicreateAttachPage(IPage<MinicreateAttachVO> page, MinicreateAttachVO minicreateAttach);

	/**
	 * 根据条件查询附件信息
	 * @param attachVO
	 * @return
	 */
    List<MinicreateAttachVO> listAttachByCondition(MinicreateAttachVO attachVO);
}
