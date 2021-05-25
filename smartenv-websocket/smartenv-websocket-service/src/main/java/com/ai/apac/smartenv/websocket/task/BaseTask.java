package com.ai.apac.smartenv.websocket.task;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.ResultCodeConstant;
import com.ai.apac.smartenv.common.enums.BizResultCode;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.*;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.IoUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author qianlong
 * @description 抽象类, 基本模板方法实现
 * @Date 2020/2/17 7:27 上午
 **/
@Slf4j
public abstract class BaseTask<T> implements Serializable, Runnable {

    private static BladeRedis bladeRedisCache = null;

    private static SimpMessagingTemplate wsTemplate = null;

    private static IUserClient userClient = null;

    private static IDeviceService deviceService = null;

    private static IResOrderService resOrderService = null;


    private static IVehicleService vehicleService = null;

    private static IPersonService personService = null;

    private static IScheduleService scheduleService = null;

    private static IMessageService messageService = null;

    private static IAlarmService alarmService = null;

    private static IEventService eventService = null;

    private static IWorkAreaService workAreaService = null;

    private static IFacilityService facilityService = null;

    private static ITaskService taskService = null;

    private static IOmnicService omnicService = null;

    private static IWebSocketTaskService webSocketTaskService = null;
    private static IPolymerizationService polymerizationService = null;
    private static IBaseService baseService = null;


    @Getter
    @Setter
    private WebsocketTask websocketTask;

    @Getter
    @Setter
    private String tenantId;

    public BaseTask(WebsocketTask websocketTask) {
        this.websocketTask = websocketTask;
    }

    protected static BladeRedis getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedisCache;
    }


    protected static SimpMessagingTemplate getWsTemplate() {
        if (wsTemplate == null) {
            wsTemplate = SpringUtil.getBean(SimpMessagingTemplate.class);
        }
        return wsTemplate;
    }

    protected static IUserClient getUserClient() {
        if (userClient == null) {
            userClient = SpringUtil.getBean(IUserClient.class);
        }
        return userClient;
    }

    protected static IVehicleService getVehicleService() {
        if (vehicleService == null) {
            vehicleService = SpringUtil.getBean(IVehicleService.class);
        }
        return vehicleService;
    }

    protected static IResOrderService getResOrderService() {
        if (resOrderService == null) {
            resOrderService = SpringUtil.getBean(IResOrderService.class);
        }
        return resOrderService;
    }


    protected static IDeviceService getDeviceService() {
        if (deviceService == null) {
            deviceService = SpringUtil.getBean(IDeviceService.class);
        }
        return deviceService;
    }

    protected static ITaskService getTaskService() {
        if (taskService == null) {
            taskService = SpringUtil.getBean(ITaskService.class);
        }
        return taskService;
    }

    protected static IPersonService getPersonService() {
        if (personService == null) {
            personService = SpringUtil.getBean(IPersonService.class);
        }
        return personService;
    }

    protected static IWorkAreaService getWorkAreaService() {
        if (workAreaService == null) {
            workAreaService = SpringUtil.getBean(IWorkAreaService.class);
        }
        return workAreaService;
    }

    protected static IScheduleService getScheduleService() {
        if (scheduleService == null) {
            scheduleService = SpringUtil.getBean(IScheduleService.class);
        }
        return scheduleService;
    }

    protected static IMessageService getMessageService() {
        if (messageService == null) {
            messageService = SpringUtil.getBean(IMessageService.class);
        }
        return messageService;
    }

    protected static IAlarmService getAlarmService() {
        if (alarmService == null) {
            alarmService = SpringUtil.getBean(IAlarmService.class);
        }
        return alarmService;
    }

    protected static IEventService getEventService() {
        if (eventService == null) {
            eventService = SpringUtil.getBean(IEventService.class);
        }
        return eventService;
    }

    protected static IFacilityService getFacilityService() {
        if (facilityService == null) {
            facilityService = SpringUtil.getBean(IFacilityService.class);
        }
        return facilityService;
    }

    protected static IWebSocketTaskService getWebSocketTaskService() {
        if (webSocketTaskService == null) {
            webSocketTaskService = SpringUtil.getBean(IWebSocketTaskService.class);
        }
        return webSocketTaskService;
    }

    protected static IPolymerizationService getPolymerizationService() {
        if (polymerizationService == null) {
            polymerizationService = SpringUtil.getBean(IPolymerizationService.class);
        }
        return polymerizationService;
    }

    public static IOmnicService getOmnicService() {
        if (omnicService == null) {
            omnicService = SpringUtil.getBean(IOmnicService.class);
        }
        return omnicService;
    }

    protected static IBaseService getBaseService() {
        if (baseService == null) {
            baseService = SpringUtil.getBean(IBaseService.class);
        }
        return baseService;
    }

    public void runTask() {
        log.info("Get Request:{}", JSON.toJSONString(websocketTask));
        R<T> result = null;
        try {
            executeInit();
            result = execute();
            send(websocketTask.getSessionId(), result);
        } catch (ServiceException ex) {
            ex.printStackTrace();
            if (ex.getResultCode().getCode() == ResultCodeConstant.WebSocketCode.SESSION_TIME_OUT ||
                    ex.getResultCode().getCode() == ResultCodeConstant.WebSocketCode.TASK_FINISHED) {
                Thread.currentThread().interrupt();
                return;
            }
            result = R.fail(ex.getMessage());
            send(websocketTask.getSessionId(), result);
            return;
        }
    }

    public void executeInit() {
        if (websocketTask == null) {
            throw new ServiceException("The websocket task should not be null!");
        }
        String sessionId = websocketTask.getSessionId();
        String userId = websocketTask.getUserId();
        //获取用户的租户标识
        User user = UserCache.getUser(Long.valueOf(userId));
        //从缓存中获取当前用户所属项目编号
//        String projectCode = getBladeRedisCache().get(CacheNames.USER_ONLINE + StringPool.COLON + userId);
//        this.tenantId = projectCode == null ? user.getTenantId() : projectCode;
        this.tenantId = user.getTenantId();

        Set<String> keys = getBladeRedisCache().keys(WebSocketConsts.CacheNames.CACHE_PREFIX + ":*:" + websocketTask.getId());

        if (CollectionUtil.isEmpty(keys)) {
            throw new ServiceException(BizResultCode.WS_SESSION_TIME_OUT);
        }
        //判断sessionId是否存在,不存在则说明已经断开连接,中断任务执行
        Object sessionIdObj = getBladeRedisCache().get(WebSocketConsts.CacheNames.SESSION_USER + ":" + sessionId);
        if (sessionIdObj == null) {
            //删除用户的任务
//            getBladeRedisCache().hDel(WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":" + userId,
//                    websocketTask.getId());
            throw new ServiceException(BizResultCode.WS_TASK_FINISHED);
        }

    }

    /**
     * 向客户端发送消息
     *
     * @param sessionId
     * @param sendContent
     */
    public void send(String sessionId, R<T> sendContent) {
        if (sendContent == null || sendContent.getData() == null) {
            return;
        }
        //判断taskId是否存在,不存在则说明已经断开连接,中断任务执行
        Set<String> keys = getBladeRedisCache().keys(WebSocketConsts.CacheNames.CACHE_PREFIX + ":*:" + websocketTask.getId() + "*");
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        log.info("Send to client:{}", JSON.toJSONString(sendContent));
        getWsTemplate().convertAndSendToUser(sessionId, getWebsocketTask().getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(sessionId));
    }

    protected Map<String, Object> validParams() {
        Map<String, Object> params = getWebsocketTask().getParams();
        if (params == null || params.size() == 0) {
            params = new HashMap<>();
//            throw new ServiceException("The param  should not be empty!");
        }
        return params;
    }

    /**
     * 具体的执行方法,返回一个待发送的内容对象,由子类实现
     */
    protected abstract R<T> execute();


    @Override
    public void run() {
        runTask();
    }
}
