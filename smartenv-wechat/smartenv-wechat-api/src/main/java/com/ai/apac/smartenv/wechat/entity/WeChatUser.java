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
package com.ai.apac.smartenv.wechat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;

/**
 * 实体类
 *
 * @author qianlong
 */
@Data
@TableName("ai_wechat_user")
@EqualsAndHashCode(callSuper = true)
public class WeChatUser extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 微信用户唯一id
	 */
	private String unionId;
	/**
	 * 小程序openId
	 */
	private String appOpenId;
	/**
	 * 公众号openId
	 */
	private String mpOpenId;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 手机
	 */
	private String mobile;

}
