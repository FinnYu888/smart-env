package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class BasicPolymerizationDetailDTO<T extends BasicPolymerizationDetailDTO>  extends WebSocketDTO implements Serializable {


    private String entityId;
    /**
     * @see WebSocketConsts.PolymerizationType
     */
    private Integer entityType;

    private T entityInfo;



}
