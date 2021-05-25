package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/16 4:28 下午
 **/
public interface IWebSocketTaskService {

    /**
     * 创建一个基本的websocket 的task
     * @param websocketTask
     * @return
     */
    WebsocketTask createTask(WebsocketTask websocketTask);


    /**
     * 创建一个当前Task的子任务，任务中包含实体ID。用于推送具体Entity的时候使用（比如：车辆位置，人员位置）
     * @param websocketTask
     * @param entityId
     * @return
     */
    Boolean createEntityTask(WebsocketTask websocketTask, String entityId);

    Boolean createEasyVTask(WebsocketTask websocketTask);

    List<WebsocketTask> getWebsocketEasyVTask(String taskType);

    List<WebsocketTask> getTenantTasksByTypes(String tenantId, String taskType);

    List<WebsocketTask> getTenantEntityTasksByTypes(String tenantId, String taskType, String entityId);

    void deleteSameTask(String sessionId, String tenantId, String taskType);




    boolean deleteTaskById(String userId, String taskId);

//    /**
//     * 根据用户ID查询任务
//     *
//     * @param userId
//     * @return
//     */
//    List<WebsocketTask> getTaskByUserId(String userId);


    /**
     * 清空所有任务
     */
    boolean clearTask();


    /**
     * 创建entity 对应的任务
     * @param taskDto
     */
//    void createEntityTask(EntityTaskDto taskDto);

//    void createTask(String key,EntityTaskDto taskDto);
//
//    List<EntityTaskDto> getTasks(String keyParam);
//
//
//    List<EntityTaskDto> getEntityTask(@NotNull String entityId, @NotNull String entityType, @NotNull String taskType);

    String getOssObjLink(String objectName);
}
