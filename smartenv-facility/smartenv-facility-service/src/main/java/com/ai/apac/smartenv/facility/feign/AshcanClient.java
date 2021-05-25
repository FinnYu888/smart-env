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
package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.service.IAshcanInfoService;
import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
import com.ai.apac.smartenv.facility.wrapper.AshcanInfoWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class AshcanClient implements IAshcanClient {

    private IAshcanInfoService ashcanInfoService;

    @Override
    @GetMapping(ASHCAN)
    public R<AshcanInfo> getAshcan(Long id) {
        return R.data(ashcanInfoService.getById(id));
    }

    @Override
    @GetMapping(ASHCAN_VO)
    public R<AshcanInfoVO> getAshcanVO(Long id) {
        AshcanInfo ashcanInfo = ashcanInfoService.getById(id);
        AshcanInfoVO ashcanInfoVO = AshcanInfoWrapper.build().entityVO(ashcanInfo);
        return R.data(ashcanInfoService.getAshcanAllInfoByVO(ashcanInfoVO));
    }

    @Override
    @GetMapping(ASHCAN_LIST)
    public R<List<AshcanInfo>> listAshcanInfoByid(Long id) {
        return R.data(ashcanInfoService.list(new LambdaQueryWrapper<AshcanInfo>().eq(AshcanInfo::getWorkareaId, id)
                .eq(AshcanInfo::getIsDeleted, 0)));
    }

    @Override
    @GetMapping(ASHCAN_ALL_LIST)
    public R<List<AshcanInfo>> listAshcanInfoAll() {
        return R.data(ashcanInfoService.list(new LambdaQueryWrapper<AshcanInfo>().eq(AshcanInfo::getIsDeleted, 0)));
    }

    @Override
    public R<Integer> countAshcanInfo(String tenantId) {
        return R.data(ashcanInfoService.count(new QueryWrapper<AshcanInfo>().lambda().eq(AshcanInfo::getTenantId, tenantId)
                .eq(AshcanInfo::getIsDeleted, 0)));
    }
}
