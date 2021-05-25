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

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.auth.constant.AuthConstant;
import com.ai.apac.smartenv.auth.utils.TokenUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.ProjectConstant;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.dto.SimpleProjectDTO;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
@Slf4j
public class BladeUserDetailsServiceImpl implements UserDetailsService {

    private IUserClient userClient;

    private IProjectClient projectClient;

    private BladeRedis bladeRedis;

    @Override
    @SneakyThrows
    public BladeUserDetails loadUserByUsername(String username) {
        HttpServletRequest request = WebUtil.getRequest();
        // 项目需要不再需要通过前端传入租户ID
//		String headerTenant = request.getHeader(TokenUtil.TENANT_HEADER_KEY);
//		String paramTenant = request.getParameter(TokenUtil.TENANT_PARAM_KEY);
//		if (StringUtil.isAllBlank(headerTenant, paramTenant)) {
//			throw new UserDeniedAuthorizationException(TokenUtil.TENANT_NOT_FOUND);
//		}

        //根据帐号查询租户信息
        User userTmp = UserCache.getUserByAcct(username);
        log.info("租户ID:{}", userTmp.getTenantId());
        R<UserInfo> result = userClient.getUserInfoByAccount(username);
        UserInfo userInfo = null;
        User user = null;
        String tenantId = null;
        List<ProjectVO> projectList = null;
        if (result.isSuccess()) {
            userInfo = result.getData();
            if (userInfo == null || userInfo.getUser() == null || userInfo.getUser().getId() == null) {
                throw new UsernameNotFoundException(result.getMsg());
            }
            user = userInfo.getUser();
            if (user.getStatus() == 2) {
                throw new UserDeniedAuthorizationException(TokenUtil.USER_STATUS_LOCKED_ERROR);
            } else if (user.getStatus() == 3) {
                throw new UserDeniedAuthorizationException(TokenUtil.USER_STATUS_CANCELLED_ERROR);
            }
            //查询默认项目
//            List<ProjectVO> projectLst = ProjectCache.listProjectByAccountId(user.getId());
            R<List<ProjectVO>> projectLstResult = projectClient.listProjectByAccountId(user.getId(), ProjectConstant.ProjectStatus.Normal);
            if (projectLstResult.isSuccess() && projectLstResult.getData() != null) {
                projectList = projectLstResult.getData();
                if (CollUtil.isNotEmpty(projectList) && projectList.size() > 0) {
                    ProjectVO defaultProject = projectList.stream().filter(projectVO -> projectVO.getIsDefault() == 1).findFirst().orElse(null);
                    if (defaultProject != null && defaultProject.getId() != null) {
                        tenantId = defaultProject.getProjectCode();
                    } else {
                        tenantId = user.getTenantId();
                    }
                } else {
                    tenantId = user.getTenantId();
                }
            } else {
                tenantId = user.getTenantId();
            }
        } else {
            throw new UsernameNotFoundException(result.getMsg());
        }

        String switchProject = request.getParameter(TokenUtil.SWITCH_PROJECT);
        log.info("switchProject:{}", switchProject);
        if (StringUtils.isNotEmpty(switchProject)) {
            tenantId = switchProject;
        }
//		String tenantId = StringUtils.isBlank(headerTenant) ? paramTenant : headerTenant;

        // 获取租户信息
        Tenant tenant = TenantCache.getTenantById(tenantId);
        String tenantName = tenant.getTenantName();

        //获取项目信息
        String platformName = "";
        Project project = ProjectCache.getProjectByCode(tenantId);
        if (project != null && project.getId() != null) {
            platformName = project.getShortName();
        } else {
            if (tenantName.length() > 10) {
                platformName = tenant.getTenantName().substring(0, 10);
            } else {
                platformName = tenantName;
            }
        }
//        R<Tenant> tenant = sysClient.getTenant(tenantId);
        if (tenant != null) {
            Date expireTime = tenant.getExpireTime();
            if (expireTime != null && expireTime.before(DateUtil.now())) {
                throw new UserDeniedAuthorizationException(TokenUtil.USER_HAS_NO_TENANT_PERMISSION);
            }
            if (tenant.getStatus() != 1) {
                throw new UserDeniedAuthorizationException(TokenUtil.TENANT_LOCKED);
            }
        } else {
            throw new UserDeniedAuthorizationException(TokenUtil.USER_HAS_NO_TENANT);
        }

        //获取租户所在城市坐标信息
        Long cityId = tenant.getCityId();
        City cityInfo = CityCache.getCity(cityId);
//        if (StringUtils.isNotEmpty(project.getLat()) && StringUtils.isNotEmpty(project.getLng())) {
//            cityInfo.setId(project.getCityId());
//            cityInfo.setLat(project.getLat());
//            cityInfo.setLon(project.getLng());
//        }

        // 获取用户类型
        String userType = Func.toStr(request.getHeader(TokenUtil.USER_TYPE_HEADER_KEY), TokenUtil.DEFAULT_USER_TYPE);

        // 判断返回信息
        if (result.isSuccess()) {
            if (user == null || user.getId() == null) {
                throw new UsernameNotFoundException(TokenUtil.USER_NOT_FOUND);
            }
            if (userInfo.getRoles() == null || Func.isEmpty(userInfo.getRoles())) {
                throw new UserDeniedAuthorizationException(TokenUtil.USER_HAS_NO_ROLE);
            }
            //查询该操作员是否和某员工关联,如果有关联返回员工姓名,否则就用帐号名作为员工姓名
            String userName = PersonUserRelCache.getPersonNameByUser(user.getId());
            if (StringUtils.isBlank(userName)) {
                userName = user.getAccount();
            }
            String nickName = CommonUtil.getNickName(userName);
            //获取用户关联的项目信息
            List<SimpleProjectDTO> simpleProjectList = null;
            if (CollUtil.isNotEmpty(projectList)) {
                simpleProjectList = projectList.stream().map(projectVO -> {
                    return BeanUtil.copy(projectVO, SimpleProjectDTO.class);
                }).collect(Collectors.toList());
            } else {
                simpleProjectList = new ArrayList<SimpleProjectDTO>();
                SimpleProjectDTO simpleProjectDTO = BeanUtil.copy(project, SimpleProjectDTO.class);
                simpleProjectList.add(simpleProjectDTO);
            }

            //记录人员在线信息,失效时间5天
            bladeRedis.setEx(CacheNames.USER_ONLINE + StringPool.COLON + user.getId(), tenantId, CacheNames.ExpirationTime.EXPIRATION_TIME_5DAYS);

            return new BladeUserDetails(user.getId(),
                    tenantId, tenantName, platformName, nickName, userName, user.getDeptId(), user.getRoleId(), Func.join(result.getData().getRoles()), user.getRoleGroup(),
                    Func.toStr(user.getAvatar(), TokenUtil.DEFAULT_AVATAR),
                    username, AuthConstant.ENCRYPT + user.getPassword(), cityInfo, simpleProjectList, true, true, true, true,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(Func.join(result.getData().getRoles())));
        } else {
            throw new UsernameNotFoundException(result.getMsg());
        }
    }

}
