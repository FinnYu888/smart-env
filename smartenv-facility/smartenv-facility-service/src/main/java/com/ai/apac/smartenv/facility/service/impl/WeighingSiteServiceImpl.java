package com.ai.apac.smartenv.facility.service.impl;

import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.facility.dto.BasicWeighingSitePolymerizationDTO;
import com.ai.apac.smartenv.facility.dto.WeighingSiteMetaDataDTO;
import com.ai.apac.smartenv.facility.dto.WeighingSiteRecordDTO;
import com.ai.apac.smartenv.facility.service.IWeighingSiteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/3  16:45    panfeng          v1.0.0             修改原因
 */
@Service
@AllArgsConstructor
@Slf4j
public class WeighingSiteServiceImpl implements IWeighingSiteService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    @Async
    public Integer batchImportWeighingSiteData(List<WeighingSiteRecordDTO> weighingSiteRecordDTOS){


        if (CollectionUtil.isEmpty(weighingSiteRecordDTOS)){
            return 0;
        }

        Map<String, List<WeighingSiteRecordDTO>> transportUnitMap = weighingSiteRecordDTOS.stream().filter(weighingSiteRecordDTO -> StringUtil.isNotBlank(weighingSiteRecordDTO.getTransportUnit())).collect(Collectors.groupingBy(WeighingSiteRecordDTO::getTransportUnit));
        Map<String, List<WeighingSiteRecordDTO>> shipperMap = weighingSiteRecordDTOS.stream().filter(weighingSiteRecordDTO -> StringUtil.isNotBlank(weighingSiteRecordDTO.getShipper())).collect(Collectors.groupingBy(WeighingSiteRecordDTO::getShipper));
        List<String> transportUnitList = new ArrayList<>(transportUnitMap.keySet());
        List<String> shipperList = new ArrayList<>(shipperMap.keySet());

        List<WeighingSiteMetaDataDTO> all = mongoTemplate.findAll(WeighingSiteMetaDataDTO.class);
        if (CollectionUtil.isNotEmpty(all)){
            WeighingSiteMetaDataDTO weighingSiteMetaDataDTO = all.get(0);
            List<String> shipperList1 = weighingSiteMetaDataDTO.getShipperList();
            List<String> transportUnitList1 = weighingSiteMetaDataDTO.getTransportUnitList();

            shipperList.addAll(shipperList1);
            shipperList = shipperList.stream().distinct().collect(Collectors.toList());


            transportUnitList.addAll(transportUnitList1);
            transportUnitList = transportUnitList.stream().distinct().collect(Collectors.toList());
        }
        mongoTemplate.dropCollection("WeighingSiteMetaData");
        WeighingSiteMetaDataDTO weighingSiteMetaDataDTO =new WeighingSiteMetaDataDTO();
        weighingSiteMetaDataDTO.setTransportUnitList(transportUnitList);
        weighingSiteMetaDataDTO.setShipperList(shipperList);
        mongoTemplate.insert(weighingSiteMetaDataDTO, "WeighingSiteMetaData");

        List<String> collect = weighingSiteRecordDTOS.stream().map(WeighingSiteRecordDTO::getWeighingRecordId).collect(Collectors.toList());
        Query query=new Query();
        query.addCriteria(Criteria.where("weighingRecordId").in(collect));
        List<WeighingSiteRecordDTO> repeated = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
        List<WeighingSiteRecordDTO> result=weighingSiteRecordDTOS;
        if (CollectionUtil.isNotEmpty(repeated)){
            List<String> repeatedIds = repeated.stream().map(WeighingSiteRecordDTO::getWeighingRecordId).collect(Collectors.toList());
            result = weighingSiteRecordDTOS.stream().filter(weighingSiteRecordDTO -> !repeatedIds.contains(weighingSiteRecordDTO.getWeighingRecordId())).collect(Collectors.toList());
        }

        Collection<WeighingSiteRecordDTO> insert = mongoTemplate.insert(result, WeighingSiteRecordDTO.class);

        return insert.size();
    }

}
