package com.ai.apac.smartenv.omnic.controller;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.facility.dto.BasicFacilityDTO;
import com.ai.apac.smartenv.omnic.service.IViewService;
import com.ai.apac.smartenv.omnic.vo.*;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.constant.RegionConstant;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.websocket.feign.IPolymerizationDataClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * @ClassName BigScreenView
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/13 14:46
 * @Version 1.0
 */
@RestController
@RequestMapping("/screenview")
@AllArgsConstructor
@Api(value = "大屏相关接口", tags = "大屏相关接口")
public class BigScreenViewController {
    private IViewService viewService;
    private ISysClient iSysClient;
    private IWorkareaNodeClient workareaNodeClient;
    private CoordsTypeConvertUtil coordsTypeConvertUtil;
    private IEntityCategoryClient categoryClient;
    private IDictClient dictClient;

    private IHomeDataClient homeDataClient;

    private IBigScreenDataClient bigScreenDataClient;

    private IPolymerizationDataClient polymerizationDataClient;


    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取大屏出勤统计数字")
    @GetMapping("/workingDataCount")
    @ApiLog(value = "获取大屏出勤统计数字")
    public R<WorkingDataCountVO> getMiniAppHomeDataCount() {
        return R.data(viewService.getWorkingDataCount());
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取工作区域内信息")
    @GetMapping("/workAreaDetails")
    @ApiLog(value = "获取工作区域内信息")
    public R<WorkAreaDetailVO> getWorkAreaDetails() {
        return R.data(viewService.getWorkAreaDetails());
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "获取整个租户信息")
    @GetMapping("/tenantDetails")
    @ApiLog(value = "获取整个租户信息")
    public R<TenantDetailsVO> getTenantDetails(@RequestParam("tenantId")String tenantId) {
        return R.data(viewService.getTenantDetails(tenantId));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取整个人员和车辆")
    @GetMapping("/getWorkareaInfoBigScreen")
    @ApiLog(value = "获取整个人员和车辆")
    public R<WorkareaInfoBigScreenVO> getAllInfo(@RequestParam("tenantId")String tenantId) {
        return R.data(viewService.getWorkareaInfoBigScreen(tenantId));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取大屏所有查询条件")
    @GetMapping("/getAllQuery4BS")
    @ApiLog(value = "获取大屏所有查询条件")
    public R<AllQuery4BSVO> getAllQuery4BS() {
        AllQuery4BSVO allQuery4BSVO = new AllQuery4BSVO();
        String tenantId = getUser().getTenantId();
        //获取区域信息
        List<Region> regions = iSysClient.getRegionForBS(String.valueOf(RegionConstant.REGION_TYPE.BUSI_REGION), tenantId).getData();
        List<BasicInfo4BSVO> regionList = regions.stream().map(region->{
            BasicInfo4BSVO regionVO = new BasicInfo4BSVO();
            regionVO.setId(region.getId().toString());
            regionVO.setName(region.getRegionName());
            return regionVO;
        }).collect(Collectors.toList());
        allQuery4BSVO.setRegionList(regionList);
        //获取车辆类型
        List<VehicleCategoryVO> vehicleCategoryVOList = VehicleCategoryCache.listCategoryByTenantId(AuthUtil.getTenantId());
        //List<EntityCategory> entityCategoryList = categoryClient.getCategoryByParentCategoryId(VehicleConstant.KindCode.MOTOR).getData();
        List<BasicInfo4BSVO> vehicleType = new ArrayList<BasicInfo4BSVO>();
        if(ObjectUtil.isNotEmpty(vehicleCategoryVOList) && vehicleCategoryVOList.size() > 0){
            for(VehicleCategoryVO vehicleCategoryVO:vehicleCategoryVOList){
                if(vehicleCategoryVO.getParentCategoryId() > 0) {
                    BasicInfo4BSVO vehicleTypeVO = new BasicInfo4BSVO();
                    vehicleTypeVO.setId(String.valueOf(vehicleCategoryVO.getCategoryCode()));
                    vehicleTypeVO.setName(vehicleCategoryVO.getCategoryName());
                    vehicleType.add(vehicleTypeVO);
                }
            }
        }
        allQuery4BSVO.setVehicleType(vehicleType);
        //车辆状态
        List<BasicInfo4BSVO> vehicleState = new ArrayList<>();
        for (int i = 1; i <8 ; i++) {
            BasicInfo4BSVO vehicleStateVO = new BasicInfo4BSVO();;
            vehicleStateVO.setId(String.valueOf(i));
            vehicleStateVO.setName(VehicleStatusEnum.getByValue(i).getDesc());
            vehicleState.add(vehicleStateVO);
        }
        allQuery4BSVO.setVehicleState(vehicleState);
        // 人员/车辆的在线离线状态
        List<BasicInfo4BSVO> deviceStatuses = dictTobasic("device_status_copy"); // 取在线/离线2个状态
        allQuery4BSVO.setVehicleAccStatus(deviceStatuses);
        allQuery4BSVO.setPersonWatchStatus(deviceStatuses);
        //人员岗位
        List<Station> stationList = iSysClient.getStationByTenant(tenantId).getData();
        List<BasicInfo4BSVO> personPositionList = stationList.stream().map(station->{
            BasicInfo4BSVO personPositionVO = new BasicInfo4BSVO();
            personPositionVO.setId(String.valueOf(station.getId()));
            personPositionVO.setName(station.getStationName());
            return personPositionVO;
        }).collect(Collectors.toList());
        allQuery4BSVO.setPersonPosition(personPositionList);
        //人员状态 暂定人员和车辆状态一致
        allQuery4BSVO.setPersonState(vehicleState);
        //中转站规模
        List<BasicInfo4BSVO> facilityType =dictTobasic(FacilityConstant.TranStationModel.CODE);
        allQuery4BSVO.setFacilityType(facilityType);
        //中转站状态
        List<BasicInfo4BSVO> facilityState = dictTobasic(FacilityConstant.TranStationStatus.CODE);
        allQuery4BSVO.setFacilityState(facilityState);
        //垃圾桶种类
        List<BasicInfo4BSVO> ashcanType = dictTobasic(FacilityConstant.DictCode.ASHCAN_TYPE);
        allQuery4BSVO.setAshcanType(ashcanType);
        //垃圾桶工作状态
        List<BasicInfo4BSVO> ashcanWorkState = dictTobasic(FacilityConstant.DictCode.ASHCAN_WORK_STATUS);
        allQuery4BSVO.setAshcanWorkState(ashcanWorkState);
        //垃圾桶状态
        List<BasicInfo4BSVO> ashcanState =dictTobasic(FacilityConstant.DictCode.ASHCAN_STATUS);
        allQuery4BSVO.setAshcanState(ashcanState);
        //事件状态
        List<BasicInfo4BSVO> envetState =dictTobasic("handle_status");
        allQuery4BSVO.setEventState(envetState);
        //事件等级
        List<BasicInfo4BSVO> envetLevel = dictTobasic("event_level");
        allQuery4BSVO.setEventLevel(envetLevel);
        //公厕等级
        List<BasicInfo4BSVO> wcLevel = dictTobasic("wc_level");
        allQuery4BSVO.setWcLevel(wcLevel);
        //公厕状态
        List<BasicInfo4BSVO> wcState = dictTobasic("wc_state");
        allQuery4BSVO.setWcState(wcState);

        return R.data(allQuery4BSVO);
    }


    private  List<BasicInfo4BSVO> dictTobasic (String dictCode) {
        List<Dict> dictList =  dictClient.getList(dictCode).getData();
        List<BasicInfo4BSVO> basicInfoList = dictList.stream().map(dict->{
            BasicInfo4BSVO envetLevelVO = new BasicInfo4BSVO();
            envetLevelVO.setId(dict.getDictKey());
            envetLevelVO.setName(dict.getDictValue());
            return envetLevelVO;
        }).collect(Collectors.toList());
        return basicInfoList;
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取大屏所选地区网格")
    @PostMapping("/getAllRegionAreaBS")
    @ApiLog(value = "获取大屏所选地区网格")
    public R<List<RegionInfo4BSVO>> getAllRegionAreaBS(@RequestBody List<String> regionList) {
        List<RegionInfo4BSVO> regionAreas = regionList.stream().map(region->{
            RegionInfo4BSVO regionVO = new RegionInfo4BSVO();
            Region regionDto = iSysClient.getRegion(Long.valueOf(region)).getData();
            regionVO.setId(region);
            regionVO.setName(regionDto.getRegionName());
            R<List<WorkareaNode>> listR = workareaNodeClient.queryRegionNodesList(Long.valueOf(region));
            if (listR.getData() != null && listR.getData().size() > 0) {
                coordsTypeConvertUtil.toWebConvert(listR.getData());
                regionVO.setWorkareaNodes(listR.getData().toArray(new WorkareaNode[listR.getData().size()]));
            }
            return regionVO;
        }).collect(Collectors.toList());
        return R.data(regionAreas);
    }


    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "刷新大屏和首页websocket缓存")
    @PostMapping("/reloadWebSocketCache")
    @ApiLog(value = "刷新大屏和首页websocket缓存")
    public R<Boolean> reloadWebSocketCache() {
        homeDataClient.updateHomeCountRedis(AuthUtil.getTenantId());
        homeDataClient.updateHomeAlarmListRedis(AuthUtil.getTenantId());
        homeDataClient.updateHomeGarbageAmountRedis(AuthUtil.getTenantId());
        homeDataClient.updateHomeEventListRedis(AuthUtil.getTenantId());
        homeDataClient.updateHomeOrderListRedis(AuthUtil.getTenantId(),AuthUtil.getUserId().toString());

        bigScreenDataClient.updateBigscreenCountRedis(AuthUtil.getTenantId());
        bigScreenDataClient.updateBigscreenAlarmAmountRedis(AuthUtil.getTenantId());
        bigScreenDataClient.updateBigscreenAlarmListRedis(AuthUtil.getTenantId());
        bigScreenDataClient.updateBigscreenEventCountByTypeRedis(AuthUtil.getTenantId());
        bigScreenDataClient.updateBigscreenGarbageAmountByRegionRedis(AuthUtil.getTenantId());
        bigScreenDataClient.updateBigscreenGarbageAmountDailyRedis(AuthUtil.getTenantId());

        polymerizationDataClient.updatePolymerizationCountRedis(AuthUtil.getTenantId(),"-1");

        return R.data(true);
    }

}
