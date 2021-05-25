package com.ai.apac.smartenv.omnic.mq;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.service.IDataChangeEventService;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.mq.IWebsocketConsumer;
import com.ai.apac.smartenv.websocket.mq.dto.DevicePositionMessage;
import com.ai.apac.smartenv.websocket.mq.dto.VehicleLocationMqDto;
import com.ai.apac.smartenv.websocket.mq.dto.WatchLocationMqDto;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;

@Slf4j
@EnableBinding(IWebsocketConsumer.class)
public class PositionConsumer {

    @Autowired
    private IDataChangeEventService dataChangeEventService;


    @Autowired
//    private DevicePositionSubject devicePositionSubject;
    private static Logger logger = LoggerFactory.getLogger(PositionConsumer.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OmnicProducerSource omnicProducerSource;


    @StreamListener(OmnicConsumerSource.WATCH_POSITION_INPUT)
    public void onWatchPositionMessage(@Payload String message) {

        try {
            WatchLocationMqDto positionMessage = JSONObject.parseObject(message, WatchLocationMqDto.class);
//            WatchLocationMqDto positionMessage=new WatchLocationMqDto();
            DevicePositionMessage devicePositionMessage = new DevicePositionMessage();

            devicePositionMessage.setEntityType(CommonConstant.ENTITY_TYPE.PERSON);
            devicePositionMessage.setDeviceCode(positionMessage.getDeviceCode());
            String lat = positionMessage.getLat();
            String lng = positionMessage.getLng();
            if ("W".equals(positionMessage.getLatType())) {
                lat = "-" + lat;
            }
            if ("W".equals(positionMessage.getLngType())) {
                lng = "-" + lng;
            }
            //转为百度坐标系
            double[] bd09 = GPSUtil.gps84_To_bd09(Double.parseDouble(lat), Double.parseDouble(lng));


            devicePositionMessage.setLat(String.valueOf(bd09[0]));
            devicePositionMessage.setLng(String.valueOf(bd09[1]));
            String gpsTime = positionMessage.getGpsTime();
            Date parse = DateUtil.parse(gpsTime, DateUtil.PATTERN_DATETIME_MINI);
            devicePositionMessage.setTime(parse);
            devicePositionMessage.setExtProperties(positionMessage);

            DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(null, positionMessage.getDeviceCode());
            if (deviceInfo == null) {
                return;
            }
            DeviceRel data = DeviceRelCache.getDeviceRelClient().getDeviceRelByDeviceId(deviceInfo.getId()).getData();
            if (data == null || data.getId() == null) {
                return;
            }


            double[] gc02 = GPSUtil.gps84_To_Gcj02(Double.parseDouble(lat), Double.parseDouble(lng));

            Long entityId = data.getEntityId();
            Query query = Query.query(Criteria.where("id").is(entityId));
            Update update = new Update();
            update.set("lat", String.valueOf(gc02[0]));
            update.set("lng",  String.valueOf(gc02[1]));
            update.set("watchBattery", positionMessage.getBattery());
            mongoTemplate.updateMulti(query, update, BasicPersonDTO.class);




            BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO = new BaseWsMonitorEventDTO<>(WsMonitorEventConstant.EventType.PERSON_GPS_EVENT, data.getTenantId(), null, entityId.toString());
            dataChangeEventService.doWebsocketEvent(baseWsMonitorEventDTO);

        } catch (Exception e) {
            logger.warn("发送消息失败", e);
        }

    }

    @StreamListener(OmnicConsumerSource.VEHICLE_POSITION_INPUT)
    public void onVehiclePositionMessage(@Payload String message) {
        VehicleLocationMqDto positionMessage=JSONObject.parseObject(message, VehicleLocationMqDto.class);

        try {
            DevicePositionMessage devicePositionMessage = new DevicePositionMessage();

            devicePositionMessage.setEntityType(CommonConstant.ENTITY_TYPE.PERSON);
            devicePositionMessage.setDeviceCode(positionMessage.getDevice_id());
            String lat = positionMessage.getLatitude();
            String lng = positionMessage.getLongitude();

            double[] bd09 = GPSUtil.gcj02_To_Bd09(Double.parseDouble(lat), Double.parseDouble(lng));

            devicePositionMessage.setLat(String.valueOf(bd09[0]));
            devicePositionMessage.setLng(String.valueOf(bd09[1]));
            String gpsTime = positionMessage.getGps_time();
            Date parse = DateUtil.parse(gpsTime, DateUtil.PATTERN_DATETIME_MINI);
            devicePositionMessage.setTime(parse);
            devicePositionMessage.setExtProperties(positionMessage);




            DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(null, positionMessage.getDevice_id());
            if (deviceInfo == null) {
                return;
            }
            DeviceRel data = DeviceRelCache.getDeviceRelClient().getDeviceRelByDeviceId(deviceInfo.getId()).getData();
            if (data == null || data.getId() == null) {
                return;
            }


            double[] gc02 = GPSUtil.bd09_To_Gcj02(bd09[0], bd09[1]);
            Long entityId = data.getEntityId();
            Query query = Query.query(Criteria.where("id").is(entityId));
            Update update = new Update();
            update.set("lat", Double.parseDouble(lat));
            update.set("lng",  Double.parseDouble(lng));
            mongoTemplate.updateMulti(query, update, BasicVehicleInfoDTO.class);

            //发送websocket消息
            BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO=new BaseWsMonitorEventDTO<>();
            baseWsMonitorEventDTO.setEventObject(entityId.toString());
            baseWsMonitorEventDTO.setEventType(WsMonitorEventConstant.EventType.VEHICLE_GPS_EVENT);
            baseWsMonitorEventDTO.setTenantId(data.getTenantId());
            Message<BaseWsMonitorEventDTO<String>> msg= MessageBuilder.withPayload(baseWsMonitorEventDTO).build();
            omnicProducerSource.websocketMonitorEvent().send(msg);


        } catch (Exception e) {
            logger.warn("发送消息失败", e);
        }

    }

}
