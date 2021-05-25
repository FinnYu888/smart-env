package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.PersonController;
import com.ai.apac.smartenv.websocket.controller.VehicleController;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.GetBigScreenDto;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.service.IWebsocketTriggerService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WebsocketTriggerService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2021/1/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2021/1/4  15:27    panfeng          v1.0.0             修改原因
 */
@Service
@AllArgsConstructor
@Slf4j

public class WebsocketTriggerService implements IWebsocketTriggerService {


    private SimpMessagingTemplate wsTemplate;


    private MongoTemplate mongoTemplate;


    private IWebSocketTaskService webSocketTaskService;


    @Override
    public Boolean cangZScreenPosition(GetBigScreenDto bigScreenDto) {
        if (bigScreenDto.getEntityType() == null) {
            rePushEasyVVehiclePosition(bigScreenDto,false);
            rePushEasyVPersonPosition(bigScreenDto,false);
        } else if (bigScreenDto.getEntityType().equals(CommonConstant.ENTITY_TYPE.VEHICLE)) {
            rePushEasyVVehiclePosition(bigScreenDto,true);
        } else if (bigScreenDto.getEntityType().equals(CommonConstant.ENTITY_TYPE.PERSON)) {
            rePushEasyVPersonPosition(bigScreenDto,true);
        }

        return false;
    }

    private Boolean rePushEasyVPersonPosition(GetBigScreenDto bigScreenDto,Boolean isClear) {

        List<WebsocketTask> websocketEasyVTask = webSocketTaskService.getWebsocketEasyVTask(PersonController.GET_PERSON_POSITION);
        if (CollectionUtil.isEmpty(websocketEasyVTask)){
            return false;
        }


        Query query = new Query();
        Long personPositionId = bigScreenDto.getPersonPositionId();
        String tenantIdStr = bigScreenDto.getTenantId();
        if (StringUtil.isNotBlank(tenantIdStr)) {
            List<String> list = Func.toStrList(tenantIdStr);
            query.addCriteria(Criteria.where("tenantId").in(list));
        }

        Long not=-1L;
        if (personPositionId != null&&!not.equals(personPositionId)) {
            query.addCriteria(Criteria.where("personPositionId").is(personPositionId));
        }
        query.addCriteria(Criteria.where("lat").ne(null));
        query.addCriteria(Criteria.where("deviceStatus").is(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(query, BasicPersonDTO.class);
        List<PersonMonitorInfoVO> personMonitorInfoVOS = basicPersonDTOS.stream().map(basicPersonDTO -> {
            PersonMonitorInfoVO personMonitorInfoVO = new PersonMonitorInfoVO();
            PositionDTO positionDTO = null;
            positionDTO = new PositionDTO();
            positionDTO.setLat(basicPersonDTO.getLat());
            positionDTO.setLng(basicPersonDTO.getLng());
            positionDTO.setTimestamp(System.currentTimeMillis());
            List<PositionDTO> list = new ArrayList();
            list.add(positionDTO);
            positionDTO.setTimestamp(System.currentTimeMillis());
            PersonInfoVO personInfoVO = BeanUtil.copy(basicPersonDTO, PersonInfoVO.class);
            personInfoVO.setDeviceId(basicPersonDTO.getWechatId());
            personInfoVO.setDeviceCode(basicPersonDTO.getWatchDeviceCode());
            personInfoVO.setStatus(basicPersonDTO.getWorkStatus());
            personInfoVO.setStatusName(basicPersonDTO.getWorkStatusName());
            personInfoVO.setIcon(PersonCache.getPersonStatusImg(basicPersonDTO.getWorkStatus()));
            personInfoVO.setPersonId(basicPersonDTO.getId().toString());
            personMonitorInfoVO.setPosition(positionDTO);
            personMonitorInfoVO.setPersonInfo(personInfoVO);
            return personMonitorInfoVO;
        }).filter(personMonitorInfoVO -> personMonitorInfoVO != null).collect(Collectors.toList());


        for (WebsocketTask websocketTask : websocketEasyVTask) {
            PersonMonitorVO personMonitorVO = new PersonMonitorVO();
            personMonitorVO.setTopicName(websocketTask.getTopic());
            personMonitorVO.setActionName(websocketTask.getTaskType());
            personMonitorVO.setTaskId(String.valueOf(websocketTask.getId()));
            personMonitorVO.setPersonList(personMonitorInfoVOS);
            send(websocketTask,R.data(personMonitorVO));
        }

        if (isClear){
            List<WebsocketTask> websocketVehicleEasyVTask = webSocketTaskService.getWebsocketEasyVTask(VehicleController.GET_VEHICLE_POSITION);
            for (WebsocketTask websocketTask : websocketVehicleEasyVTask) {
                VehicleMonitorVO vehicleMonitorVO = new VehicleMonitorVO();
                vehicleMonitorVO.setTopicName(websocketTask.getTopic());
                vehicleMonitorVO.setActionName(websocketTask.getTaskType());
                vehicleMonitorVO.setTaskId(String.valueOf(websocketTask.getId()));
                vehicleMonitorVO.setVehicleList(null);
                send(websocketTask,R.data(vehicleMonitorVO));
            }

        }


        return true;
    }


    private Boolean rePushEasyVVehiclePosition(GetBigScreenDto bigScreenDto,Boolean isClear) {

        List<WebsocketTask> websocketEasyVTask = webSocketTaskService.getWebsocketEasyVTask(VehicleController.GET_VEHICLE_POSITION);
        if (CollectionUtil.isEmpty(websocketEasyVTask)){
            return false;
        }
        Query query = new Query();
        Long vehicleType = bigScreenDto.getVehicleType();
        String tenantIdStr = bigScreenDto.getTenantId();
        if (StringUtil.isNotBlank(tenantIdStr)) {
            List<String> list = Func.toStrList(tenantIdStr);
            query.addCriteria(Criteria.where("tenantId").in(list));
        }
        Long not=-1L;
        if (vehicleType != null&&!not.equals(vehicleType)) {
            query.addCriteria(Criteria.where("entityCategoryId").is(vehicleType));
        }
        query.addCriteria(Criteria.where("lat").ne(null));
        query.addCriteria(Criteria.where("deviceStatus").is(Long.parseLong(DeviceConstant.DeviceStatus.ON)));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        List<VehicleMonitorInfoVO> vehicleDetailVOList = basicVehicleInfoDTOS.stream().map(basicVehicleInfoDTO -> {
            VehicleMonitorInfoVO vehicleDetailVO = new VehicleMonitorInfoVO();
            VehicleInfoVO vehicleInfoVO = BeanUtil.copy(basicVehicleInfoDTO, VehicleInfoVO.class);
            PositionDTO positionDTO = null;
            positionDTO=new PositionDTO();
            positionDTO.setLat(basicVehicleInfoDTO.getLat());
            positionDTO.setLng(basicVehicleInfoDTO.getLng());
            List<PositionDTO> list=new ArrayList();
            list.add(positionDTO);
            positionDTO.setTimestamp(System.currentTimeMillis());
            vehicleInfoVO.setVehicleId(basicVehicleInfoDTO.getId().toString());
            vehicleInfoVO.setDeviceId(String.valueOf(basicVehicleInfoDTO.getGpsDeviceId()));
            vehicleInfoVO.setDeviceCode(String.valueOf(basicVehicleInfoDTO.getGpsDeviceCode()));
            vehicleInfoVO.setStatus(basicVehicleInfoDTO.getWorkStatus());
            vehicleInfoVO.setStatusName(basicVehicleInfoDTO.getWorkStatusName());
            vehicleInfoVO.setIcon(VehicleCache.getVehicleStatusImg(basicVehicleInfoDTO.getWorkStatus()));
            vehicleDetailVO.setVehicleInfo(vehicleInfoVO);
            vehicleDetailVO.setPosition(positionDTO);
            return vehicleDetailVO;
        }).filter(vehicleMonitorInfoVO -> vehicleMonitorInfoVO!=null).collect(Collectors.toList());
        for (WebsocketTask websocketTask : websocketEasyVTask) {
            VehicleMonitorVO vehicleMonitorVO = new VehicleMonitorVO();
            vehicleMonitorVO.setTopicName(websocketTask.getTopic());
            vehicleMonitorVO.setActionName(websocketTask.getTaskType());
            vehicleMonitorVO.setTaskId(String.valueOf(websocketTask.getId()));
            vehicleMonitorVO.setVehicleList(vehicleDetailVOList);
            send(websocketTask,R.data(vehicleMonitorVO));
        }


        if (isClear){
            List<WebsocketTask> websocketPersonEasyVTask = webSocketTaskService.getWebsocketEasyVTask(PersonController.GET_PERSON_POSITION);
            for (WebsocketTask websocketTask : websocketPersonEasyVTask) {
                PersonMonitorVO personMonitorVO = new PersonMonitorVO();
                personMonitorVO.setTopicName(websocketTask.getTopic());
                personMonitorVO.setActionName(websocketTask.getTaskType());
                personMonitorVO.setTaskId(String.valueOf(websocketTask.getId()));
                personMonitorVO.setPersonList(null);
                send(websocketTask,R.data(personMonitorVO));
            }
        }

        return null;
    }


    /**
     * 推送指定任务的消息
     *
     * @param sendContent
     */
    private <U> void send(WebsocketTask websocketTask, R<U> sendContent) {
        if (sendContent == null || sendContent.getData() == null || websocketTask == null || StringUtil.isBlank(websocketTask.getSessionId())) {
            return;
        }
        log.info("发送给客户端实时更新消息,sessionID:" + websocketTask.getSessionId() + ",content:" + JSONUtil.toJsonStr(sendContent));
        wsTemplate.convertAndSendToUser(websocketTask.getSessionId(), websocketTask.getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(websocketTask.getSessionId()));
    }

}
