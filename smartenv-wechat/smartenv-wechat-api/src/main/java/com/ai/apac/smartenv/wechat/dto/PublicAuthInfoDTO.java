package com.ai.apac.smartenv.wechat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 公众用户登录鉴权结果
 * @Date 2020/12/31 2:07 下午
 **/
@Data
public class PublicAuthInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String unionId;

    private String appOpenId;

    private String token;
}
