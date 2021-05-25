package com.ai.apac.smartenv.websocket.controller;

import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.GetBigScreenDto;
import com.ai.apac.smartenv.websocket.service.IWebsocketTriggerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WebsocketTriggerController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2021/1/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2021/1/4  14:40    panfeng          v1.0.0             修改原因
 */
@RestController
@RequestMapping("/trigger")
@Api(value = "websocket触发器", tags = "websocket触发器")
@AllArgsConstructor
public class WebsocketTriggerController {

    private IWebsocketTriggerService websocketTriggerService;

    private MongoTemplate mongoTemplate;


    @GetMapping("/cangZScreenPosition")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "沧州大屏触发器", notes = "沧州大屏触发器")
    public R<Boolean> cangZScreenPosition(GetBigScreenDto getBigScreenDto){
        return R.data(websocketTriggerService.cangZScreenPosition(getBigScreenDto));
    }
    @GetMapping("/getPersonAndVehicleTypeByTenant")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取人员车辆类型列表", notes = "沧州大屏触发器")
    public R<Map<String, Object>> getPersonAndVehicleTypeByTenant(@RequestParam String tenantIdStr){
        Map<String,Object> result=new HashMap<>();
        Query personQuery=new Query();
        List<String> list = Func.toStrList(tenantIdStr);
        personQuery.addCriteria(Criteria.where("tenantId").in(list).and("personPositionId").ne(null));
        List<BasicPersonDTO> basicPersonDTOS = mongoTemplate.find(personQuery, BasicPersonDTO.class);
        if (CollectionUtil.isNotEmpty(basicPersonDTOS)){
            List<BasicPersonDTO> results=new ArrayList<>();
            BasicPersonDTO basicVehicleInfoDTO=new BasicPersonDTO();
            basicVehicleInfoDTO.setPersonPositionId(-1L);
            basicVehicleInfoDTO.setPersonPositionName("全部");
            results.add(basicVehicleInfoDTO);
            List<BasicPersonDTO> collect = basicPersonDTOS.stream().collect(Collectors.groupingBy(BasicPersonDTO::getPersonPositionId)).entrySet().stream().map(map -> {
                Long key = map.getKey();
                List<BasicPersonDTO> value = map.getValue();
                BasicPersonDTO basicPersonDTO = new BasicPersonDTO();
                basicPersonDTO.setPersonPositionId(key);
                basicPersonDTO.setPersonPositionName(value.get(0).getPersonPositionName());
                return basicPersonDTO;
            }).collect(Collectors.toList());
            collect = collect.stream().filter(basicPersonDTO -> basicPersonDTO.getPersonPositionName()!=null).collect(Collectors.groupingBy(BasicPersonDTO::getPersonPositionName)).entrySet().stream().map(entiy -> {
                BasicPersonDTO basicPersonDTO = new BasicPersonDTO();
                basicPersonDTO.setPersonPositionId(entiy.getValue().get(0).getPersonPositionId());
                basicPersonDTO.setPersonPositionName(entiy.getKey());
                return basicPersonDTO;
            }).collect(Collectors.toList());

            results.addAll(collect);
            result.put("personPositions",results);
        }

        Query vehicleQuery=new Query();
        vehicleQuery.addCriteria(Criteria.where("tenantId").in(list).and("entityCategoryId").ne(null));
        List<BasicVehicleInfoDTO> basicVehicleInfoDTOS = mongoTemplate.find(vehicleQuery, BasicVehicleInfoDTO.class);
        if (CollectionUtil.isNotEmpty(basicVehicleInfoDTOS)){
            List<BasicVehicleInfoDTO> results=new ArrayList<>();

            BasicVehicleInfoDTO basicVehicleInfoDTO=new BasicVehicleInfoDTO();
            basicVehicleInfoDTO.setEntityCategoryId(-1L);
            basicVehicleInfoDTO.setVehicleTypeName("全部");
            results.add(basicVehicleInfoDTO);
            List<BasicVehicleInfoDTO> collect = basicVehicleInfoDTOS.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getEntityCategoryId)).entrySet().stream().map(map -> {
                Long key = map.getKey();
                List<BasicVehicleInfoDTO> value = map.getValue();
                BasicVehicleInfoDTO basicPersonDTO = new BasicVehicleInfoDTO();
                basicPersonDTO.setEntityCategoryId(key);
                basicPersonDTO.setVehicleTypeName(value.get(0).getVehicleTypeName());
                return basicPersonDTO;
            }).collect(Collectors.toList());
            results.addAll(collect);

            result.put("vehicleTypes",results);
        }

        return R.data(result);
    }



}
