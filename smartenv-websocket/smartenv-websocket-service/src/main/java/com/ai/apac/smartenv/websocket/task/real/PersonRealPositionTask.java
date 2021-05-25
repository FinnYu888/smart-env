package com.ai.apac.smartenv.websocket.task.real;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonPositionVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.task.BaseTask;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.formula.functions.T;
import org.springblade.core.tool.api.R;

import java.util.Date;
import java.util.Map;


public class PersonRealPositionTask extends BaseTask implements Runnable {




    public PersonRealPositionTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void executeInit() {

    }

    public R<PersonPositionVO> execute() {
        Map<String,Object> map = validParams();
        String lat = map.get("lat")==null?null: (String) map.get("lat");
        String lng = map.get("lng")==null?null: (String) map.get("lng");
        String personId = map.get("personId")==null?null: (String) map.get("personId");
        DeviceInfo deviceInfo = map.get("deviceInfo")==null?null: (DeviceInfo) map.get("deviceInfo");
        Integer status = map.get("status")==null?null: (Integer) map.get("status");
        PersonPositionVO personMonitorVO=new PersonPositionVO();
        personMonitorVO.setTopicName(getWebsocketTask().getTopic());
        personMonitorVO.setActionName(getWebsocketTask().getTaskType());
        personMonitorVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
        personMonitorVO.setPersonId(personId);
        personMonitorVO.setLat(lat);
        personMonitorVO.setLng(lng);

        String statusImg = PersonCache.getPersonStatusImg(status);
        personMonitorVO.setIcon(statusImg);
        personMonitorVO.setDeviceCode(deviceInfo.getDeviceCode());

        personMonitorVO.setDeviceId(deviceInfo.getId());


        return R.data(personMonitorVO);
    }

    @Override
    public void send(String sessionId, R sendContent) {
        if (sendContent == null || sendContent.getData() == null) {
            return;
        }
        getWsTemplate().convertAndSendToUser(sessionId, getWebsocketTask().getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(sessionId));

    }


}
