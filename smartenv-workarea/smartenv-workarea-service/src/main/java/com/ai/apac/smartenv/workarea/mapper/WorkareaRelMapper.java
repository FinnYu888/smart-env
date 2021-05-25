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
package com.ai.apac.smartenv.workarea.mapper;

import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.vo.WorkareaRelVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

/**
 * 工作区域关联表 Mapper 接口
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface WorkareaRelMapper extends BaseMapper<WorkareaRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param workareaRel
	 * @return
	 */
	List<WorkareaRelVO> selectWorkareaRelPage(IPage page, WorkareaRelVO workareaRel);


	/**
	 * 自定义列表查询
	 *
	 * @param
	 * @return
	 */
	@Select({"SELECT * FROM ai_workarea_rel rel where rel.is_deleted=#{isDeleted} and rel.entity_id=#{entityId} and rel.entity_type=#{entityType} and rel.tenant_id=#{tenantId} order by rel.update_time desc"})
	List<WorkareaRel> selectWorkareaRelHList(@Param("entityId") Long entityId,
											 @Param("entityType") Long entityType,
											 @Param("isDeleted") Integer isDeleted,
											 @Param("tenantId") String tenantId);

	/**
	 * 自定义列表查询
	 *
	 * @param
	 * @return
	 */
	@Select({"SELECT * FROM ai_workarea_rel rel where rel.is_deleted=#{isDeleted} and rel.entity_id=#{entityId} and rel.entity_type=#{entityType} and rel.tenant_id=#{tenantId} and rel.update_time between #{startTime} and #{endTime} order by rel.update_time desc"})
	List<WorkareaRel> queryWorkareaRelHList(@Param("entityId") Long entityId,
											 @Param("entityType") Long entityType,
											 @Param("isDeleted") Integer isDeleted,
											@Param("tenantId") String tenantId,
											@Param("startTime") Timestamp startTime,
											@Param("endTime") Timestamp endTime
											);
}
