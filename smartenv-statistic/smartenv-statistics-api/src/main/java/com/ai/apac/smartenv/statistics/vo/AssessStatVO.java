package com.ai.apac.smartenv.statistics.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/27 11:28 上午
 **/
@Data
@ApiModel("考核问题分析")
public class AssessStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("考核问题")
    private String itemType;

    @ApiModelProperty("考核问题数量")
    private Integer count;

    public AssessStatVO() {
    }

    public AssessStatVO(String itemType, Integer count) {
        this.itemType = itemType;
        this.count = count;
    }
}
