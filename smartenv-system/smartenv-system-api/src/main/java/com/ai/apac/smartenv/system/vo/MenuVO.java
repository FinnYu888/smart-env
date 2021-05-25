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
package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Menu;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MenuVO对象", description = "MenuVO对象")
public class MenuVO extends Menu implements INode {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty("主键ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 父节点ID
	 */
	@ApiModelProperty("父节点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long parentId;

	/**
	 * 子孙节点
	 */
	@ApiModelProperty("子孙节点")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<INode> children;

	@Override
	public List<INode> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		return this.children;
	}

	/**
	 * 上级菜单
	 */
	@ApiModelProperty("上级菜单")
	private String parentName;

	/**
	 * 菜单类型
	 */
	@ApiModelProperty("菜单类型")
	private String categoryName;

	/**
	 * 按钮功能
	 */
	@ApiModelProperty("按钮功能")
	private String actionName;

	/**
	 * 是否新窗口打开
	 */
	@ApiModelProperty("是否新窗口打开")
	private String isOpenName;

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		final MenuVO menuVO = (MenuVO) obj;
		if (this == menuVO) {
			return true;
		} else {
			return this.id.equals(menuVO.id);
		}
	}

	@Override
	public int hashCode() {
		int hashno = 7;
		hashno = 13 * hashno + (id == null ? 0 : id.hashCode());
		return hashno;
	}

}
