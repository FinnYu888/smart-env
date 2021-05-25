package com.ai.apac.smartenv.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 人体节律数据
 * @Date 2020/9/16 9:24 上午
 **/
@ApiModel("人体节律数据")
@Data
public class BodyBiologicalDTO implements Serializable {

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("智力数据")
    private String intelligence;

    @ApiModelProperty("体力数据")
    private String physical;

    @ApiModelProperty("情绪数据")
    private String emotion;

    public BodyBiologicalDTO(String date, String intelligence, String physical, String emotion) {
        this.date = date;
        this.intelligence = intelligence;
        this.physical = physical;
        this.emotion = emotion;
    }

    public BodyBiologicalDTO(){}
}
