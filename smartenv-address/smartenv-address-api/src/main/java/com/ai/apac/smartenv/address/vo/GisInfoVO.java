package com.ai.apac.smartenv.address.vo;

import com.ai.apac.smartenv.address.entity.GisInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/27 2:13 下午
 **/
@Data
@ApiModel
public class GisInfoVO extends GisInfo {

    private String fullAreaName;
}
