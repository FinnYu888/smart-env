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
package com.ai.apac.smartenv.system.wrapper;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.CompanyCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Chill
 */
public class ProjectWrapper extends BaseEntityWrapper<Project, ProjectVO> {

    public static ProjectWrapper build() {
        return new ProjectWrapper();
    }

    @Override
    public ProjectVO entityVO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectVO projectVO = Objects.requireNonNull(BeanUtil.copy(project, ProjectVO.class));
        Long ownerId = project.getOwnerId();
        Person person = PersonCache.getPersonById(project.getProjectCode(), ownerId);
        projectVO.setOwnerName(person == null ? "" : person.getPersonName());
        projectVO.setMobile(person.getMobileNumber());
        projectVO.setEmail(person.getEmail());
        projectVO.setCityName(CityCache.getCityNameById(project.getCityId()));
        PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(ownerId);
        if (personUserRel != null) {
            User admin = UserCache.getUser(personUserRel.getUserId());
            if (personUserRel != null && admin != null && StringUtils.isNotBlank(admin.getAccount())) {
                projectVO.setAdminAccount(admin.getAccount());
                projectVO.setAccountId(admin.getId());
            }
        }
        Company company = CompanyCache.getCompany(project.getCompanyId());
        projectVO.setCompanyName(company == null ? "" : company.getFullName());
        projectVO.setStatusName(DictCache.getValue("project_status", project.getStatus()));
        projectVO.setProjectTypeName(this.getProjectTypeName(project.getProjectType()));
        projectVO.setParentCompanyId(company.getParentId());
        return projectVO;
    }

    public ProjectVO entityDetailVO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectVO projectVO = entityVO(project);
        projectVO.setCityFullId(this.getFullCityId(project.getCityId()));
        List<String> fullCompanyId = new ArrayList<>();
        if (projectVO.getParentCompanyId() <= 0L) {
            fullCompanyId.add(String.valueOf(projectVO.getCompanyId()));
        } else {
            fullCompanyId.add(String.valueOf(projectVO.getParentCompanyId()));
            fullCompanyId.add(String.valueOf(projectVO.getCompanyId()));
            Company parentCompany = CompanyCache.getCompany(projectVO.getParentCompanyId());
            if(parentCompany != null){
                projectVO.setParentCompanyName(parentCompany.getFullName());
            }
        }
        projectVO.setCompanyFullId(fullCompanyId);
        return projectVO;
    }

    @Override
    public List<ProjectVO> listVO(List<Project> list) {
        List<ProjectVO> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return collect;
    }

    private String getProjectTypeName(String projectType) {
        List<String> projectTypeList = Func.toStrList(projectType);
        String projectTypeNameStr = "";
        if (CollUtil.isNotEmpty(projectTypeList)) {
            StringBuffer projectTypeNames = new StringBuffer();
            projectTypeList.stream().forEach(projectTypeStr -> {
                String projectTypeName = DictCache.getValue("project_type", projectTypeStr);
                if (StringUtils.isNotBlank(projectTypeName)) {
                    projectTypeNames.append(projectTypeName).append(",");
                }
            });
            if (StringUtils.isNotBlank(projectTypeNames)) {
                if (projectTypeNames.indexOf(",") >= 0) {
                    projectTypeNameStr = projectTypeNames.substring(0, projectTypeNames.lastIndexOf(","));
                } else {
                    projectTypeNameStr = projectTypeNames.toString();
                }
            }
        }
        return projectTypeNameStr;
    }

    private List<String> getFullCityId(Long cityId) {
        List<String> cityFullId = new ArrayList<String>();
        if (cityId != null && cityId > 0L) {
            //需要遍历父节点,最多三级
            City parentCity = CityCache.getParentCity(cityId);
            if (parentCity != null && parentCity.getId() != null) {
                if (parentCity.getParentId() != 0) {
                    City rootCity = CityCache.getParentCity(parentCity.getId());
                    if (rootCity != null) {
                        cityFullId.add(String.valueOf(rootCity.getId()));
                        cityFullId.add(String.valueOf(parentCity.getId()));
                    }
                } else {
                    cityFullId.add(String.valueOf(parentCity.getId()));
                }
            }
            cityFullId.add(String.valueOf(cityId));
        }
        return cityFullId;
    }

    private List<String> getFullCompanyId(Company company) {
        List<String> fullCompanyId = null;
        if (company == null) {
            return null;
        }
        fullCompanyId = new ArrayList<>();
        if (company.getParentId() <= 0L) {
            fullCompanyId.add(String.valueOf(company.getId()));
        } else {
            fullCompanyId.add(String.valueOf(company.getParentId()));
            fullCompanyId.add(String.valueOf(company.getId()));
        }
        return fullCompanyId;
    }
}
