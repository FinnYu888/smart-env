package com.ai.apac.smartenv.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/10 9:41 上午
 **/
public class LoginInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    private String scope;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("tenant_name")
    private String tenantName;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("real_name")
    private String realName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("role_id")
    private String roleId;

    @JsonProperty("nick_name")
    private String nickName;

    @JsonProperty("dept_id")
    private String deptId;

    @JsonProperty("account")
    private String account;

    @JsonProperty("jti")
    private String jti;
}
