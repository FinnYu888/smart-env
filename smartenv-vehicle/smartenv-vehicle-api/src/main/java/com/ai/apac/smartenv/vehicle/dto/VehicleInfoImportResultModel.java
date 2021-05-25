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
package com.ai.apac.smartenv.vehicle.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: VehicleInfoImportResultModel.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月21日 下午3:10:12 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月21日     zhaoaj           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleInfoImportResultModel extends BaseRowModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ExcelProperty(value = "车牌号" ,index = 0)
	private String plateNumber;
	
	@ExcelProperty(value = "车辆类型" ,index = 1)
	private String entityCategoryId;

	@ExcelProperty(value = "所属部门" ,index = 2)
	private String deptId;

	@ExcelProperty(value = "加入日期" ,index = 3)
	private String deptAddTime;

	@ExcelProperty(value = "状态" ,index = 4)
	private String status;

	@ExcelProperty(value = "原因" ,index = 5)
	private String reason;
}
