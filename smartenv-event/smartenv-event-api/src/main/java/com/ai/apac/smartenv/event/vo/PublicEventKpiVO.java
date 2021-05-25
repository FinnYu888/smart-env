package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 3:55 下午
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PublicEventKpiVO对象", description = "PublicEventKpiVO对象")
public class PublicEventKpiVO extends PublicEventKpi {

    private static final long serialVersionUID = 1L;

}
