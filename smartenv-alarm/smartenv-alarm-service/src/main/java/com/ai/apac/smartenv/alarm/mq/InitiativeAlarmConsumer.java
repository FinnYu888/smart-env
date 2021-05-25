package com.ai.apac.smartenv.alarm.mq;

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.entity.MinicreateAttach;
import com.ai.apac.smartenv.alarm.service.IAlarmInfoService;
import com.ai.apac.smartenv.alarm.service.IMinicreateAttachService;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@EnableBinding(InitiativeAlarmConsumerSource.class)
public class InitiativeAlarmConsumer {

    @Autowired
    private IAlarmInfoService alarmInfoService;

    @Autowired
    private IMinicreateAttachService minicreateAttachService;

    @Autowired
    private IDataChangeEventClient dataChangeEventClient;

    @StreamListener(InitiativeAlarmConsumerSource.MINICREATE_INITIATIVE_ALARM_INPUT)
    public void onGpsInfo(@Payload String  message) {
        log.info("[onGpsInfo][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        JSONObject gpsInfo = JSON.parseObject(message);
        String uuid = gpsInfo.getString("id");
        if (StringUtils.isBlank(uuid)) {
            return;
        }
        JSONObject dsm = gpsInfo.getJSONObject("dsm_alarm_info");
        JSONObject adas = gpsInfo.getJSONObject("adas_alarm_info");
        if (dsm == null && adas == null) {
            return;
        }
        LambdaQueryWrapper<AlarmInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmInfo::getUuid, uuid);
        AlarmInfo aRecord = alarmInfoService.getOne(queryWrapper);
        // 没有才插入
        if (aRecord == null) {
            try {
                try {
                    double latitude = gpsInfo.getDoubleValue("latitude");
                    double longitude = gpsInfo.getDoubleValue("longitude");
                    double[] doubles = GPSUtil.gps84_To_Gcj02(latitude, longitude);
                    gpsInfo.put("originLatitude", gpsInfo.getString("latitude"));
                    gpsInfo.put("originLongitude", gpsInfo.getString("longitude"));
                    gpsInfo.put("latitude", String.valueOf(doubles[0]));
                    gpsInfo.put("longitude", String.valueOf(doubles[1]));
                } catch (Exception e) {
                    log.error("[onGpsInfo] 坐标转换异常，消息内容：[{}]", gpsInfo.toJSONString());
                }
                if (dsm != null) {
                    String time = dsm.getString("time");
                    gpsInfo.put("eventTime", time);
                } else if (adas != null) {
                    String time = adas.getString("time");
                    gpsInfo.put("eventTime", time);
                }
                alarmInfoService.handlerInitiativeAlarm(gpsInfo);
            } catch (Exception e) {
                log.error("[onGpsInfo] 消息内容:[{}], 错误内容:[{}]", gpsInfo.toJSONString(), e.getMessage());
            }
        }
    }

    @StreamListener(InitiativeAlarmConsumerSource.MINICREATE_INITIATIVE_ALARM_ATTACHMENT_INPUT)
    public void onAttachmentInfo(@Payload String message) {
        log.info("[onAttachmentInfo][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        JSONObject initiativeAlarmInfo = JSON.parseObject(message);
        JSONArray attachments = initiativeAlarmInfo.getJSONArray("attachements");
        String uuid = initiativeAlarmInfo.getString("uuid");
        List<MinicreateAttach> attachList = new ArrayList<>();
        for (Object attachment : attachments) {
            JSONObject attach = (JSONObject)JSON.toJSON(attachment);
            String filename = attach.getString("filename");
            // 不记录.bin格式结尾的告警
            if (StrUtil.isNotBlank(filename) && !filename.endsWith(".bin")) {
                MinicreateAttach minicreateAttach = new MinicreateAttach();
                minicreateAttach.setUuid(uuid);
                minicreateAttach.setAttType(attach.getInteger("att_type"));
                minicreateAttach.setSize(attach.getInteger("size"));;
                minicreateAttach.setFileName(attach.getString("filename"));
                minicreateAttach.setFileUrl(attach.getString("fileurl"));
                attachList.add(minicreateAttach);
            }
        }
        if (CollectionUtils.isNotEmpty(attachList)) {
            minicreateAttachService.saveBatch(attachList);
        }
    }
}
