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

import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * 考核指标分类视图实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventKpiCatalogVO对象", description = "事件指标分类")
public class EventKpiCatalogVO extends EventKpiCatalog implements INode{
	private static final long serialVersionUID = 1L;

	private String parentName;

	private Integer eventKpiCount;

	private List<EventKpiDefVO> eventKpiDefVOList;

	/**
	 * 子孙节点
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<INode> children;

	@Override
	public List<INode> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		return this.children;
	}
}
