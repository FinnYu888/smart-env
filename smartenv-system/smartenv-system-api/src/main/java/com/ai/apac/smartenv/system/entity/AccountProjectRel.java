package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author qianlong
 * @since 2020-11-26
 */
@Data
@TableName("ai_account_project_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AccountProjectRel对象", description = "AccountProjectRel对象")
public class AccountProjectRel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 登录帐号ID
     */
    @ApiModelProperty(value = "登录帐号ID")
    private Long accountId;
    /**
     * 登录帐号
     */
    @ApiModelProperty(value = "登录帐号")
    private String account;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    /**
     * 是否是默认项目
     */
    @ApiModelProperty(value = "是否是默认项目")
    private Integer isDefault;
}
