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
package com.ai.apac.smartenv.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Data
@TableName("ai_res_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ResInfo对象", description = "ResInfo对象")
public class ResInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long resType;
	private String serialNumber;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long batchId;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long resSpecId;
	@JsonSerialize(using = ToStringSerializer.class)
	private String inventoryId;
	private Long reservationRecipient;
	private String reservationRecipientType;
	private Timestamp reservedValidDate;
	private Timestamp reservedExpireDate;
	private Integer amount;
	private String unitPrice;
	private Timestamp storageTime;
	private Timestamp usedTime;
	private String manageState;
	private String manageStateReasonDesc;
	private String remark;


}
