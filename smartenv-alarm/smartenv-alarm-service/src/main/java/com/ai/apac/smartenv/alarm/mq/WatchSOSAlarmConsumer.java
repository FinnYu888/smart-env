package com.ai.apac.smartenv.alarm.mq;

import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: WatchSOSAlarmConsumer
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/10     zhaidx           v1.0.0               修改原因
 */
@Component
@Slf4j
@EnableBinding(WatchSOSAlarmConsumerSource.class)
public class WatchSOSAlarmConsumer {

    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private IAlarmRuleInfoService ruleInfoService;

    @Autowired
    private IAlarmInfoService infoService;

    /**
     *
     *
     *
     * {"battery":"100","deviceCode":"1703400465","deviceStatus":16,"deviceStatusName":"SOS告警",
     *  "direction":"0.0","gpsTime":"20200910115454","lat":"31.975417","latType":"N",
     *  "lng":"118.7458667","lngType":"E","speed":"0.00","stepCount":0,"vendor":"365"}
     * @param message
     */

    @StreamListener(WatchSOSAlarmConsumerSource.WATCH_SOS_ALARM_INPUT)
    public void onWatchSOSAlarmMessage(@Payload String message) {

        JSONObject originData = JSON.parseObject(message);
        String deviceCode = originData.getString("deviceCode");
        if (StringUtils.isBlank(deviceCode)) {
            return;
        }
        List<DeviceInfo> deviceInfos = deviceClient.getDeviceByCode(deviceCode).getData();
        if (CollectionUtils.isEmpty(deviceInfos)) {
            return;
        }
        // 取设备的租户ID找对应的告警规则
        String tenantId = deviceInfos.get(0).getTenantId();
        LambdaQueryWrapper<AlarmRuleInfo> ruleQuery = new LambdaQueryWrapper<>();
        ruleQuery.eq(AlarmRuleInfo::getTenantId, tenantId);
        ruleQuery.eq(AlarmRuleInfo::getEntityCategoryCode, AlarmConstant.PERSON_WATCH_SOS_ALARM);
        ruleQuery.eq(AlarmRuleInfo::getStatus, AlarmConstant.Status.YES);
        AlarmRuleInfo watchSOSRule = ruleInfoService.getOne(ruleQuery);
        // 手表告警规则不存在/未开启都不告警
        if (watchSOSRule == null) {
            return;
        }
        // 数据预处理
        JSONObject newDataObj = new JSONObject();
        String latitude = originData.getString("lat");
        String longitude = originData.getString("lng");

        double[] doubles = GPSUtil.gps84_To_Gcj02(Double.parseDouble(latitude), Double.parseDouble(longitude)); //84 -> GC
        newDataObj.put("latitude", String.valueOf(doubles[0]));
        newDataObj.put("longitude", String.valueOf(doubles[1]));
        newDataObj.put("eventTime", originData.getString("gpsTime"));
        newDataObj.put("originData", originData);

        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setDeviceCode(deviceCode);
        alarmInfo.setRuleId(watchSOSRule.getId());
        alarmInfo.setData(newDataObj.toString());
        try {
            infoService.handleBigDataAlarmInfo(alarmInfo);
        } catch (Exception e) {
            log.error("[onWatchSOSAlarmMessage][处理错误：{}, 原始数据：{}]", e.getMessage(), originData.toJSONString());
        }
    }

}
