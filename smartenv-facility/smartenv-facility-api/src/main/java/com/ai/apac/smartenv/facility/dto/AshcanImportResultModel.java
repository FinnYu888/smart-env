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
package com.ai.apac.smartenv.facility.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AshcanImportResultModel extends BaseRowModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ExcelProperty(value = "垃圾桶名称" ,index = 0)
	private String ashcanCode;
	
	@ExcelProperty(value = "垃圾桶类型" ,index = 1)
	private String ashcanType;

	@ExcelProperty(value = "垃圾桶大小" ,index = 2)
	private String capacity;

	@ExcelProperty(value = "是否支持安装终端" ,index = 3)
	private String supportDevice;

	@ExcelProperty(value = "规划路线/区域" ,index = 4)
	private String workarea;

	@ExcelProperty(value = "垃圾桶地址" ,index = 5)
	private String address;


	@ExcelProperty(value = "所属部门" ,index = 7)
	private String dept;

	
	@ExcelProperty(value = "状态" ,index = 8)
	private String status;

	@ExcelProperty(value = "原因" ,index = 9)
	private String reason;

}
