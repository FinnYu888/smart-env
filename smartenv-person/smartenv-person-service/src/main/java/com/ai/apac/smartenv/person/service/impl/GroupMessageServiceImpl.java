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
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.feign.IWatchClient;
import com.ai.apac.smartenv.person.entity.GroupMessage;
import com.ai.apac.smartenv.person.vo.GroupMessageVO;
import com.ai.apac.smartenv.person.mapper.GroupMessageMapper;
import com.ai.apac.smartenv.person.service.IGroupMessageService;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组消息表 服务实现类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Service
@AllArgsConstructor
public class GroupMessageServiceImpl extends BaseServiceImpl<GroupMessageMapper, GroupMessage> implements IGroupMessageService {

	private IWatchClient watchClient;

	private IDeviceRelClient deviceRelClient;


	@Override
	public IPage<GroupMessageVO> selectGroupMessagePage(IPage<GroupMessageVO> page, GroupMessageVO groupMessage) {
		return page.setRecords(baseMapper.selectGroupMessagePage(page, groupMessage));
	}

	@Override
	public Boolean submitMessage(GroupMessageVO groupMessageVO) {
		//TODO 调用发送message接口
		String message = groupMessageVO.getMessageInfo();
		List<String> personIdList = Func.toStrList(groupMessageVO.getMembersId());
		if(ObjectUtil.isNotEmpty(personIdList) && personIdList.size() > 0){
			List<String> deviceCodes = new ArrayList<String>();
			List<Long> ids = personIdList.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
			List<DeviceInfo> deviceInfoList = deviceRelClient.getDevicesByEntityList(ids, CommonConstant.ENTITY_TYPE.PERSON).getData();
			if(ObjectUtil.isNotEmpty(deviceInfoList) && deviceInfoList.size() > 0){
				deviceInfoList.forEach(deviceInfo -> {
					deviceCodes.add(deviceInfo.getDeviceCode());
				});
				watchClient.sendVoice(deviceCodes,message);
			}
		}
		return this.save(groupMessageVO);
	}

}
