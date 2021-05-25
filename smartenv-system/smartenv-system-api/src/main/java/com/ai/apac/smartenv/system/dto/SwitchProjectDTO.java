package com.ai.apac.smartenv.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/30 6:13 下午
 **/
@Data
public class SwitchProjectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目编码", required = true)
    private String projectCode;
}
