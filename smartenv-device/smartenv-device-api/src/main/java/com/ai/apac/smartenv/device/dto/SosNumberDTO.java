package com.ai.apac.smartenv.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description SOS号码对象
 * @Date 2020/5/21 3:37 下午
 **/
@Data
public class SosNumberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户号码")
    private String phoneNumber;

    @ApiModelProperty("优先级")
    private Integer priority;

    public SosNumberDTO(String phoneNumber,Integer priority){
        this.phoneNumber = phoneNumber;
        this.priority = priority;
    }
}
