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

import com.ai.apac.smartenv.person.entity.PersonExt;
import com.ai.apac.smartenv.person.vo.PersonExtVO;
import com.ai.apac.smartenv.person.vo.PersonVO;

import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 人员扩展信息表 服务类
 *
 * @author Blade
 * @since 2020-01-16
 */
public interface IPersonExtService extends BaseService<PersonExt> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param personExt
	 * @return
	 */
	IPage<PersonExtVO> selectPersonExtPage(IPage<PersonExtVO> page, PersonExtVO personExt);

	/**
	 * 
	 * @Function: IPersonExtService::savePicture
	 * @Description: 保存人员驾驶证照片
	 * @param personId
	 * @param attrId
	 * @param attrName
	 * @param attrValue
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @return 
	 * @date: 2020年2月18日 上午11:21:02 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	boolean savePicture(Long personId, Long attrId, String attrName, String attrValue);

	/**
	 * 
	 * @Function: IPersonExtService::updatePicture
	 * @Description: 更新照片
	 * @param personId
	 * @param attrValue
	 * @param attrId
	 * @param attrName
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月18日 下午2:12:04 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	void updatePicture(Long personId, String attrValue, Long attrId, String attrName);

	void removePersonAttr(Long id);

	PersonVO getPictures(PersonVO personVO);

	PersonExt getPersonAttr(Long personId, Long attrId);

}
