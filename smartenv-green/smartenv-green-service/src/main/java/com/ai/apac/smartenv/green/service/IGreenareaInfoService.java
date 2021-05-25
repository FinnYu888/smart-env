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
package com.ai.apac.smartenv.green.service;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.green.dto.GreenareaInfoDTO;
import com.ai.apac.smartenv.green.entity.GreenareaInfo;
import com.ai.apac.smartenv.green.vo.GreenareaInfoVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.*;

import java.util.List;


/**
 * 绿化养护信息 服务类
 *
 * @author Blade
 * @since 2020-07-22
 */
public interface IGreenareaInfoService extends BaseService<GreenareaInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param greenareaInfo
	 * @return
	 */
	IPage<GreenareaInfoVO> selectGreenareaInfoPage(IPage<GreenareaInfoVO> page, GreenareaInfoVO greenareaInfo);

	Boolean saveGreenareaInfo(GreenareaInfoDTO greenareaInfoDTO);

	Boolean updateGreenareaInfo(GreenareaInfoDTO greenareaInfoDTO);

	IPage<GreenareaInfo> pageGreenareas(GreenareaInfo greenareaInfo, Query query);

	Boolean removeGreenareaInfo(List<Long> ids);

}
