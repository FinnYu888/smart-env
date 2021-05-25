package com.ai.apac.core.log.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.log.model.LogApi;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/30 9:17 下午
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "LogApiVO对象", description = "LogApiVO对象")
public class LogApiVO extends LogApi {

    private static final long serialVersionUID = 1L;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 租户名称
     */
    private String tenantName;
}
