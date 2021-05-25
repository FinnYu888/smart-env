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
package com.ai.apac.smartenv.person.service;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.vo.PersonUserRelVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.user.entity.User;

import java.util.List;

import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 员工用户关联表 服务类
 *
 * @author Blade
 * @since 2020-03-31
 */
public interface IPersonUserRelService extends BaseService<PersonUserRel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param personUserRel
	 * @return
	 */
	IPage<PersonUserRelVO> selectPersonUserRelPage(IPage<PersonUserRelVO> page, PersonUserRelVO personUserRel);

	/**
	 * 
	 * @Function: IPersonUserRelService::getRelByUserOrPerson
	 * @Description: 通过用户或人员查询关联关系
	 * @param userId
	 * @param personId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月31日 下午5:32:55 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	PersonUserRel getRelByUserOrPerson(Long userId, Long personId);

	/**
	 * 
	 * @Function: IPersonUserRelService::savePersonUserRel
	 * @Description: 保存关联关系
	 * @param personUserRel
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月31日 下午5:37:24 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Boolean savePersonUserRel(PersonUserRel personUserRel);

	/**
	 * 
	 * @Function: IPersonUserRelService::deletePersonUserRel
	 * @Description: 逻辑删除
	 * @param idList
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @return 
	 * @date: 2020年4月1日 上午9:44:53 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	Boolean deletePersonUserRel(List<Long> idList);

	/**
	 * 
	 * @Function: IPersonUserRelService::unbindUser
	 * @Description: 解绑操作员
	 * @param personId
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年4月2日 下午2:18:18 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void unbindUser(Long personId);

	/**
	 * 
	 * @Function: IPersonUserRelService::listUserForPerson
	 * @Description: 查询可绑定的操作员
	 * @param personDeptId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @param personId 
	 * @date: 2020年4月2日 下午3:12:53 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<PersonVO> listUserForPerson(Long personDeptId, Long personId);

	/**
	 *
	 * @Function: IPersonUserRelService::listUserForPerson
	 * @Description: 查询可绑定的操作员
	 * @param userId
	 * @return
	 * @version: v1.0.0
	 * @author: yupf3
	 * @date: 2020年4月2日 下午3:12:53
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	List<Person> listPersonForUser(Long userId);

}
