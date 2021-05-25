package com.ai.apac.smartenv.device.mq;

import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenGreenAreasDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenTasksDTO;
import com.ai.apac.smartenv.green.dto.mongo.GreenScreenWorkingCountDTO;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;


@Slf4j
@EnableBinding(IDeviceCollcetConsumer.class)
public class DeviceCollcetConsumer {
    private static Logger logger = LoggerFactory.getLogger(DeviceCollcetConsumer.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @StreamListener(IDeviceCollcetConsumer.DEVICE_COLLECT_INPUT)
    public void onDeviceCollcetMessage(@Payload String message) {

        try {
            log.info("onDeviceCollcetMessage start message: " +message);
            GreenScreenDeviceDTO positionMessage = JSONObject.parseObject(message, GreenScreenDeviceDTO.class);
            if(null!=positionMessage&&null!=positionMessage.getDeviceCode()){
                //查询设备DTO
                    try {
                        Query query = new Query();
                        query.addCriteria(Criteria.where("deviceCode").is(positionMessage.getDeviceCode()));
                        Update update = new Update();
                        update.set("indexList",positionMessage.getIndexList());
                        UpdateResult result = mongoTemplate.updateFirst(query,update,OmnicConstant.mongoNmae.DEVICE_DATA);
                        if(null!=result){
                            log.debug("update count={},id={}",result.getMatchedCount(),positionMessage.getDeviceCode());
                            log.debug("onDeviceCollcetMessage update finish");
                        }else{
                            log.debug("update count={},id={}",0,positionMessage.getDeviceCode());
                        }


//                        Query mongoQuery = new Query();
//                        mongoQuery.addCriteria(Criteria.where("deviceCode").is(positionMessage.getDeviceCode()));
//                        GreenScreenDeviceDTO deviceDTO = mongoTemplate.findOne(mongoQuery, GreenScreenDeviceDTO.class,OmnicConstant.mongoNmae.DEVICE_DATA);
//                       if(null!=deviceDTO){
//                           //删除前一个,更新后一个
//                           positionMessage.setDeviceId(deviceDTO.getDeviceId());
//                           positionMessage.setGreenAreaId(deviceDTO.getGreenAreaId());
//                           mongoTemplate.remove(deviceDTO);
//                           mongoTemplate.save(positionMessage,OmnicConstant.mongoNmae.DEVICE_DATA);
//                       }
                    }catch (Exception e) {
                        log.error(e.getMessage(),e);
                    }
            }
        } catch (Exception e) {
            logger.error("发送消息失败", e);
        }

    }
}
