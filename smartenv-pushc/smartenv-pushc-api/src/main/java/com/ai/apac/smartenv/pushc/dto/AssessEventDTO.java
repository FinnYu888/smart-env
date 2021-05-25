package com.ai.apac.smartenv.pushc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 考核事件DTO对象
 * @Date 2020/10/14 9:15 上午
 **/
@Data
public class AssessEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("微信用户唯一身份ID")
    private String unionId;

    @ApiModelProperty("微信公众号身份ID")
    private String mpOpenId;

    @ApiModelProperty("考核事件信息对象")
    private EventInfoDTO eventInfoDTO;
}
