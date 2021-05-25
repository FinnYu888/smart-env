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
package com.ai.apac.smartenv.green.service.impl;

import com.ai.apac.smartenv.green.entity.GreenareaItem;
import com.ai.apac.smartenv.green.vo.GreenareaItemVO;
import com.ai.apac.smartenv.green.mapper.GreenareaItemMapper;
import com.ai.apac.smartenv.green.service.IGreenareaItemService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 绿化养护项信息 服务实现类
 *
 * @author Blade
 * @since 2020-07-22
 */
@Service
public class GreenareaItemServiceImpl extends BaseServiceImpl<GreenareaItemMapper, GreenareaItem> implements IGreenareaItemService {

	@Override
	public IPage<GreenareaItemVO> selectGreenareaItemPage(IPage<GreenareaItemVO> page, GreenareaItemVO greenareaItem) {
		return page.setRecords(baseMapper.selectGreenareaItemPage(page, greenareaItem));
	}

}
