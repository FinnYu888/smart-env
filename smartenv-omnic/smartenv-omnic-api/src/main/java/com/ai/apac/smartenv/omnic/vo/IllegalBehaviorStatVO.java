package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 作业违规分析
 * @Date 2020/8/26 5:03 下午
 **/
@Data
@ApiModel("作业违规分析对象")
public class IllegalBehaviorStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("行为名称")
    private String item;

    @ApiModelProperty("数量")
    private String value;

    @ApiModelProperty("日期")
    private String group;

    public IllegalBehaviorStatVO(String item, String value, String group) {
        this.item = item;
        this.value = value;
        this.group = group;
    }
}
