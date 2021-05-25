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
package com.ai.apac.smartenv.person.mapper;

import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 人员信息表 Mapper 接口
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface PersonMapper extends BaseMapper<Person> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param person
	 * @return
	 */
	List<PersonVO> selectPersonPage(IPage page, PersonVO person);

	/**
	 * 
	 * @Function: PersonMapper::updatePersonInfoById
	 * @Description: 修改，可置空
	 * @param person
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月20日 下午2:46:33 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Integer updatePersonInfoById(@Param("record")PersonVO person);

	List<DeptStaffCountDTO> getDeptStaffCount(@Param("tenantId") String tenantId);

	List<PersonAccountVO> listPersonAccount(@Param("personName") String personName);
}
