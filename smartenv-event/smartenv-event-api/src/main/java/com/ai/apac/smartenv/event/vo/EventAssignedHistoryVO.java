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
package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventAssignedHistoryVO对象", description = "EventAssignedHistoryVO对象")
public class EventAssignedHistoryVO extends EventAssignedHistory {
	private static final long serialVersionUID = 1L;

	private String checkResultName;

	private List<EventMediumVO> eventMediumVOList;
}
