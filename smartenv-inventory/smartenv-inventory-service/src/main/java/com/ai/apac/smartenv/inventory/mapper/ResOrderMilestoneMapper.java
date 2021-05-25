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
package com.ai.apac.smartenv.inventory.mapper;

import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Blade
 * @since 2020-02-25
 */
public interface ResOrderMilestoneMapper extends BaseMapper<ResOrderMilestone> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param resOrderMilestone
	 * @return
	 */
	List<ResOrderMilestoneVO> selectResOrderMilestonePage(IPage page, ResOrderMilestoneVO resOrderMilestone);

	/**
	* 更新
	*/
	@Select("update ai_res_order_milestone set done_result=#{doneResult},done_remark=#{doneRemark},assignment_id=#{assignmentId}," +
			"assignment_name=#{assignmentName},update_time = now() where order_id = #{orderId} and task_define_name=#{taskName}")
	void updateOrderMilestoneByCond(@Param("doneResult") String doneResult, @Param("doneRemark") String doneRemark, @Param("assignmentId") String assignmentId,
                                    @Param("assignmentName") String assignmentName, @Param("orderId") Long orderId, @Param("taskName") String taskName);

	@Select("update ai_res_order_milestone set task_id = #{taskId},update_time = now() where order_id = #{orderId} and task_define_name=#{taskName}")
	void updateDeliverOrderMilestone(@Param("taskId") String taskId, @Param("orderId") Long orderId, @Param("taskName") String taskName);
}
