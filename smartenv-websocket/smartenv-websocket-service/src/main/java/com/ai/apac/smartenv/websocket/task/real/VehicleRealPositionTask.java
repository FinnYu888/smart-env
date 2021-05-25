package com.ai.apac.smartenv.websocket.task.real;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonPositionVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehiclePositionVO;
import com.ai.apac.smartenv.websocket.task.BaseTask;
import com.ai.apac.smartenv.websocket.task.VehiclePositionTask;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import org.springblade.core.tool.api.R;

import java.util.Date;
import java.util.Map;

public class VehicleRealPositionTask extends BaseTask implements Runnable {


    public VehicleRealPositionTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    protected R execute() {
        Map<String, Object> map = validParams();
        String lat = map.get("lat") == null ? null : (String) map.get("lat");
        String lng = map.get("lng") == null ? null : (String) map.get("lng");
        String vehicleId = map.get("vehicleId") == null ? null : (String) map.get("vehicleId");
        Integer picStatus = map.get("picStatus") == null ? null : (Integer) map.get("picStatus");
        String picStatusName = map.get("picStatusName") == null ? null : (String) map.get("picStatusName");
        VehiclePositionVO vehiclePositionVO = new VehiclePositionVO();
        DeviceInfo deviceInfo = map.get("deviceInfo")==null?null: (DeviceInfo) map.get("deviceInfo");
        vehiclePositionVO.setTopicName(getWebsocketTask().getTopic());
        vehiclePositionVO.setActionName(getWebsocketTask().getTaskType());
        vehiclePositionVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
        vehiclePositionVO.setVehicleId(vehicleId);
        vehiclePositionVO.setLat(lat);
        vehiclePositionVO.setLng(lng);

//        PicStatus picStatus = new PicStatus();
//        picStatus.setEntityId(vehicleId);
//        picStatus.setPicStatus(VehicleStatusEnum.OFF_LINE.getValue());
//        if (deviceInfo != null && deviceInfo.getId() != null) {
//            Boolean isNeedWork = ScheduleCache.getScheduleClient().checkNeedWork(Long.parseLong(vehicleId), ArrangeConstant.ScheduleObjectEntityType.VEHICLE, new Date()).getData();
//            VehicleStatusEnum status = null;
//            if (!isNeedWork) {
//                status = VehicleStatusEnum.OFF_LINE;
//            } else if (isNeedWork && deviceInfo.getDeviceStatus().equals(0L)) {
//                status = VehicleStatusEnum.ON_LINE;
//            } else {
//                status = VehicleStatusEnum.ONLINE_ALARM;
//            }
//            picStatus.setPicStatus(status.getValue());
//        }
//
//        if(picStatus != null){
//            vehiclePositionVO.setStatus(picStatus.getPicStatus());
//            vehiclePositionVO.setStatusName(PersonStatusEnum.getDescByValue(vehiclePositionVO.getStatus()));
//        }


        vehiclePositionVO.setStatus(picStatus);
        vehiclePositionVO.setStatusName(picStatusName);

        String statusImg = VehicleCache.getVehicleStatusImg(vehiclePositionVO.getStatus());
        vehiclePositionVO.setIcon(statusImg);
        vehiclePositionVO.setDeviceCode(deviceInfo.getDeviceCode());
        vehiclePositionVO.setDeviceId(deviceInfo.getId());

        return R.data(vehiclePositionVO);
    }


    @Override
    public void executeInit() {

    }


    @Override
    public void send(String sessionId, R sendContent) {
        if (sendContent == null || sendContent.getData() == null) {
            return;
        }
        getWsTemplate().convertAndSendToUser(sessionId, getWebsocketTask().getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(sessionId));

    }


}
