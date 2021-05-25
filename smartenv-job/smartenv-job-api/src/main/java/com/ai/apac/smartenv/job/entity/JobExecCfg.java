package com.ai.apac.smartenv.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("ai_job_exec_cfg")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "JobExecCfg对象", description = "JOB配置信息表")
public class JobExecCfg  extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 事件基本信息表主键id
     */
    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "JOB名称")
    private String jobName;

    @ApiModelProperty(value = "参数1")
    private String param1;

    @ApiModelProperty(value = "参数2")
    private String param2;

    @ApiModelProperty(value = "参数3")
    private String param3;

    @ApiModelProperty(value = "参数4")
    private String param4;

    @ApiModelProperty(value = "参数5")
    private String param5;

    @ApiModelProperty(value = "参数6")
    private String param6;

    @ApiModelProperty(value = "JOB执行时间")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date executeTime;

}
