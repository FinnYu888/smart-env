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

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;

import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_FACILITY_NAME,
        fallback = IAshcanClientFallback.class
)
public interface IAshcanClient {

    String API_PREFIX = "/client";
    String ASHCAN = API_PREFIX + "/ashcan";
    String ASHCAN_VO = API_PREFIX + "/ashcan_vo";
    String ASHCAN_LIST = API_PREFIX + "/ashcan_list";
    String ASHCAN_ALL_LIST = API_PREFIX + "/ashcan_all_list";
    String ASHCAN_ALL_COUNT = API_PREFIX + "/ashcan_all_count";

    /**
     * 获取垃圾桶信息
     *
     * @param id
     * @return
     */
    @GetMapping(ASHCAN)
    R<AshcanInfo> getAshcan(@RequestParam("id") Long id);
    
    @GetMapping(ASHCAN_VO)
    R<AshcanInfoVO> getAshcanVO(@RequestParam("id") Long id);

    @GetMapping(ASHCAN_LIST)
    R<List<AshcanInfo>>  listAshcanInfoByid(@RequestParam("id") Long id);

    @GetMapping(ASHCAN_ALL_LIST)
    R<List<AshcanInfo>>  listAshcanInfoAll();

    @GetMapping(ASHCAN_ALL_COUNT)
    R<Integer>  countAshcanInfo(@RequestParam("tenantId") String tenantId);

}
