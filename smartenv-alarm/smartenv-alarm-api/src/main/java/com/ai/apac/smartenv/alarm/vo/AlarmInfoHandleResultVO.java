package com.ai.apac.smartenv.alarm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: AlarmInfoHandleResultVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/18     zhaidx           v1.0.0               修改原因
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AlarmInfoHandleResultVO", description = "告警信息处理结果对象")
public class AlarmInfoHandleResultVO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "告警信息id，多个以逗号隔开")
    private String alarmInfoIds;
    
    @ApiModelProperty(value = "告警校对，正常告警：1， 错误告警：2，其他：3")
    private Integer alarmCheck;
    
    @ApiModelProperty(value = "备注")
    private String checkRemark;
    
    @ApiModelProperty(value = "告警信息发送方式，邮件：1，微信：2，后台通知：3，手表：4")
    private String informTypes;
}
