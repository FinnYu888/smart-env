package com.ai.apac.smartenv.websocket.module.task.dto;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.List;
import java.util.Map;

@Data
@Deprecated
public class EntityTaskDto extends WebsocketTask {


    private static final long serialVersionUID = 1L;
    private List<String> entityIds;
    private Long entityType;




}
