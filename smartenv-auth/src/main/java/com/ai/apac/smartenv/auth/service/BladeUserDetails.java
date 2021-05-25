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
package com.ai.apac.smartenv.auth.service;

import com.ai.apac.smartenv.system.dto.ProjectDTO;
import com.ai.apac.smartenv.system.dto.SimpleProjectDTO;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 用户信息拓展
 *
 * @author Chill
 */
@Getter
public class BladeUserDetails extends User {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 平台名称
     */
    private String platformName;
    /**
     * 昵称
     */
    private String name;
    /**
     * 真名
     */
    private String realName;
    /**
     * 账号
     */
    private String account;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色组
     */
    private String roleGroup;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 城市ID
     */
    private String cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lon;

    /**
     * 项目信息
     */
    private List<SimpleProjectDTO> projectList;

    public BladeUserDetails(Long userId, String tenantId, String tenantName, String platformName, String name, String realName, String deptId, String roleId, String roleName, String roleGroup,
                            String avatar, String username, String password, City cityInfo, List<SimpleProjectDTO> projectList, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.platformName = platformName;
        this.name = name;
        this.realName = realName;
        this.account = username;
        this.deptId = deptId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleGroup = roleGroup;
        this.avatar = avatar;
        this.projectList = projectList;
        if (cityInfo != null) {
            this.cityId = String.valueOf(cityInfo.getId());
            this.cityName = cityInfo.getCityName();
            this.lat = cityInfo.getLat();
            this.lon = cityInfo.getLon();
        }
    }

}
