package com.ai.apac.core.log.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.log.model.LogError;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: LogErrorQueryVO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/7/1
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/1     zhaidx           v1.0.0               修改原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "LogErrorQueryVO", description = "LogErrorQueryVO")
public class LogErrorQueryDTO extends LogError {
    Long startTime;
    
    Long endTime;
    
    Integer current;
    
    Integer size;
}
