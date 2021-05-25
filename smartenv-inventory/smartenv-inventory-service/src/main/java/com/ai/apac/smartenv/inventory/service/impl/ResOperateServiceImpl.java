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
package com.ai.apac.smartenv.inventory.service.impl;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.inventory.entity.*;
import com.ai.apac.smartenv.inventory.mapper.ResOperateMapper;
import com.ai.apac.smartenv.inventory.service.*;
import com.ai.apac.smartenv.inventory.vo.ResOperateQueryVO;
import com.ai.apac.smartenv.inventory.vo.ResOperateVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-27
 */
@Service
public class ResOperateServiceImpl extends BaseServiceImpl<ResOperateMapper, ResOperate> implements IResOperateService {

	@Autowired
	IResOrderService orderService;
	@Autowired
	IResManageService manageService;
	@Autowired
	IResOrderDtlService orderDtlService;


	@Override
	public IPage<ResOperateVO> selectResOperatePage(IPage<ResOperateVO> page, ResOperateVO resOperate) {
		return page.setRecords(baseMapper.selectResOperatePage(page, resOperate));
	}

	@Override
	public IPage<ResOperateQuery> listResOperatorPage(IPage<ResOperateQuery> page, QueryWrapper<ResOperateVO> resOperate) {
		resOperate.eq("oper.is_deleted",0);
		return  page.setRecords(baseMapper.queryResOperatePage(page,resOperate));
	}




}
