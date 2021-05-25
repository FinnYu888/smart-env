/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.auth.support;

import com.ai.apac.smartenv.auth.service.BladeUserDetails;
import com.ai.apac.smartenv.auth.utils.TokenUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt返回参数增强
 *
 * @author Chill
 */
public class BladeJwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        BladeUserDetails principal = (BladeUserDetails) authentication.getUserAuthentication().getPrincipal();
        Map<String, Object> info = new HashMap<>(20);
        info.put(TokenUtil.CLIENT_ID, TokenUtil.getClientIdFromHeader());
        info.put(TokenUtil.USER_ID, Func.toStr(principal.getUserId()));
        info.put(TokenUtil.DEPT_ID, Func.toStr(principal.getDeptId()));
        info.put(TokenUtil.ROLE_ID, Func.toStr(principal.getRoleId()));
        info.put(TokenUtil.TENANT_ID, principal.getTenantId());
        info.put(TokenUtil.TENANT_NAME, principal.getTenantName());
        info.put(TokenUtil.PLATFORM_NAME, principal.getPlatformName());
        info.put(TokenUtil.TENANT_CITY, principal.getCityId());
        info.put(TokenUtil.TENANT_CITY_NAME, principal.getCityName());
        info.put(TokenUtil.TENANT_CITY_LON, principal.getLon());
        info.put(TokenUtil.TENANT_CITY_LAT, principal.getLat());
        info.put(TokenUtil.ACCOUNT, principal.getAccount());
        info.put(TokenUtil.USER_NAME, principal.getUsername());
        info.put(TokenUtil.NICK_NAME, principal.getName());
        info.put(TokenUtil.REAL_NAME, principal.getRealName());
        info.put(TokenUtil.ROLE_NAME, principal.getRoleName());
        info.put(TokenUtil.ROLE_GROUP, principal.getRoleGroup());
        info.put(TokenUtil.AVATAR, principal.getAvatar());
        info.put(TokenUtil.PROJECT_LIST, principal.getProjectList());
        info.put(TokenUtil.LICENSE, TokenUtil.LICENSE_NAME);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
