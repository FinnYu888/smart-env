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

import com.ai.apac.smartenv.person.entity.Group;
import com.ai.apac.smartenv.person.entity.GroupMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * 组信息表视图实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GroupVO对象", description = "组信息表")
public class GroupVO extends Group {
	private static final long serialVersionUID = 1L;

	private String createUserName;

	private List<GroupMemberVO> groupMemberVOList;

}
