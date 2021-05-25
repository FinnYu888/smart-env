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
package com.ai.apac.smartenv.event.mapper;

import com.ai.apac.smartenv.event.entity.PublicEventInfo;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.mongodb.repository.CountQuery;

import java.util.List;

/**
 * 事件基本信息表 Mapper 接口
 *
 * @author Blade
 * @since 2020-02-06
 */
public interface PublicEventInfoMapper extends BaseMapper<PublicEventInfo> {


//    @Select("select * from ai_public_event_info t where t.tenant_id={tenantId} and t.tenant_id is null")
//     List<PublicEventInfo> getEventInfoByTenant(String tenantId);
//
//
//    @Select("select * from ai_public_event_info t where t.reportPersonId=#{wechatId} limit #{begin}, #{end} ")
//    List<PublicEventInfo> getEventInfoByWechatId(String wechatId,int begin,int end);
//
//
//    @CountQuery("select count(*) from ai_public_event_info t where t.reportPersonId=#{wechatId}  ")
//    Integer countEventInfoByWechatId(String wechatId);


}
