package com.ai.apac.smartenv.websocket.controller;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.common.GetVehiclePositionDTO;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleStatusCntVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IDeviceService;
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
@Api("车辆管理")
@Slf4j
public class VehicleController extends BladeController {

    public static final String GET_VEHICLE_STATUS_CNT = "vehicle.getVehicleStatusCnt";
    public static final String GET_VEHICLE_POSITION = "vehicle.getVehiclePosition";
    public static final String GET_VEHICLE_POSITION_STATUS = "vehicle.getVehiclePositionByStatus";
    public static final String GET_VEHICLE_INFO_REALTIME = "vehicle.getVehicleInfoRealTime";
    public static final String GET_VEHICLE_TRACK_REALTIME = "vehicle.getVehicleTrackRealTime";
    public static final String FINISH_TASK_BY_ID = "vehicle.finishTaskById";
    public static final String FINISH_TASK_BY_TYPE = "vehicle.finishTaskByType";

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @Autowired
    private IVehicleService vehicleService;

    @Autowired
    private WebSocketTaskService websocketTaskService;

    @Autowired
    private IDeviceService deviceService;

    /**
     * 获取车辆实时状态统计
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_VEHICLE_STATUS_CNT)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp<VehicleStatusCntVO> getVehicleStatusCnt(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_VEHICLE_STATUS_CNT, WebSocketConsts.PUSH_VEHICLE_MONITOR,
                "0/5 * * * * ?", null);
        vehicleService.pushVehicleStatus(task);
        VehicleStatusCntVO vehicleCntDTO = new VehicleStatusCntVO();
        vehicleCntDTO.setTopicName(WebSocketConsts.PUSH_VEHICLE_MONITOR);
        vehicleCntDTO.setActionName(GET_VEHICLE_STATUS_CNT);
        BaseWebSocketResp<VehicleStatusCntVO> result = BaseWebSocketResp.data(vehicleCntDTO);
        return result;
    }

    /**
     * 根据状态获取车辆实时位置
     *
     * @param headerAccessor
     * @return
     */
//    @MessageMapping(GET_VEHICLE_POSITION_STATUS)
//    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
//    public BaseWebSocketResp<VehicleMonitorVO> getVehiclePositionByStatus(@Payload Integer status, SimpMessageHeaderAccessor headerAccessor) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("status", status);
//        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
//                GET_VEHICLE_POSITION_STATUS, WebSocketConsts.PUSH_VEHICLE_MONITOR,
//                "0/5 * * * * ?", params);
//        vehicleService.pushVehiclePositionByStatus(task);
//        VehicleMonitorVO vehicleMonitorVO = new VehicleMonitorVO();
//        vehicleMonitorVO.setTopicName(WebSocketConsts.PUSH_VEHICLE_MONITOR);
//        vehicleMonitorVO.setActionName(GET_VEHICLE_POSITION);
//        BaseWebSocketResp<VehicleMonitorVO> result = BaseWebSocketResp.data(vehicleMonitorVO);
//        return result;
//    }

    /**
     * 获取车辆实时位置
     *
     * @param request
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_VEHICLE_POSITION)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp<VehicleMonitorVO> getVehiclePosition(@Payload GetVehiclePositionDTO request,
                                                                  SimpMessageHeaderAccessor headerAccessor) {
        log.info("GetVehiclePosition Request:{}", JSON.toJSONString(request));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleIds", request.getVehicleIds());
        params.put("status", request.getStatus());
        params.put("workareaIds", request.getWorkareaIds());
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_VEHICLE_POSITION, WebSocketConsts.PUSH_VEHICLE_MONITOR,
                "0/5 * * * * ?", params);
        vehicleService.pushVehiclePosition(task);

        VehicleMonitorVO vehicleMonitorVO = new VehicleMonitorVO();
        vehicleMonitorVO.setTopicName(WebSocketConsts.PUSH_VEHICLE_MONITOR);
        vehicleMonitorVO.setActionName(GET_VEHICLE_POSITION);
        BaseWebSocketResp<VehicleMonitorVO> result = BaseWebSocketResp.data(vehicleMonitorVO);
        return result;
    }

    @ApiOperation("向指定的session推送消息")
    @GetMapping("/{sessionId}/pushTest")
    public void test(@PathVariable String sessionId, @RequestParam String content) {
        BaseWebSocketResp<String> result = BaseWebSocketResp.data(content);
        wsTemplate.convertAndSendToUser(sessionId, WebSocketConsts.PUSH_VEHICLE_MONITOR, JSONUtil.toJsonStr(result), WebSocketUtil.createHeaders(sessionId));
    }

    /**
     * 实时获取车辆信息,对应客户端打开的弹窗
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_VEHICLE_INFO_REALTIME)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp<VehicleDetailVO> getVehicleInfoRealTime(@Payload String vehicleId,
                                                                     SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", vehicleId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_VEHICLE_INFO_REALTIME, WebSocketConsts.PUSH_VEHICLE_MONITOR,
                "0/5 * * * * ?", params);
        //客户端需要立即显示实时信息,所以这边要立即查询,然后才会进入定时任务
        String tenantId = SmartCache.hget(CacheNames.VEHICLE_TENANT_MAP, Long.valueOf(vehicleId));
        VehicleDetailVO vehicleDetailVO = vehicleService.getVehicleDetailRealTime(Long.valueOf(vehicleId), tenantId, BaiduMapUtils.CoordsSystem.BD09LL);
        String sessionId = headerAccessor.getSessionId();
        wsTemplate.convertAndSendToUser(task.getSessionId(), task.getTopic(), JSONUtil.toJsonStr(vehicleDetailVO), WebSocketUtil.createHeaders(sessionId));
        vehicleService.pushVehicleDetail(task);
        return BaseWebSocketResp.data(vehicleDetailVO);
    }

    /**
     * 准实时获取车辆轨迹(当天)
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_VEHICLE_TRACK_REALTIME)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp<VehicleDetailVO> getVehicleTrackRealTime(@Payload String vehicleId,
                                                                      SimpMessageHeaderAccessor headerAccessor) {
        log.debug("getVehicleTrackRealTime vehicleId:{}", vehicleId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", vehicleId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_VEHICLE_TRACK_REALTIME, WebSocketConsts.PUSH_VEHICLE_MONITOR,
                "0/5 * * * * ?", params);
        vehicleService.pushVehicleTrackRealTime(task);
        return BaseWebSocketResp.status(true);
    }

    /**
     * 根据任务ID删除定时任务
     */
    @MessageMapping(FINISH_TASK_BY_ID)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp finishTaskById(@Payload String taskId,
                                            SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getNativeHeader("userId").get(0);
        return BaseWebSocketResp.status(websocketTaskService.deleteTaskById(userId, taskId));
    }

    /**
     * 根据任务名称删除定时任务
     */
    @MessageMapping(FINISH_TASK_BY_TYPE)
    @SendTo(WebSocketConsts.PUSH_VEHICLE_MONITOR)
    public BaseWebSocketResp finishTaskByType(@Payload String taskType,
                                              SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getNativeHeader("userId").get(0);
        websocketTaskService.deleteSameTask(userId, headerAccessor.getSessionId(), taskType);
        return BaseWebSocketResp.status(true);
    }


    //*******************临时接口********************

    @PostMapping("/getVehiclePosition")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询车辆实时位置", notes = "查询车辆实时位置")
    public R<VehicleMonitorVO> getVehiclePosition(@RequestBody GetVehiclePositionDTO request) {
        WebsocketTask websocketTask=new WebsocketTask();
        R<VehicleMonitorVO> vehiclesPosition = vehicleService.getVehiclesPosition(request);
        return vehiclesPosition;
    }


    @GetMapping("/getVehicleInfo/{vehicleId}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询车辆实时信息", notes = "查询车辆实时信息")
    public R<VehicleDetailVO> getVehicleInfo(@PathVariable String vehicleId) {
        VehicleDetailVO vehicleDetail = vehicleService.getVehicleDetail(vehicleId);
        if (vehicleDetail!=null){
            return R.data(vehicleDetail);
        }
        return R.fail("查询失败");
    }

}
