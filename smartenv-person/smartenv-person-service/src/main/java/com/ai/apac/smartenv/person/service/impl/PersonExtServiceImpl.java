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
package com.ai.apac.smartenv.person.service.impl;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.entity.PersonExt;
import com.ai.apac.smartenv.person.vo.PersonExtVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleExt;
import com.ai.apac.smartenv.person.mapper.PersonExtMapper;
import com.ai.apac.smartenv.person.service.IPersonExtService;

import java.util.ArrayList;
import java.util.List;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 人员扩展信息表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class PersonExtServiceImpl extends BaseServiceImpl<PersonExtMapper, PersonExt> implements IPersonExtService {

	@Override
	public IPage<PersonExtVO> selectPersonExtPage(IPage<PersonExtVO> page, PersonExtVO personExt) {
		return page.setRecords(baseMapper.selectPersonExtPage(page, personExt));
	}

	@Override
	public boolean savePicture(Long personId, Long attrId, String attrName, String attrValue) {
		PersonExt personExt = new PersonExt();
		personExt.setPersonId(personId);;
		personExt.setAttrId(attrId);
		personExt.setAttrName(attrName);
		personExt.setAttrValue(attrValue);
		return save(personExt);
	}

	@Override
	public void updatePicture(Long personId, String attrValue, Long attrId, String attrName) {
		PersonExt personExt = new PersonExt();
		personExt.setPersonId(personId);
		personExt.setAttrId(attrId);
		PersonExt picture = getOne(Condition.getQueryWrapper(personExt).last("LIMIT 1"));
		if (picture != null && StringUtil.isNotBlank(attrValue)) {
			picture.setAttrValue(attrValue);
			updateById(picture);
		} else if (picture == null && StringUtil.isNotBlank(attrValue)) {
			savePicture(personId, attrId, attrName, attrValue);
		}
	}

	@Override
	public void removePersonAttr(Long personId) {
		PersonExt personExt = new PersonExt();
		personExt.setPersonId(personId);
		List<PersonExt> pictureList = list(Condition.getQueryWrapper(personExt));
		if (pictureList != null && !pictureList.isEmpty()) {
			List<Long> paramList = new ArrayList<>();
			pictureList.forEach(ext -> {
				paramList.add(ext.getId());
			});
			deleteLogic(paramList);
		}
	}

	@Override
	public PersonVO getPictures(PersonVO personVO) {
		Long personId = personVO.getId();
		// 行驶证正页
		PersonExt picture = getPersonAttr(personId, PersonConstant.PersonExtAttr.DRIVER_LICENSE_FIRST_ATTR_ID);
		if (picture != null) {
			personVO.setDriverLicenseFirstName(picture.getAttrValue());
		}
		// 行驶证副页
		picture = getPersonAttr(personId, PersonConstant.PersonExtAttr.DRIVER_LICENSE_SECOND_ATTR_ID);
		if (picture != null) {
			personVO.setDriverLicenseSecondName(picture.getAttrValue());
		}
		return personVO;
	}
	
	@Override
	public PersonExt getPersonAttr(Long personId, Long attrId) {
		QueryWrapper<PersonExt> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("person_id", personId).eq("attr_id", attrId);
		return getOne(queryWrapper.last("LIMIT 1"));
	}

}
