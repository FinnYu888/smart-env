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
package com.ai.apac.smartenv.person.vo;

import com.ai.apac.smartenv.person.entity.GroupMember;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dept;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 组成员信息表视图实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GroupMemberVO对象", description = "组成员信息表")
public class GroupMemberVO extends GroupMember {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "成员所属部门名称")
	private String memberDeptName;
	/**
	 * 成员所属职位
	 */
	@ApiModelProperty(value = "成员所属职位名称")
	private String memberPositionName;

	@ApiModelProperty(value = "成员所属职位名称")
	private String image;

}
