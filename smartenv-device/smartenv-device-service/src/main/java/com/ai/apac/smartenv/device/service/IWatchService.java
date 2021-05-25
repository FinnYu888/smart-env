package com.ai.apac.smartenv.device.service;

import com.ai.apac.smartenv.device.dto.PhoneBookDTO;
import org.springblade.core.secure.BladeUser;

import java.util.List;

/**
 * @author qianlong
 * @Description 手表服务
 * @Date 2020/5/21 11:00 上午
 **/
public interface IWatchService {

    /**
     * 发送短信消息
     *
     * @param deviceCode
     * @param message
     * @return
     */
    void sendMessage(String deviceCode, String message);

    /**
     * 发送短信消息并转成语音
     *
     * @param deviceCode
     * @param message
     * @return
     */
    void sendMessage2Voice(String deviceCode, String message);

    /**
     * 设置SOS号码
     *
     * @param deviceCode 设备编号
     * @param sosNumber  SOS号码
     * @param serial     SOS号码序号
     * @return
     */
    void setSosNumber(String deviceCode, String sosNumber, Integer serial);

    /**
     * 批量设置SOS号码
     *
     * @param deviceCode    设备编号
     * @param sosNumberList SOS号码列表
     * @return
     */
    void batchSetSosNumber(String deviceCode, List<String> sosNumberList);

    /**
     * 设置上传数据时间间隔
     *
     * @param deviceCode 设备编号
     * @param period     上传时间间隔,单位是秒
     * @return
     */
    void setUploadDataFrequency(String deviceCode, Integer period);

    /**
     * 批量设置租户所有手表的上传数据时间间隔
     *
     * @param tenantId 租户ID
     * @param period 上传时间间隔,单位是秒
     * @return
     */
    void setUploadDataFrequencyForTenant(String tenantId, Integer period);

    /**
     * 设置通讯录电话本,最多5组号码
     *
     * @param deviceCode    设备编号
     * @param phoneBookList 号码对象
     * @return
     */
    void setPhoneBook(String deviceCode, List<PhoneBookDTO> phoneBookList);

    /**
     * 设置通讯录电话本,最多5组号码
     *
     * @param deviceCode 设备编号
     * @param phoneBook  号码对象
     * @return
     */
    void setPhoneBook(String deviceCode, PhoneBookDTO phoneBook);

    /**
     * 指定手表拨打目标电话
     *
     * @param deviceCode      设备编号
     * @param destPhoneNumber 目标号码
     */
    void call(String deviceCode, String destPhoneNumber);

    /**
     * 当前登录用户的手表拨打目标号码
     *
     * @param currentUser  当前登录的用户
     * @param destPersonId 目标员工ID
     */
    void call(BladeUser currentUser, Long destPersonId);

    /**
     * 校验是否可以呼叫目标用户的手表
     *
     * @param destPersonId 目标员工ID
     */
    boolean isCanCall(Long destPersonId);

    /**
     * 获取手表定位
     *
     * @param deviceCode 设备编号
     */
    void getLocation(String deviceCode);
}
