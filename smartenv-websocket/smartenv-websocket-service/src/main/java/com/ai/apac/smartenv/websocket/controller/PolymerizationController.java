package com.ai.apac.smartenv.websocket.controller;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.BasicPolymerizationDetailDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationConditionDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IPolymerizationService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api("综合监控")
@Slf4j
public class PolymerizationController {


    /**
     * 综合监控所有实体位置
     */
    public static final String GET_ALL_ENTITY_POSITION = "polymerization.getAllEntityPosition";


    /**
     * 综合监控详情数据
     */
    public static final String GET_ENTITY_DETAIL = "polymerization.getEntityDetail";


    /**
     * 综合监控汇总数据
     */
    public static final String GET_ALL_ENTITY_COUNT = "polymerization.getAllEntityCount";


    @Autowired
    private IPolymerizationService polymerizationService;



    @MessageMapping(GET_ALL_ENTITY_COUNT)
    @SendTo(WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS)
    public BaseWebSocketResp<PolymerizationCountVO> getAllEntityCount(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_ALL_ENTITY_COUNT, WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS,
                "0/5 * * * * ?", null);
        polymerizationService.pushPolymerizationEntityCount(task);
        PolymerizationCountVO polymerizationCountVO = new PolymerizationCountVO();
        polymerizationCountVO.setTopicName(WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS);
        polymerizationCountVO.setActionName(GET_ALL_ENTITY_COUNT);

        BaseWebSocketResp<PolymerizationCountVO> result = BaseWebSocketResp.data(polymerizationCountVO);
        return result;
    }


    /**
     * 获取聚合页面所有实体的位置
     *
     * @param message
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_ALL_ENTITY_POSITION)
    @SendTo(WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS)
    public BaseWebSocketResp<List<PolymerizationDTO>> getAllEntityPosition(@Payload String message,
                                                                           SimpMessageHeaderAccessor headerAccessor) {
        PolymerizationConditionDTO request = JSON.parseObject(message, PolymerizationConditionDTO.class);

        Map<String, Object> params=new HashMap<>();
        params.put("conditionDTO",request);
        WebsocketTask websocketTask=WebSocketUtil.buildTask(headerAccessor,GET_ALL_ENTITY_POSITION,WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS,"0/5 * * * * ?",params);

        polymerizationService.pushPolymerizationEntityList(websocketTask);

        BaseWebSocketResp<List<PolymerizationDTO>> result = BaseWebSocketResp.data(null);
        return result;
    }

    /**
     * 获取单个实体的详细信息
     *
     * @param request
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_ENTITY_DETAIL)
    @SendTo(WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS)
    public BaseWebSocketResp<BasicPolymerizationDetailDTO> getEntityDetail(@Payload BasicPolymerizationDetailDTO request,
                                                                           SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params=new HashMap<>();
        params.put("entityType",request.getEntityType());
        params.put("entityId",request.getEntityId());
        WebsocketTask websocketTask=WebSocketUtil.buildTask(headerAccessor,GET_ENTITY_DETAIL,WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS,"0/5 * * * * ?",params);

        polymerizationService.pushPolymerizationEntityDetail(websocketTask);

        BaseWebSocketResp<BasicPolymerizationDetailDTO> result = BaseWebSocketResp.data(null);
        return result;
    }
}
