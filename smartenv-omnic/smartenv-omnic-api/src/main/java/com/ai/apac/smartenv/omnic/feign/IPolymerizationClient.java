package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IPolymerizationClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  10:46    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_OMNIC_NAME,
        fallback = IPolymerizationClientFallback.class
)
public interface IPolymerizationClient {
    String FEIGN ="/client/polymerization";

    String INIT_TREE_DATA= FEIGN +"/initTree";
    String ADD_OR_UPDATE_VEHICLE_LIST= FEIGN +"/add-or-update-vehicle-list";
    String ADD_OR_UPDATE_PERSON_LIST= FEIGN +"/add-or-update-person-list";
    String REMOVE_PERSON_LIST= FEIGN +"/remove-person-list";
    String REMOVE_VEHICLE_LIST= FEIGN +"/remove-vehicle-list";
    String REMOVE_FACILITY_LIST= FEIGN +"/remove-facility-by-id";
    String RELOAD_VEHICLE_INFO= FEIGN +"/reload-vehicle-info";
    String RELOAD_PERSON_INFO= FEIGN +"/reload-person-info";
    String RELOAD_TREE_DATA_TO_MONGO_DB= FEIGN +"/reload-tree-data-to-mongo-db";
    String ADD_OR_UPDATE_FACILITY= FEIGN +"/add-or-update-facility";


    /**
     * 初始化MongoDB中的所有聚合数据
     * @return
     */
    @GetMapping(INIT_TREE_DATA)
    R initTreeDataToMongoDB();

    /**
     * 重新加载 MongoDB中的数据，保留原有的坐标数据
     * @return
     */
    @GetMapping(RELOAD_TREE_DATA_TO_MONGO_DB)
    R reloadTreeDataToMongoDb();


    /**
     * 添加或修改中转站，用于中转站新增或修改的时候重新加载到聚合数据
     * @param facilityListId
     * @param facilityMainType
     * @return
     */
    @GetMapping(ADD_OR_UPDATE_FACILITY)
    R addOrUpdateFacility(@RequestParam("facilityListId") String facilityListId, @RequestParam("facilityMainType") Integer facilityMainType);

    /**
     * 从聚合数据中删除指定的人员
     * @param personList
     * @return
     */
    @PostMapping(REMOVE_PERSON_LIST)
    R removePersonList(List<Long> personList);

    /**
     * 从聚合数据中删除中转站
     * @param facilityIds
     * @return
     */
    @DeleteMapping(REMOVE_FACILITY_LIST)
    R removeFacilityList(@RequestParam("facilityIds") String facilityIds);

    /**
     * 从聚合数据中删除车辆
     * @param personList
     * @return
     */
    @PostMapping(REMOVE_VEHICLE_LIST)
    R removeVehicleList(List<Long> personList);

    /**
     * 重新加载聚合中指定车辆的信息
     * @param vehicleIds
     * @return
     */
    @PostMapping(RELOAD_VEHICLE_INFO)
    R reloadVehicleInfo(@RequestBody List<Long> vehicleIds);

    /**
     * 重新加载聚合中的人员信息
     * @param personIds
     * @return
     */
    @PostMapping(RELOAD_PERSON_INFO)
    R reloadPersonInfo(@RequestBody List<Long> personIds);
}
