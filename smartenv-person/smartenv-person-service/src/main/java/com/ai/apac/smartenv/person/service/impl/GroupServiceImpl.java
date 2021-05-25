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

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Group;
import com.ai.apac.smartenv.person.entity.GroupMember;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.mapper.GroupMemberMapper;
import com.ai.apac.smartenv.person.service.IGroupMemberService;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.GroupMemberVO;
import com.ai.apac.smartenv.person.vo.GroupVO;
import com.ai.apac.smartenv.person.mapper.GroupMapper;
import com.ai.apac.smartenv.person.service.IGroupService;
import com.ai.apac.smartenv.person.wrapper.GroupMemberWrapper;
import com.ai.apac.smartenv.person.wrapper.GroupWrapper;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.rmi.ServerException;
import java.util.*;

/**
 * 组信息表 服务实现类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Service
@AllArgsConstructor
public class GroupServiceImpl extends BaseServiceImpl<GroupMapper, Group> implements IGroupService {


	@Autowired
	private IGroupMemberService groupMemberService;

	private IOssClient ossClient;

	@Override
	public IPage<GroupVO> selectGroupPage(IPage<GroupVO> page, GroupVO group) {
		return page.setRecords(baseMapper.selectGroupPage(page, group));
	}

	@Override
	public List<GroupVO> getGroupList(GroupVO group) {
		List<GroupVO> groupVOList = new ArrayList<GroupVO>();
		QueryWrapper<Group> wrapper = new QueryWrapper<Group>();
		wrapper.lambda().eq(Group::getCreateUser, AuthUtil.getUserId());
		if(ObjectUtil.isNotEmpty(group.getGroupName())){
			wrapper.lambda().like(Group::getGroupName,group.getGroupName());
		}
		wrapper.lambda().orderByDesc(Group::getUpdateTime);
		List<Group> groupList= this.list(wrapper);
		if(ObjectUtil.isNotEmpty(groupList) && groupList.size() > 0){
			groupVOList = GroupWrapper.build().listVO(this.list(wrapper));
			List<Long> groupIds = new ArrayList<Long>();
			int index = 0;
			for (int i = 0; i < groupVOList.size(); i++) {
				groupIds.add(groupVOList.get(i).getId());
				groupVOList.get(i).setCreateUserName(AuthUtil.getUserName());
				if(ObjectUtil.isNotEmpty(groupVOList.get(i).getIsDefault()) && groupVOList.get(i).getIsDefault() == 1L){
					index = i;
				}
			};
			Collections.swap(groupVOList,index,0);

			QueryWrapper<GroupMember> wrapper1= new QueryWrapper<GroupMember>();
			wrapper1.lambda().in(GroupMember::getGroupId,groupIds);
			List<GroupMember> groupMemberList = groupMemberService.list(wrapper1);
			if(ObjectUtil.isNotEmpty(groupMemberList) && groupMemberList.size() > 0){
				Map<Long,List<GroupMemberVO>> memberMap = new HashMap<Long,List<GroupMemberVO>>();
				groupMemberList.forEach(groupMember -> {

					GroupMemberVO groupMemberVO = GroupMemberWrapper.build().entityVO(groupMember);

					Person person  = PersonCache.getPersonById(AuthUtil.getTenantId(),groupMember.getMemberId());

					// 人员照片
					String image = person.getImage();
					if (StringUtils.isBlank(image)) {
						image = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_PERSON);
					}
					groupMemberVO.setImage(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, image).getData());

					// 部门名称
					Dept dept = DeptCache.getDept(groupMember.getMemberDeptId());
					if (dept != null) {
						groupMemberVO.setMemberDeptName(dept.getFullName());
					}

					// 岗位
					if (groupMember.getMemberPositionId() != null) {
						groupMemberVO.setMemberPositionName(StationCache.getStationName(groupMember.getMemberPositionId()));
					}


					if(ObjectUtil.isNotEmpty(memberMap.get(groupMember.getGroupId()))){
						memberMap.get(groupMember.getGroupId()).add(groupMemberVO);
					}else{
						List<GroupMemberVO> groupMemberVOs = new ArrayList<GroupMemberVO>();
						groupMemberVOs.add(groupMemberVO);
						memberMap.put(groupMember.getGroupId(),groupMemberVOs);
					}
				});
				groupVOList.forEach(groupVO -> {
					if(ObjectUtil.isNotEmpty(memberMap.get(groupVO.getId()))){
						groupVO.setGroupMemberVOList(memberMap.get(groupVO.getId()));
					}
				});
			}
		}
		return groupVOList;
	}

	@Override
	public GroupVO getGroupDetails(Long groupId, String memberName) {
		GroupVO groupVO = GroupWrapper.build().entityVO(this.getById(groupId));
		if(ObjectUtil.isEmpty(groupVO)){
			throw new ServiceException("该组不存在");
		}
		QueryWrapper<GroupMember> wrapper= new QueryWrapper<GroupMember>();
		wrapper.lambda().eq(GroupMember::getGroupId,groupId);
		if(ObjectUtil.isNotEmpty(memberName)){
			wrapper.lambda().like(GroupMember::getMemberName,memberName);
		}
		List<GroupMember> groupMemberList = groupMemberService.list(wrapper);

		if(ObjectUtil.isNotEmpty(groupMemberList) && groupMemberList.size() > 0){
			List<GroupMemberVO> memberVOList = new ArrayList<GroupMemberVO>();
			groupMemberList.forEach(groupMember -> {

				GroupMemberVO groupMemberVO = GroupMemberWrapper.build().entityVO(groupMember);

				Person person  = PersonCache.getPersonById(AuthUtil.getTenantId(),groupMember.getMemberId());

				// 人员照片
				String image = person.getImage();
				if (StringUtils.isBlank(image)) {
					image = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_PERSON);
				}
				groupMemberVO.setImage(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, image).getData());


				// 部门名称
				Dept dept = DeptCache.getDept(groupMember.getMemberDeptId());
				if (dept != null) {
					groupMemberVO.setMemberDeptName(dept.getFullName());
				}

				// 岗位
				if (groupMember.getMemberPositionId() != null) {
					groupMemberVO.setMemberPositionName(StationCache.getStationName(groupMember.getMemberPositionId()));
				}

				memberVOList.add(groupMemberVO);

			});

			groupVO.setGroupMemberVOList(memberVOList);

		}
		return groupVO;
	}

	@Override
	public Boolean setGroupDefault(Long groupId) {

		List<Group> groupList = new ArrayList<Group>();

		QueryWrapper<Group> wrapper = new QueryWrapper<Group>();
		wrapper.lambda().eq(Group::getCreateUser,AuthUtil.getUserId());
		wrapper.lambda().eq(Group::getIsDefault,1l);
		List<Group> groupList1 = this.list(wrapper);
		if(ObjectUtil.isNotEmpty(groupList1) && groupList1.size() > 0) {
			Group group = groupList1.get(0);
			group.setIsDefault(0L);
			groupList.add(group);
		}

		Group group_ = new Group();
		group_.setId(groupId);
		group_.setIsDefault(1l);
		groupList.add(group_);

		return this.updateBatchById(groupList);
	}


	@Override
	public Boolean saveGroup(GroupVO groupVO) {
		checkGroup(groupVO);
		Group group = BeanUtil.copyProperties(groupVO, Group.class);
		group.setMemberNum(0L);
		return this.save(group);
	}


	@Override
	public Boolean updateGroup(GroupVO groupVO) {
		checkGroup(groupVO);
		Group group = BeanUtil.copyProperties(groupVO, Group.class);
		return this.updateById(group);
	}

	@Override
	public Boolean delGroup(List<Long> ids) {
		this.deleteLogic(ids);
		List<Long> memberIds = new ArrayList<Long>();
		List<GroupMember> members = groupMemberService.list(new QueryWrapper<GroupMember>().lambda().in(GroupMember::getGroupId,ids));
		if(ObjectUtil.isNotEmpty(members) && members.size() > 0 ){
			members.forEach(groupMember -> {
				memberIds.add(groupMember.getId());
			});
			groupMemberService.deleteLogic(memberIds);
		}

		return true;
	}

	private Boolean checkGroup(GroupVO groupVO){
		if(ObjectUtil.isEmpty(groupVO.getGroupName())){
			throw new ServiceException("组名称不能为空");
		}
		String groupName = groupVO.getGroupName();
		QueryWrapper<Group> wrapper = new QueryWrapper<Group>();
		wrapper.lambda().eq(Group::getGroupName,groupName);
		List<Group> l = this.list(wrapper);
		if(ObjectUtil.isNotEmpty(l) && l.size() > 0 && (ObjectUtil.isEmpty(groupVO.getId())||!groupVO.getId().equals(l.get(0).getId()))){
			throw new ServiceException("组名称不能重复");
		}

		return true;

	}

}
