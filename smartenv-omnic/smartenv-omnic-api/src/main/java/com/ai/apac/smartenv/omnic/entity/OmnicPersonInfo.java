package com.ai.apac.smartenv.omnic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OmnicPersonInfo
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/20  10:05    panfeng          v1.0.0             修改原因
 */
@Data
public class OmnicPersonInfo {


    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;
    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNumber;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String personName;
    /**
     * 员工所属部门
     */
    @ApiModelProperty(value = "员工所属部门")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personDeptId;
    /**
     * 员工所属职位
     */
    @ApiModelProperty(value = "员工所属职位")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personPositionId;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private Long mobileNumber;
    /**
     * 微信ID
     */
    @ApiModelProperty(value = "微信ID")
    private String wechatId;
    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;
    /**
     * 入职时间
     */
    @ApiModelProperty(value = "入职时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date entryTime;
    /**
     * 离职时间
     */
    @ApiModelProperty(value = "离职时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;
    /**
     * 工作年限
     */
    @ApiModelProperty(value = "工作年限")
    private Long workYear;
    /**
     * 人员头像
     */
    @ApiModelProperty(value = "人员头像")
    private String image;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer gender;
    /**
     * 实体分类
     */
    @ApiModelProperty(value = "实体分类")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long entityCategoryId;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idCard;
    /**
     * 证件类型
     */
    @ApiModelProperty(value = "证件类型")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer idCardType;
    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 学历
     */
    @ApiModelProperty(value = "学历")
    private String education;
    /**
     * 婚姻状况
     */
    @ApiModelProperty(value = "婚姻状况")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer maritalStatus;
    /**
     * 是否在职
     */
    @ApiModelProperty(value = "是否在职")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer isIncumbency;
    /**
     * 劳动合同
     */
    @ApiModelProperty(value = "劳动合同")
    private String laborContract;
    /**
     * 合同类型
     */
    @ApiModelProperty(value = "合同类型")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer contractType;
    /**
     * 合同开始日期
     */
    @ApiModelProperty(value = "合同开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractStart;
    /**
     * 合同结束日期
     */
    @ApiModelProperty(value = "合同结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEnd;
    /**
     * 社会保险(0:未缴纳，1:缴纳)
     */
    @ApiModelProperty(value = "社会保险(0:未缴纳，1:缴纳)")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer socialInsurance;
    /**
     * 社保编号
     */
    @ApiModelProperty(value = "社保编号")
    private String socialInsuranceNumber;
    /**
     * 住房公积金(0:未缴纳，1:缴纳)
     */
    @ApiModelProperty(value = "住房公积金(0:未缴纳，1:缴纳)")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer providentFund;
    /**
     * 公积金编号
     */
    @ApiModelProperty(value = "公积金编号")
    private String providentFundNumber;
    /**
     * 政治面貌
     */
    @ApiModelProperty(value = "政治面貌")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer politicalKind;




}
