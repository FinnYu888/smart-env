package com.ai.apac.smartenv.websocket.controller;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.common.GetPersonPositionDTO;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonStatusCntVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.service.IDeviceService;
import com.ai.apac.smartenv.websocket.service.IPersonService;
import com.ai.apac.smartenv.websocket.service.IVehicleService;
import com.ai.apac.smartenv.websocket.service.impl.WebSocketTaskService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.ai.smartenv.cache.util.SmartCache;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * VehicleController
 *
 * @author qianlong
 */
@RestController
@Api("人员管理")
@Slf4j
public class PersonController extends BladeController {

    public static final String GET_PERSON_STATUS_CNT = "person.getPersonStatusCnt";
    public static final String GET_PERSON_POSITION = "person.getPersonPosition";
    public static final String GET_PERSON_POSITION_STATUS = "person.getPersonPositionByStatus";
    public static final String GET_PERSON_INFO_REALTIME = "person.getPersonInfoRealTime";
    public static final String GET_PERSON_TRACK_REALTIME = "person.getPersonTrackRealTime";
    public static final String FINISH_TASK_BY_ID = "person.finishTaskById";
    public static final String FINISH_TASK_BY_TYPE = "person.finishTaskByType";


    @Autowired
    private IVehicleService vehicleService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private WebSocketTaskService websocketTaskService;

    @Autowired
    private SimpMessagingTemplate wsTemplate;


    @Autowired
    private IDeviceService deviceService;

    /**
     * 获取人员实时状态统计
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_PERSON_STATUS_CNT)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp<PersonStatusCntVO> getPersonStatusCnt(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_PERSON_STATUS_CNT, WebSocketConsts.PUSH_PERSON_MONITOR,
                "0/5 * * * * ?", null);
        personService.pushPersonStatus(task);
        PersonStatusCntVO personStatusCntVO = new PersonStatusCntVO();
        personStatusCntVO.setTopicName(WebSocketConsts.PUSH_PERSON_MONITOR);
        personStatusCntVO.setActionName(GET_PERSON_STATUS_CNT);
        BaseWebSocketResp<PersonStatusCntVO> result = BaseWebSocketResp.data(personStatusCntVO);
        return result;
    }

    /**
     * 根据状态获取人员实时位置
     *
     * @param headerAccessor
     * @return
     */
//    @MessageMapping(GET_PERSON_POSITION_STATUS)
//    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
//    public BaseWebSocketResp<PersonMonitorVO> getPersonPositionByStatus(@Payload Integer status,
//                                                                        SimpMessageHeaderAccessor headerAccessor) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("status", status);
//        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
//                GET_PERSON_POSITION_STATUS, WebSocketConsts.PUSH_PERSON_MONITOR,
//                "0/5 * * * * ?", params);
//        personService.pushPersonPositionByStatus(task);
//        PersonMonitorVO personMonitorVO = new PersonMonitorVO();
//        personMonitorVO.setTopicName(WebSocketConsts.PUSH_PERSON_MONITOR);
//        personMonitorVO.setActionName(GET_PERSON_POSITION_STATUS);
//        BaseWebSocketResp<PersonMonitorVO> result = BaseWebSocketResp.data(personMonitorVO);
//        return result;
//    }

    /**
     * 获取人员实时位置
     *
     * @param request
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_PERSON_POSITION)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp<PersonMonitorVO> getPersonPosition(@Payload GetPersonPositionDTO request,
                                                                SimpMessageHeaderAccessor headerAccessor) {
        log.info("GET_PERSON_POSITION Request:{}", JSON.toJSONString(request));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personIds", request.getPersonIds());
        params.put("status", request.getStatus());
        params.put("workareaIds", request.getWorkareaIds());
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_PERSON_POSITION, WebSocketConsts.PUSH_PERSON_MONITOR,
                "0/5 * * * * ?", params);
        personService.pushPersonPosition(task);

        PersonMonitorVO personMonitorVO = new PersonMonitorVO();
        personMonitorVO.setTopicName(WebSocketConsts.PUSH_PERSON_MONITOR);
        personMonitorVO.setActionName(GET_PERSON_POSITION);
        BaseWebSocketResp<PersonMonitorVO> result = BaseWebSocketResp.data(personMonitorVO);
        return result;
    }

    /**
     * 实时获取人员信息,对应客户端的弹窗
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_PERSON_INFO_REALTIME)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp getPersonInfoRealTime(@Payload String personId,
                                                   SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personId", personId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_PERSON_INFO_REALTIME, WebSocketConsts.PUSH_PERSON_MONITOR,
                "0/5 * * * * ?", params);
        //客户端需要立即显示实时信息,所以这边要立即查询,然后才会进入定时任务
        String tenantId = SmartCache.hget(CacheNames.PERSON_TENANT_MAP, Long.valueOf(personId));
        PersonDetailVO personDetailVO = personService.getPersonDetailRealTime(personId, tenantId);
        String sessionId = headerAccessor.getSessionId();
        wsTemplate.convertAndSendToUser(task.getSessionId(), task.getTopic(), JSONUtil.toJsonStr(personDetailVO), WebSocketUtil.createHeaders(sessionId));
        personService.pushPersonDetail(task);
        return BaseWebSocketResp.status(true);
    }

    /**
     * 准实时获取车辆轨迹(当天)
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_PERSON_TRACK_REALTIME)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp getPersonTrackRealTime(@Payload String personId,
                                                    SimpMessageHeaderAccessor headerAccessor) {
        log.debug("getVehicleTrackRealTime personId:{}", personId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personId", personId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_PERSON_TRACK_REALTIME, WebSocketConsts.PUSH_PERSON_MONITOR,
                "0/5 * * * * ?", params);
        personService.pushPersonTrackRealTime(task);
        return BaseWebSocketResp.status(true);
    }

    /**
     * 根据任务ID删除定时任务
     */
    @MessageMapping(FINISH_TASK_BY_ID)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp finishTaskById(@Payload String taskId,
                                            SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getNativeHeader("userId").get(0);
        return BaseWebSocketResp.status(websocketTaskService.deleteTaskById(userId, taskId));
    }

    /**
     * 根据任务名称删除定时任务
     */
    @MessageMapping(FINISH_TASK_BY_TYPE)
    @SendTo(WebSocketConsts.PUSH_PERSON_MONITOR)
    public BaseWebSocketResp finishTaskByType(@Payload String taskType,
                                              SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getNativeHeader("userId").get(0);
        websocketTaskService.deleteSameTask(userId, headerAccessor.getSessionId(), taskType);
        return BaseWebSocketResp.status(true);
    }


    @PostMapping("/getPersonPosition")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询人员实时位置", notes = "查询人员实时位置")
    public R<PersonMonitorVO> getPersonPosition(@RequestBody GetPersonPositionDTO getPersonPositionDTO) {

        R<PersonMonitorVO> personPosition = personService.getPersonPosition(getPersonPositionDTO);

        return personPosition;
    }

    @GetMapping("/getPersonInfo/{personId}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询实时信息", notes = "查询人员实时信息")
    public R<PersonDetailVO> getPersonInfo(@PathVariable String personId) {
        PersonDetailVO personDetailInfo = personService.getPersonDetailInfo(personId);
        if (personDetailInfo != null) {
            return R.data(personDetailInfo);
        }
        return R.fail("查询失败");
    }

}
