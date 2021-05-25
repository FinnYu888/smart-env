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
package com.ai.apac.smartenv.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 点创主动告警附件表实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@TableName("ai_minicreate_attach")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MinicreateAttach对象", description = "点创主动告警附件表")
public class MinicreateAttach extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 点创主动告警附件表主键id
	*/
		@ApiModelProperty(value = "点创主动告警附件表主键id")
		private Long id;
	/**
	* 点创GPS告警Id
	*/
		@ApiModelProperty(value = "点创GPS告警Id")
		private String uuid;
	/**
	* 报警编号
	*/
		@ApiModelProperty(value = "报警编号")
		private Long alarmId;
	/**
	* 附件数量
	*/
		@ApiModelProperty(value = "附件数量")
		private Long total;
	/**
	* 附件类型（00——图片；01——音频；02——视频；03——文本；04——其它,此处是十进制数）
	*/
		@ApiModelProperty(value = "附件类型（00——图片；01——音频；02——视频；03——文本；04——其它,此处是十进制数）")
		private Integer attType;
	/**
	* 文件大小
	*/
		@ApiModelProperty(value = "文件大小")
		private Integer size;
	/**
	* 文件名称
	*/
		@ApiModelProperty(value = "文件名称")
		private String fileName;
	/**
	* 文件名称
	*/
		@ApiModelProperty(value = "文件名称")
		private String fileUrl;


}
