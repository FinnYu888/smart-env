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
package com.ai.apac.smartenv.device.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.device.entity.DeviceContact;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.mapper.DeviceContactMapper;
import com.ai.apac.smartenv.device.mapper.DeviceInfoMapper;
import com.ai.apac.smartenv.device.service.IDeviceContactService;
import com.ai.apac.smartenv.device.vo.DeviceContactVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员紧急联系人信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-26
 */
@Service
public class DeviceContactServiceImpl extends BaseServiceImpl<DeviceContactMapper, DeviceContact> implements IDeviceContactService {

    @Autowired
    private WatchService watchService;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Override
    public IPage<DeviceContactVO> selectDeviceContactPage(IPage<DeviceContactVO> page, DeviceContactVO deviceContact) {
        return page.setRecords(baseMapper.selectDeviceContactPage(page, deviceContact));
    }

    @Override
    public List<DeviceContact> listDeviceContact(DeviceContact deviceContact) {
        QueryWrapper<DeviceContact> wrapper = new QueryWrapper<DeviceContact>();
        if (null != deviceContact.getDeviceId()) {
            wrapper.lambda().eq(DeviceContact::getDeviceId, deviceContact.getDeviceId());
        }
        wrapper.lambda().orderByAsc(DeviceContact::getContactPersonSeq);

        return baseMapper.selectList(wrapper);
    }

    /**
     * 新增或修改紧急联系人
     *
     * @param deviceContact
     * @return
     */
    @Override
    public boolean submitContactInfo(DeviceContact deviceContact) {
        boolean result = saveOrUpdate(deviceContact);
        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                resetWatchSos(deviceContact.getDeviceId());
            }
        }));
        return result;
    }

    @Override
    public boolean removeDeviceContact(Long deviceId, String ids) {
        baseMapper.deleteBatchIds(Func.toLongList(ids));
        DeviceContact req = new DeviceContact();
        req.setDeviceId(deviceId);
        List<DeviceContact> deviceContactList = this.listDeviceContact(req);
        Long seq = 1l;
        for (DeviceContact deviceContact : deviceContactList) {
            deviceContact.setContactPersonSeq(seq);
            baseMapper.updateById(deviceContact);
            seq++;
        }
        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                resetWatchSos(deviceId);
            }
        }));
        return true;
    }

    @Override
    public Boolean transferDeviceContact(String ids) {
        List<DeviceContact> deviceContactList = baseMapper.selectBatchIds(Func.toLongList(ids));
        Long seq0 = deviceContactList.get(0).getContactPersonSeq();
        Long seq1 = deviceContactList.get(1).getContactPersonSeq();
        deviceContactList.get(0).setContactPersonSeq(seq1);
        baseMapper.updateById(deviceContactList.get(0));
        deviceContactList.get(1).setContactPersonSeq(seq0);
        baseMapper.updateById(deviceContactList.get(1));
        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                resetWatchSos(deviceContactList.get(0).getDeviceId());
            }
        }));
        return true;
    }

    /**
     * 根据设备ID重新设置SOS号码
     *
     * @param deviceId
     */
    private void resetWatchSos(Long deviceId) {
        //根据deviceId查询绑定的紧急联系人,按优先级顺序排列
        List<String> sosNumbeList = null;
        List<DeviceContact> contactList = baseMapper.selectList(new LambdaQueryWrapper<DeviceContact>().eq(DeviceContact::getDeviceId, deviceId).orderByAsc(DeviceContact::getContactPersonSeq));
        if (CollUtil.isNotEmpty(contactList)) {
            sosNumbeList = contactList.stream().map(deviceContact -> {
                return deviceContact.getContactPersonNumber();
            }).collect(Collectors.toList());
            //批量设置3个SOS号码,如果不足3个,默认补全,00000000000
            if (sosNumbeList.size() == 1) {
                sosNumbeList.add("00000000000");
                sosNumbeList.add("00000000000");
            } else if (sosNumbeList.size() == 2) {
                sosNumbeList.add("00000000000");
            }
        } else {//没有设置SOS号码则全部设置为空
            sosNumbeList = new ArrayList<String>();
            for (int i = 0; i < 3; i++) {
                sosNumbeList.add("00000000000");
            }
        }
        //根据deviceId获取deviceCode
        DeviceInfo deviceInfo = deviceInfoMapper.selectById(deviceId);
        watchService.batchSetSosNumber(deviceInfo.getDeviceCode(), sosNumbeList);
    }

}
