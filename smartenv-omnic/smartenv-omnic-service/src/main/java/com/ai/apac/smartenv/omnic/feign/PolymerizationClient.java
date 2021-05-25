package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PolymerizationClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  10:48    panfeng          v1.0.0             修改原因
 */
//@ApiIgnore
@RestController
@RequiredArgsConstructor
public class PolymerizationClient implements IPolymerizationClient {


    @Autowired
    private PolymerizationService polymerizationService;


    /**
     * 初始化MongoDB中的所有聚合数据
     * @return
     */
    @Override
    @GetMapping(INIT_TREE_DATA)
    public R initTreeDataToMongoDB() {
        polymerizationService.initAllPersonDataToMongoDb(false);
        polymerizationService.initAllVehicleDataToMongoDb(false);
        polymerizationService.initAllFacilityDataToMongoDb();
        return R.data("Success");
    }


    /**
     * 重新加载 MongoDB中的数据，保留原有的坐标数据
     * @return
     */
    @Override
    @GetMapping(RELOAD_TREE_DATA_TO_MONGO_DB)
    public R reloadTreeDataToMongoDb() {
        polymerizationService.initAllPersonDataToMongoDb(true);
        polymerizationService.initAllVehicleDataToMongoDb(true);
        polymerizationService.initAllFacilityDataToMongoDb();
        return R.data("Success");
    }
//    以下接口请使用reload方法
//
//    /**
//     * 添加或者更新车辆列表，用于车辆新增或修改后重新加载到聚合数据
//     * @param vehicleInfoList
//     * @return
//     */
//    @Override
//    @Deprecated
//    @PostMapping(ADD_OR_UPDATE_VEHICLE_LIST)
//    public R addOrUpdateVehicleList(@RequestBody List<OmnicVehicleInfo> vehicleInfoList){
//        List<VehicleInfo> collect = vehicleInfoList.stream().map(omnicVehicleInfo -> BeanUtil.copy(omnicVehicleInfo, VehicleInfo.class)).collect(Collectors.toList());
//        return R.data(polymerizationService.addOrUpdateVehicleList(collect));
//    }
//
//
//    /**
//     * 添加或修改人员列表，用于人员新增或修改后重新加载到聚合数据
//     *
//     * 此接口废弃，请使用reloadPersonInfo
//     * @param personList
//     * @return
//     */
//    @Override
//    @Deprecated
//    @PostMapping(ADD_OR_UPDATE_PERSON_LIST)
//    public R addOrUpdatePersonList(@RequestBody List<OmnicPersonInfo> personList){
//        List<Person> collect = personList.stream().map(omnicPersonInfo -> BeanUtil.copy(omnicPersonInfo, Person.class)).collect(Collectors.toList());
//        return R.data(polymerizationService.addOrUpdatePersonList(collect));
//    }

    /**
     * 添加或修改设施，用于设施新增或修改的时候重新加载到聚合数据
     * @param facilityId
     * @param facilityMainType
     * @return
     */
    @Override
    @GetMapping(ADD_OR_UPDATE_FACILITY)
    public R addOrUpdateFacility(String facilityId, Integer facilityMainType){
        return R.data(polymerizationService.addOrUpdateFacility(facilityId,facilityMainType));
    }


    /**
     * 从聚合数据中删除指定的人员
     * @param personList
     * @return
     */
    @Override
    @PostMapping(REMOVE_PERSON_LIST)
    public R removePersonList(@RequestBody List<Long> personList){

        return R.data(polymerizationService.removePersonList(personList));
    }

    /**
     * 从聚合数据中删除设施
     * @param facilityIds
     * @return
     */
    @Override
    public R removeFacilityList(String facilityIds){
        return R.data(polymerizationService.removeFacilityList(Func.toLongList(facilityIds)));
    }

    /**
     * 从聚合数据中删除车辆
     * @param personList
     * @return
     */
    @Override
    @PostMapping(REMOVE_VEHICLE_LIST)
    public R removeVehicleList(@RequestBody List<Long> personList){

        return R.data(polymerizationService.removeVehicleList(personList));
    }


    /**
     * 重新加载聚合中指定车辆的信息
     * @param vehicleIds
     * @return
     */
    @Override
    @PostMapping(RELOAD_VEHICLE_INFO)
    public R reloadVehicleInfo(@RequestBody List<Long> vehicleIds){
        return R.data(polymerizationService.reloadVehicleInfo(vehicleIds));
    }

    /**
     * 重新加载聚合中的人员信息
     * @param personIds
     * @return
     */
    @Override
    @PostMapping(RELOAD_PERSON_INFO)
    public R reloadPersonInfo(@RequestBody List<Long> personIds){
        return R.data(polymerizationService.reloadPersonInfo(personIds));
    }



}
