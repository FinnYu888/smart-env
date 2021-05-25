package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 实体类
 *
 * @author qianlong
 * @since 2020-11-26
 */
@Data
@TableName("ai_company")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Company对象", description = "Company对象")
public class Company extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 上级公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上级公司ID")
    private Long parentId;

    /**
     * 公司全称
     */
    @ApiModelProperty(value = "公司全称")
    private String fullName;
    /**
     * 公司简称
     */
    @ApiModelProperty(value = "公司简称")
    private String shortName;
    /**
     * 公司地址
     */
    @ApiModelProperty(value = "公司地址")
    private String address;
    /**
     * 所在城市编码
     */
    @ApiModelProperty(value = "所在城市编码")
    private Long cityId;
    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    private String ownerName;
    /**
     * 负责人手机
     */
    @ApiModelProperty(value = "负责人手机")
    private String mobile;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;
    /**
     * 办公室电话
     */
    @ApiModelProperty(value = "办公室电话")
    private String officePhone;
    /**
     * 公司规模
     */
    @ApiModelProperty(value = "公司规模")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Integer companySize;
    /**
     * 营业执照URL
     */
    @ApiModelProperty(value = "营业执照URL")
    private String businessLicenseUrl;
    /**
     * 公司法人
     */
    @ApiModelProperty(value = "公司法人")
    private String legalPerson;
}
