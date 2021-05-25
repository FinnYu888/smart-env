package com.ai.apac.smartenv.workarea.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 事件上报下拉框视图类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@ApiModel(value = "WorkareaViewVO对象", description = "事件上报下拉框视图类")
public class WorkareaViewVO {
    private static final long serialVersionUID = 1L;

    private Long workareaId;

    private String workareaName;

    private Long areaType;

}
