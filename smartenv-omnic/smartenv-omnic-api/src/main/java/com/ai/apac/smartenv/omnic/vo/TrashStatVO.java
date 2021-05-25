package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 垃圾分类收运分析
 * @Date 2020/8/27 10:42 上午
 **/
@Data
@ApiModel("垃圾分类收运分析")
public class TrashStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("垃圾分类类型")
    private String itemType;

    @ApiModelProperty("垃圾重量")
    private Double weight;

    @ApiModelProperty("日期")
    private String date;

    public TrashStatVO() {
    }

    public TrashStatVO(String itemType, Double weight, String date) {
        this.itemType = itemType;
        this.weight = weight;
        this.date = date;
    }
}
