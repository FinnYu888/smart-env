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

import com.ai.apac.smartenv.oss.cache.OssCache;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.CompanyCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.vo.CompanyVO;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
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
public class CompanyWrapper extends BaseEntityWrapper<Company, CompanyVO> {

    public static CompanyWrapper build() {
        return new CompanyWrapper();
    }

    @Override
    public CompanyVO entityVO(Company company) {
        return buildVO(company, false);
    }

    public CompanyVO entityDetailVO(Company company) {
        return buildVO(company, true);
    }

    @Override
    public List<CompanyVO> listVO(List<Company> list) {
        List<CompanyVO> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return collect;
    }

    private CompanyVO buildVO(Company company, boolean showDetail) {
        if (company == null) {
            return null;
        }
        CompanyVO companyVO = Objects.requireNonNull(BeanUtil.copy(company, CompanyVO.class));
        String companyStatusName = DictCache.getValue("company_status", Func.toInt(company.getStatus()));
        companyVO.setStatusName(companyStatusName);
        String cityName = CityCache.getCityNameById(company.getCityId());
        companyVO.setCityName(cityName);
        if (showDetail) {
            List<String> cityFullId = new ArrayList<String>();
            if (company.getCityId() != null) {
                //需要遍历父节点,最多三级
                City parentCity = CityCache.getParentCity(company.getCityId());
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
                cityFullId.add(String.valueOf(company.getCityId()));
            }
            companyVO.setCityFullId(cityFullId);
        }
        Company parentCompany = CompanyCache.getCompany(company.getParentId());
        String parentCompanyName = parentCompany == null ? "" : parentCompany.getFullName();
        companyVO.setParentCompanyName(parentCompanyName);
        String companySizeName = DictCache.getValue("company_size", Func.toInt(company.getCompanySize()));
        companyVO.setCompanySizeName(companySizeName);
        return companyVO;
    }

    public List<INode> listNodeVO(List<Company> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<INode> collect = list.stream().map(company -> {
                return entityVO(company);
            }).collect(Collectors.toList());
            return ForestNodeMerger.merge(collect);
        }
        return null;
    }
}
