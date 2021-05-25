package com.ai.apac.smartenv.pushc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;

/**
 * @author qianlong
 * @description Email消息对象
 * @Date 2020/5/24 7:54 下午
 **/
@Data
public class EmailDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("邮件标题")
    private String subject;

    @ApiModelProperty("邮件收件人")
    private String receiver;

    @ApiModelProperty("邮件抄送人")
    private String cc;

    @ApiModelProperty("邮件内容")
    private String content;

    @ApiModelProperty("租户ID")
    private String tenantId;
}
