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

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.GroupMember;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.GroupMemberVO;
import com.ai.apac.smartenv.person.mapper.GroupMemberMapper;
import com.ai.apac.smartenv.person.service.IGroupMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

/**
 * 组成员信息表 服务实现类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Service
@AllArgsConstructor
public class GroupMemberServiceImpl extends BaseServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {

	private IDeviceRelClient deviceRelClient;


	@Override
	public IPage<GroupMemberVO> selectGroupMemberPage(IPage<GroupMemberVO> page, GroupMemberVO groupMember) {
		return page.setRecords(baseMapper.selectGroupMemberPage(page, groupMember));
	}

	@Override
	public Boolean addGroupMembers(Long groupId,String memberIds) {

		List<Long> memberList = Func.toLongList(",", memberIds);

		if(ObjectUtil.isEmpty(memberList) || memberList.size() == 0){
			throw new ServiceException("成员不能为空");
		}

		if(ObjectUtil.isEmpty(groupId)){
			throw new ServiceException("群组ID不能为空");
		}

		List<GroupMember> toAddGroupMembers = new ArrayList<GroupMember>();
		memberList.forEach(memberId -> {
			GroupMember toADDGroupMember = new GroupMember();
			toADDGroupMember.setGroupId(groupId);
			toADDGroupMember.setMemberId(memberId);
			Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),memberId);
			toADDGroupMember.setMemberName(person.getPersonName());
			List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(person.getId(), CommonConstant.ENTITY_TYPE.PERSON).getData();
			if(ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0 ){
				DeviceInfo deviceInfo = DeviceCache.getDeviceById(AuthUtil.getTenantId(),deviceRelList.get(0).getDeviceId());
				toADDGroupMember.setMemberDeviceCode(deviceInfo.getDeviceCode());
			}
			toADDGroupMember.setMemberDeptId(person.getPersonDeptId());
			toADDGroupMember.setMemberJobNumber(person.getJobNumber());
			toADDGroupMember.setMemberMobileNumber(person.getMobileNumber());
			toADDGroupMember.setMemberPositionId(person.getPersonPositionId());
			toAddGroupMembers.add(toADDGroupMember);
		});

		return this.saveBatch(toAddGroupMembers);

	}

	@Override
	public Boolean delGroupMembers(Long groupId,String memberIds) {

		QueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>();

		List<Long> memberIdList = Func.toLongList(",", memberIds);

		if(ObjectUtil.isEmpty(memberIdList) || memberIdList.size() == 0){
			throw new ServiceException("成员不能为空");
		}

		wrapper.lambda().in(GroupMember::getMemberId,memberIdList);

		if(ObjectUtil.isEmpty(groupId)){
			throw new ServiceException("群组ID不能为空");
		}
		wrapper.lambda().eq(GroupMember::getGroupId,groupId);

		List<GroupMember> memberList = this.list(wrapper);

		if(ObjectUtil.isNotEmpty(memberList) && memberList.size() > 0){
			List<Long> toDelIds = new ArrayList<Long>();
			memberList.forEach(groupMember -> {
				toDelIds.add(groupMember.getId());
			});
			return this.deleteLogic(toDelIds);
		}

		return true;

	}

}
