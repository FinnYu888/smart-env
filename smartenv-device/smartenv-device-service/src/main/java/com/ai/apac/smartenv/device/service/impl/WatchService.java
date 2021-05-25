package com.ai.apac.smartenv.device.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.UnicodeUtil;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.WatchCommandConst;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.device.dto.PhoneBookDTO;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.device.service.ISimInfoService;
import com.ai.apac.smartenv.device.service.IWatchService;
import com.ai.apac.smartenv.device.socket.SocketClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/21 11:12 上午
 **/
@Service
@Slf4j
public class WatchService implements IWatchService {

    @Autowired
    private IDeviceRelService deviceRelService;

    @Autowired
    private IDeviceInfoService deviceInfoService;

    @Autowired
    private ISimInfoService simInfoService;

    @Autowired
    private IPersonClient personClient;

    /**
     * 发送短信消息
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @Override
    public void sendMessage(String deviceCode, String message) {
        String uniCodeMsg = UnicodeUtil.toUnicode(message, true).replaceAll("\\\\u", "");
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.MESSAGE, uniCodeMsg);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 发送短信消息并转成语音
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @Override
    public void sendMessage2Voice(String deviceCode, String message) {
        if (message.trim().length() > 40) {
            throw new ServiceException("不能超过40个字符");
        }
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.SEND_TEXT_TO_VOICE, message);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 设置SOS号码
     *
     * @param deviceCode 设备编号
     * @param sosNumber  SOS号码
     * @param serial     SOS号码序号
     * @return
     */
    @Override
    public void setSosNumber(String deviceCode, String sosNumber, Integer serial) {
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.SOS + serial, sosNumber);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 批量设置SOS号码
     *
     * @param deviceCode    设备编号
     * @param sosNumberList SOS号码列表
     * @return
     */
    @Override
    public void batchSetSosNumber(String deviceCode, List<String> sosNumberList) {
        if (CollUtil.isEmpty(sosNumberList)) {
            throw new ServiceException("号码列表不能为空");
        }
        if (sosNumberList.size() > 3) {
            throw new ServiceException("号码数量不能超过3个");
        }
        String sosNumbers = CollUtil.join(sosNumberList, ",");
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.SOS, sosNumbers);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 设置上传数据时间间隔
     *
     * @param deviceCode 设备编号
     * @param period     上传时间间隔,单位是秒
     * @return
     */
    @Override
    public void setUploadDataFrequency(String deviceCode, Integer period) {
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.UPLOAD, String.valueOf(period));
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 批量设置租户所有手表的上传数据时间间隔
     *
     * @param tenantId 租户ID
     * @param period   上传时间间隔,单位是秒
     * @return
     */
    @Override
    public void setUploadDataFrequencyForTenant(String tenantId, Integer period) {
        //查询该租户下所有手表
        List<DeviceInfo> watchList = deviceInfoService.list(new LambdaQueryWrapper<DeviceInfo>()
                .eq(TenantEntity::getTenantId, tenantId)
                .eq(DeviceInfo::getDeviceStatus, 0));
        if (CollUtil.isNotEmpty(watchList)) {
            watchList.stream().forEach(watchInfo -> {
                this.setUploadDataFrequency(watchInfo.getDeviceCode(), period);
            });
        }
    }

    /**
     * 设置通讯录电话本,最多5组号码
     *
     * @param deviceCode
     * @param phoneBookList
     * @return
     */
    @Override
    public void setPhoneBook(String deviceCode, List<PhoneBookDTO> phoneBookList) {
        if (CollUtil.isEmpty(phoneBookList)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        phoneBookList.stream().forEach(phoneBookDTO -> {
            sb.append(phoneBookDTO.getPhoneNumber() + ",");
            String contactName = phoneBookDTO.getContactName();
            if (CommonUtil.isContainChinese(contactName)) {
                String unicodeName = UnicodeUtil.toUnicode(contactName, true).replaceAll("\\\\u", "");
                sb.append(unicodeName + ",");
            } else {
                sb.append(contactName + ",");
            }
        });
        String commandContent = sb.substring(0, sb.lastIndexOf(","));
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.PHONE_BOOK, commandContent);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 设置通讯录电话本,最多5组号码
     *
     * @param deviceCode 设备编号
     * @param phoneBook  号码对象
     * @return
     */
    @Override
    public void setPhoneBook(String deviceCode, PhoneBookDTO phoneBook) {
        List<PhoneBookDTO> phoneBookList = new ArrayList<PhoneBookDTO>();
        phoneBookList.add(phoneBook);
        this.setPhoneBook(deviceCode, phoneBookList);
    }

    /**
     * 指定手表拨打目标电话
     *
     * @param deviceCode      设备编号
     * @param destPhoneNumber 目标号码
     */
    @Override
    public void call(String deviceCode, String destPhoneNumber) {
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.CALL, destPhoneNumber);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 获取手表定位
     *
     * @param deviceCode 设备编号
     */
    @Override
    public void getLocation(String deviceCode) {
        String msgBody = buildClientMessage(deviceCode, WatchCommandConst.GET_LOCATION, null);
        SocketClient.sendToServer(msgBody);
    }

    /**
     * 当前登录用户的手表拨打目标号码
     *
     * @param currentUser  当前登录的用户
     * @param destPersonId 目标员工ID
     */
    @Override
    public void call(BladeUser currentUser, Long destPersonId) {
        PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(currentUser.getUserId());
        if (personUserRel == null || personUserRel.getId() == null) {
            throw new ServiceException("当前操作员还没绑定员工");
        }
        //查询操作员绑定的手表信息
        Long adminPersonId = personUserRel.getPersonId();
        DeviceInfo adminWatch = getBindWatch(adminPersonId);
        if (adminWatch == null || adminWatch.getId() == null) {
            throw new ServiceException("当前操作员没有绑定手表");
        }
        SimInfo adminSimInfo = simInfoService.getSimByDeviceId(adminWatch.getId());
        if (adminSimInfo == null || adminSimInfo.getId() == null) {
            throw new ServiceException("当前操作员手表没有绑定SIM卡");
        }
        if (adminSimInfo.getSimType().equals(DeviceConstant.SimCardType.CARD_GPRS)) {
            throw new ServiceException("当前操作员手表的SIM卡不支持语音");
        }

        //查询目标员工绑定的手表信息及号码信息
        Person person = PersonCache.getPersonById(currentUser.getTenantId(), destPersonId);
        DeviceInfo personWatch = getBindWatch(destPersonId);
        if (personWatch == null || personWatch.getId() == null) {
            throw new ServiceException("被叫员工没有绑定手表");
        }
        SimInfo personSimInfo = simInfoService.getSimByDeviceId(personWatch.getId());
        if (personSimInfo == null || personSimInfo.getId() == null) {
            throw new ServiceException("被叫员工手表没有绑定SIM卡");
        }
        if (adminSimInfo.getSimType().equals(DeviceConstant.SimCardType.CARD_GPRS)) {
            throw new ServiceException("被叫员工手表的SIM卡不支持语音");
        }

        //给主叫手表上设置一下电话本,把被叫号码设置进去，否则无法拨出
        PhoneBookDTO callingPhoneBook = new PhoneBookDTO(personSimInfo.getSimNumber(), person.getPersonName());
        this.setPhoneBook(adminWatch.getDeviceCode(), callingPhoneBook);

        //给被叫号码手表上设置一下电话本,把主叫号码设置进去，否则无法接听
        PhoneBookDTO calledPhoneBook = new PhoneBookDTO(adminSimInfo.getSimNumber(), currentUser.getUserName());
        this.setPhoneBook(personWatch.getDeviceCode(), calledPhoneBook);

        //发起打电话
        this.call(adminWatch.getDeviceCode(), personSimInfo.getSimNumber());
    }

    /**
     * 校验是否可以呼叫目标用户的手表
     *
     * @param destPersonId 目标员工ID
     */
    @Override
    public boolean isCanCall(Long destPersonId) {
        //查询目标员工绑定的手表信息及号码信息
        DeviceInfo watchDevice = getBindWatch(destPersonId);
        if (watchDevice == null || watchDevice.getId() == null) {
            throw new ServiceException("被叫员工没有绑定手表");
        }
        SimInfo simInfo = simInfoService.getSimByDeviceId(watchDevice.getId());
        if (simInfo == null || simInfo.getId() == null) {
            throw new ServiceException("被叫员工手表没有绑定SIM卡");
        }
        if (simInfo.getSimType().equals(DeviceConstant.SimCardType.CARD_GPRS)) {
            throw new ServiceException("被叫员工手表的SIM卡不支持语音");
        }
        return true;
    }

    /**
     * 根据人员ID查询绑定的手表
     *
     * @param personId
     * @return
     */
    private DeviceInfo getBindWatch(Long personId) {
        List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelsByEntity(personId, 5L);
        if (CollUtil.isEmpty(deviceRelList)) {
            return null;
        }
        Long deviceId = deviceRelList.get(0).getDeviceId();
        DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
        return deviceInfo;
    }

    /**
     * 构建需要发送给手表的消息
     *
     * @param deviceCode
     * @param command
     * @param commandContent
     * @return
     */
    public static String buildClientMessage(String deviceCode, String command, String commandContent) {
        //计算内容长度
        int commandContentLen = 0;
        if (StringUtils.isNotBlank(commandContent)) {
            commandContentLen = (command + "," + commandContent).length();
        } else {
            commandContentLen = command.length();
        }
        //将10进制转换为16进制
        String msgLen = String.format("%08x", commandContentLen);
        msgLen = msgLen.substring(4, msgLen.length());
        String msgBody = null;
        if (StringUtils.isNotBlank(commandContent)) {
            msgBody = "3G" + "*" + deviceCode + "*" + msgLen + "*" + command + "," + commandContent;
        } else {
            msgBody = "3G" + "*" + deviceCode + "*" + msgLen + "*" + command;
        }
        return "SEND[" + msgBody + "]";
    }
}
