package com.ai.apac.smartenv.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/28 5:42 下午
 **/
@Data
public class SimpleDictVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典值")
    private Integer dictKey;

    @ApiModelProperty("字典值描述")
    private String dictValue;
}
