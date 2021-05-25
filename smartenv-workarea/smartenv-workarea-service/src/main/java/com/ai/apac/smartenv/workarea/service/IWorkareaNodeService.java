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

import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.vo.WorkareaNodeVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 工作区域节点信息 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IWorkareaNodeService extends BaseService<WorkareaNode> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param workareaNode
	 * @return
	 */
	IPage<WorkareaNodeVO> selectWorkareaNodePage(IPage<WorkareaNodeVO> page, WorkareaNodeVO workareaNode);



	List<WorkareaNodeVO> queryWorkareaNodeVOListByParam (WorkareaNode workareaNode, String tenantId);
}
