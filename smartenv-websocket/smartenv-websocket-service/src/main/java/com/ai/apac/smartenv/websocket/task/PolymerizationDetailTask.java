package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.BasicPolymerizationDetailDTO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import org.springblade.core.tool.api.R;

import java.util.Map;

public class PolymerizationDetailTask extends BaseTask<BasicPolymerizationDetailDTO> implements Runnable {
    public PolymerizationDetailTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    protected R<BasicPolymerizationDetailDTO> execute() {
        Map<String, Object> params = validParams();
        Integer entityType = params.get("entityType") == null ? null : (Integer) params.get("entityType");
        String entityId = params.get("entityId") == null ? null : (String) params.get("entityId");
        if (entityType==null||entityId==null){
            return R.fail("ID和TYPE不能为空");
        }
        BasicPolymerizationDetailDTO result=new BasicPolymerizationDetailDTO();
        result.setEntityType(entityType);
        result.setEntityId(entityId);

        BasicPolymerizationDetailDTO polymerizationDetailDTO =null;
        if (WebSocketConsts.PolymerizationType.VEHICLE.equals(entityType)){
            polymerizationDetailDTO = getPolymerizationService().getVehiclePolymerizationDetail(Long.parseLong(entityId),getTenantId());
        }else if (WebSocketConsts.PolymerizationType.PERSON.equals(entityType)){
            polymerizationDetailDTO =getPolymerizationService().getPersonPolymerizationDetail(Long.parseLong(entityId));
        }else if (WebSocketConsts.PolymerizationType.EVENT.equals(entityType)){
            polymerizationDetailDTO =getPolymerizationService().getEventPolymerizationDetail(Long.parseLong(entityId));
        }else if (WebSocketConsts.PolymerizationType.ASHCAN.equals(entityType)){
            polymerizationDetailDTO =getPolymerizationService().getAshcanPolymerizationDetail(Long.parseLong(entityId));
        }else if (WebSocketConsts.PolymerizationType.TRANSFER_STATION.equals(entityType)){
            polymerizationDetailDTO =getPolymerizationService().getTransferStationPolymerizationDetail(Long.parseLong(entityId));
        }else if (WebSocketConsts.PolymerizationType.PUBLIC_TOILET.equals(entityType)){
            polymerizationDetailDTO =getPolymerizationService().getToiletPolymerizationDetail(Long.parseLong(entityId));
        }
        if (polymerizationDetailDTO==null){
            return R.fail("根据所得ID与TYPE，不能得到对应实体信息");
        }
        result.setEntityInfo(polymerizationDetailDTO);
        result.setTopicName(getWebsocketTask().getTopic());
        result.setActionName(getWebsocketTask().getTaskType());
        result.setTaskId(String.valueOf(getWebsocketTask().getId()));

        /**
         * 系统做了雪花算法，所有实体ID的类型都是不同的
         */
        getWebSocketTaskService().createEntityTask(getWebsocketTask(),polymerizationDetailDTO.getEntityId());

        // 查询实体对应详情信息
        return R.data(result);
    }
}
