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
package com.ai.apac.smartenv.system.mapper;

import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author qianlong
 * @since 2020-11-26
 */
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 根据帐户ID查询关联的状态为正常的项目
     *
     * @param accountId
     * @param projectStatus
     * @param cityId
     * @param projectName
     * @param cityIdList
     * @return
     */
    List<ProjectVO> listProjectByAccountId(@Param("accountId") Long accountId, @Param("projectStatus") Integer projectStatus,
                                           @Param("cityId") Long cityId, @Param("projectName") String projectName,
                                           @Param("cityIdList") List<Long> cityIdList);
}
