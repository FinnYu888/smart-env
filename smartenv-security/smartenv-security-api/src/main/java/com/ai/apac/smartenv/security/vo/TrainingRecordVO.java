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
package com.ai.apac.smartenv.security.vo;

import com.ai.apac.smartenv.security.entity.TrainingRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 培训记录表视图实体类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrainingRecordVO对象", description = "培训记录表")
public class TrainingRecordVO extends TrainingRecord {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "图片附件")
	private List<TrainingAttachVO> picList;

	@ApiModelProperty(value = "文件附件")
	private List<TrainingAttachVO> docList;

	@ApiModelProperty(value = "培训对象")
	private List<TrainingObjectVO> objectList;

}
