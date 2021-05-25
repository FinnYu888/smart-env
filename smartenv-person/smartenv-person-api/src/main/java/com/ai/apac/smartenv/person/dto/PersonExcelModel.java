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
package com.ai.apac.smartenv.person.dto;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: PersonExcelModel.java
 * @Description: 人员信息导出
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月19日 下午2:27:42 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月19日     zhaoaj           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonExcelModel extends BaseRowModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ExcelProperty(value = "姓名" ,index = 0)
	private String presonName;
	
	@ExcelProperty(value = "工号" ,index = 1)
	private String jobNumber;

	@ExcelProperty(value = "职位" ,index = 2)
	private String personPosition;

	@ExcelProperty(value = "部门" ,index = 3)
	private String deptName;

	@ExcelProperty(value = "联系电话" ,index = 4)
	private String mobileNumber;

	@ExcelProperty(value = "性别" ,index = 5)
	private String gender;

	@ExcelProperty(value = "入职日期" ,index = 6)
	private String entryDate;
}
