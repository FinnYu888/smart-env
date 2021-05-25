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

import com.ai.apac.smartenv.event.entity.EventKpiTplDef;
import com.ai.apac.smartenv.event.entity.EventKpiTplRel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考核指标定义表视图实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventKpiTplDefVO对象", description = "事件考核模板定义表")
public class EventKpiTplDefVO extends EventKpiTplDef {

	private static final long serialVersionUID = 1L;

	private String positionName;

	private String statusName;


}
