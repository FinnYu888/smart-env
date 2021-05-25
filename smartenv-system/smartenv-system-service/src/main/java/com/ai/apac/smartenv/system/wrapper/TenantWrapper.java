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

import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.TenantVO;
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
public class TenantWrapper extends BaseEntityWrapper<Tenant, TenantVO> {

    public static TenantWrapper build() {
        return new TenantWrapper();
    }

    @Override
    public TenantVO entityVO(Tenant tenant) {
        return buildVO(tenant, false);
    }

    public TenantVO entityDetailVO(Tenant tenant) {
        return buildVO(tenant, true);
    }

    public List<TenantVO> listVO(List<Tenant> list) {
        List<TenantVO> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return collect;
    }

    private TenantVO buildVO(Tenant tenant, boolean showDetail) {
        if (tenant == null) {
            return null;
        }
        TenantVO tenantVO = Objects.requireNonNull(BeanUtil.copy(tenant, TenantVO.class));
        String statusName = DictCache.getValue("tenant_status", Func.toInt(tenant.getStatus()));
        tenantVO.setStatusName(statusName);
        String cityName = CityCache.getCityNameById(tenant.getCityId());
        tenantVO.setCityName(cityName);
        if (showDetail) {
            List<String> cityFullId = new ArrayList<String>();
            if(tenant.getCityId() != null){
                //需要遍历父节点,最多三级
                City parentCity = CityCache.getParentCity(tenant.getCityId());
                if (parentCity != null) {
                    if(parentCity.getParentId() != 0){
                        City rootCity = CityCache.getParentCity(parentCity.getId());
                        if(rootCity != null){
                            cityFullId.add(String.valueOf(rootCity.getId()));
                            cityFullId.add(String.valueOf(parentCity.getId()));
                        }
                    }else{
                        cityFullId.add(String.valueOf(parentCity.getId()));
                    }
                }
                cityFullId.add(String.valueOf(tenant.getCityId()));
            }
            tenantVO.setCityFullId(cityFullId);
        }
        User user = UserCache.getUserByAcct(tenant.getAdminAccount());
        if(user != null){
            tenantVO.setAdminId(user.getId());
            tenantVO.setEmail(user.getEmail());
        }
        return tenantVO;
    }

}
