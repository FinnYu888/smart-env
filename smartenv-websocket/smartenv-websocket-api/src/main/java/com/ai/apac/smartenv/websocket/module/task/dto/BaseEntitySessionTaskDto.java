package com.ai.apac.smartenv.websocket.module.task.dto;

import java.util.List;

public class BaseEntitySessionTaskDto<T extends EntityTaskDto> {

    private String entityId;

    private String entityType;

    private List<T> taskList;


}
