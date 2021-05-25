package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationConditionDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.BasicPolymerizationDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.List;

public interface IPolymerizationService {


    void updatePolymerizationCountRedis(String tenantId, String entityType);

    /**
     * 推送大屏全量统计数据
     * @param websocketTask
     */
    void pushPolymerizationEntityCount(WebsocketTask websocketTask);

    /**
     * 获取大屏全量统计数据用于推送
     * @param tenantId
     */
    PolymerizationCountVO getPolymerizationEntityCount(String tenantId);

    void pushPolymerizationEntityList(WebsocketTask websocketTask);

    void pushPolymerizationEntityDetail(WebsocketTask websocketTask);

    List<PolymerizationDTO> getPersonPolymerization(PolymerizationConditionDTO conditionDTO,String tenantId);

    List<PolymerizationDTO> getVehiclePolymerization(PolymerizationConditionDTO conditionDTO,String tenantId);

    /**
     * 根据条件取中转站聚合数据
     *
     * @param conditionDTO
     * @param tenantId
     * @return
     */
    List<PolymerizationDTO> getTransferStationPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId);

    /**
     * 根据条件取垃圾桶聚合数据
     *
     * @param conditionDTO
     * @param tenantId
     * @return
     */
    List<PolymerizationDTO> getAshcanPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId);

    List<PolymerizationDTO> getEventPolymerization(PolymerizationConditionDTO conditionDTO,String tenantId);

    List<PolymerizationDTO> getToiletPolymerization(PolymerizationConditionDTO conditionDTO, String tenantId);

    BasicPolymerizationDetailDTO getPersonPolymerizationDetail(Long entityId);

    BasicPolymerizationDetailDTO getVehiclePolymerizationDetail(Long entityId, String tenantId);

    BasicPolymerizationDetailDTO getTransferStationPolymerizationDetail(Long entityId);

    BasicPolymerizationDetailDTO getAshcanPolymerizationDetail(Long entityId);

    BasicPolymerizationDetailDTO getEventPolymerizationDetail(Long entityId);

    BasicPolymerizationDetailDTO getToiletPolymerizationDetail(Long entityId);
}
