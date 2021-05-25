package com.ai.apac.smartenv.vehicle.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.address.feign.IReverseAddressClient;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.dto.CommuterAndMileageDTO;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceExtClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.feign.ISimClient;
import com.ai.apac.smartenv.device.vo.DeviceViewVO;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.omnic.dto.BigDataRespDto;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleExt;
import com.ai.apac.smartenv.vehicle.service.IVehicleExtService;
import com.ai.apac.smartenv.vehicle.vo.*;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.ai.apac.smartenv.common.utils.CommonUtil.*;

/**
 * @ClassName VehicleViewController
 * @Desc 车辆360视图页面
 * @Author ZHANGLEI25
 * @Date 2020/2/6 15:21
 * @Version 1.0
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/vehicleView")
@Api(value = "车辆360视图接口", tags = "车辆360视图接口")
public class VehicleViewController {
    private IVehicleInfoService vehicleInfoService;

    private IVehicleExtService vehicleExtService;
    private IScheduleClient scheduleClient;


    private IPersonVehicleRelClient personVehicleRelClient;

    private IPersonClient personClient;

    private IDictClient dictClient;

    private IEntityCategoryClient entityCategoryClient;

    private ISysClient sysClient;

    private IOssClient ossClient;

    private IDeviceRelClient deviceRelClient;

    private IDeviceClient deviceClient;

    private IWorkareaClient workareaClient;

    private IWorkareaNodeClient workareaNodeClient;

    private IWorkareaRelClient workareaRelClient;

    private IAlarmInfoClient alarmInfoClient;

    private BaiduMapUtils baiduMapUtils;

    private IReverseAddressClient addressClient;

    private IDeviceExtClient deviceExtClient;

    private ISimClient simClient;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    private MongoTemplate mongoTemplate;


    @PostMapping("/getVehicleTrack/{vehicleId}/{beginTime}/{endTime}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询车辆历史轨迹", notes = "传入车辆id")
    @ApiLog(value = "查询车辆历史轨迹")
    public R<VehicleTrackVO> getVehicleTrack(@PathVariable String vehicleId, @PathVariable Long beginTime, @PathVariable Long endTime) throws IOException, ParseException {
        String pic = ossClient.getObjectLink(VehicleConstant.BUCKET, VehicleConstant.VehicleStatusPicPath.ONLINE).getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
        //获取设备信息
        List<DeviceInfo> deviceInfos = deviceClient.getForTrack(Long.parseLong(vehicleId), CommonConstant.ENTITY_TYPE.VEHICLE, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE, beginTime, endTime).getData();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            DeviceInfo deviceInfo = deviceInfos.get(deviceInfos.size() - 1);


            Map<String, Object> params = new HashMap<>();
            params.put("deviceId", deviceInfo.getDeviceCode());
            params.put("beginTime", dateFormat.format(new Date(beginTime)));
            params.put("endTime", dateFormat.format(new Date(endTime)));
            //从大数据获取轨迹数据
            BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
            List<TrackPositionDto> data = bigDataBody.getData();
            VehicleTrackVO trackVo = new VehicleTrackVO();

            //车辆基本信息获取
//                VehicleInfo vehicleInfo = vehicleInfoService.getById(vehicleId);
            VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, Long.valueOf(vehicleId));
            trackVo.setPlateNumber(vehicleInfo.getPlateNumber());
            //工作路线获取
            List<WorkareaRel> workareaRels = workareaRelClient.queryByCondition(vehicleInfo.getId(), VehicleConstant.WORKAREA_REL_VEHICLE,beginTime,endTime,AuthUtil.getTenantId()).getData();
            String scheduleName = "";
            if (CollectionUtil.isNotEmpty(workareaRels)) {
                List<List<WorkareaNode>> workareaNodeList = new ArrayList<>();
                List<List<WorkareaNode>> areaNodes = new ArrayList<>();
                List<List<WorkareaNode>> roadNodes = new ArrayList<>();
                for (WorkareaRel workareaRel : workareaRels) {

                    WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(workareaRel.getWorkareaId()).getData();
                    Long regionId = workareaInfo.getRegionId();
                    Long workAreaId = workareaInfo.getId();
                    // 查询规划路线（区域）坐标列表
                    List<WorkareaNode> workareaNodes =
                    coordsTypeConvertUtil.toWebConvert(workareaNodeClient.queryNodeByWorkareaId(workAreaId).getData());
                    //判断当前是路线还是区域
                    if(workareaInfo.getAreaType().equals(WorkAreaConstant.AreaType.ROAD)) {
                        roadNodes.add(workareaNodes);
                    }else if(workareaInfo.getAreaType().equals(WorkAreaConstant.AreaType.AREA)) {
                        areaNodes.add(workareaNodes);
                    }
                    // 查询规划路线或区域所属片区
                    List<WorkareaNode> workareaNodes1=
                    coordsTypeConvertUtil.toWebConvert(workareaNodeClient.queryRegionNodesList(regionId).getData());
                    workareaNodeList.add(workareaNodes1);
                    scheduleName = scheduleName.concat(workareaInfo.getAreaName()).concat(",");
                }
                trackVo.setRegionNodes(workareaNodeList);
                trackVo.setAreaNodes(areaNodes);
                trackVo.setRoadNodes(roadNodes);
                scheduleName.substring(scheduleName.length() - 1);
            } else {
                trackVo.setWorkDistance("0");
            }
            trackVo.setScheduleName(scheduleName);
            Integer alarmCount;
            AlarmInfoQueryDTO alarmInfoQuery = new AlarmInfoQueryDTO();
            alarmInfoQuery.setIsHandle(0);
            alarmInfoQuery.setVehicleId(Long.parseLong(vehicleId));
            alarmInfoQuery.setStartTime(beginTime);
            alarmInfoQuery.setEndTime(endTime);
            alarmInfoQuery.setTenantId(AuthUtil.getTenantId());
            alarmCount = alarmInfoClient.countAlarmInfoByCondition(alarmInfoQuery).getData().intValue();
            trackVo.setAlarmCount(alarmCount.longValue());



            if (CollectionUtil.isNotEmpty(data)) {

                TrackPositionDto trackPositionDto = data.get(0);

                //从大数据获取当天工作时长，工作时间等
                BeanUtil.copy(trackPositionDto, trackVo);
                Map<String, Object> synparams = new HashMap<>();
                String date = DateUtil.format(new Date(beginTime), "yyyyMMdd");
                synparams.put("date", date);
                synparams.put("deviceCode", deviceInfo.getDeviceCode());
                CommuterAndMileageDTO commuterAndMileage = BigDataHttpClient.getBigDataBodyToObjcet(
                        BigDataHttpClient.syncCommuterTimeAndMileage, synparams, CommuterAndMileageDTO.class);
                if (commuterAndMileage != null && CollectionUtil.isNotEmpty(commuterAndMileage.getData())) {
                    trackVo.setWorkBeginTime(commuterAndMileage.getData().get(0).getWorkBeginTime());
                    if (commuterAndMileage.getData().get(0).getWorkBeginTime() != null && commuterAndMileage.getData().get(0).getWorkOffTime() != null) {
//                        Date st = DateUtil.parse(commuterAndMileage.getData().get(0).getWorkBeginTime(), DatePattern.PURE_DATETIME_PATTERN);
//                        Date end = DateUtil.parse(commuterAndMileage.getData().get(0).getWorkOffTime(), DatePattern.PURE_DATETIME_PATTERN);
                        Date st = cn.hutool.core.date.DateUtil.parse(commuterAndMileage.getData().get(0).getWorkBeginTime());
                        Date end = cn.hutool.core.date.DateUtil.parse(commuterAndMileage.getData().get(0).getWorkOffTime());
                        if (st != null && end != null) {
                            long abs = Math.abs(end.getTime() - st.getTime());
                            abs /= 1000;
                            String time = (abs / 3600 == 0 ? "" : abs / 3600 + "小时") + "" + (abs % 3600 / 60 == 0 ? "" : abs % 3600 / 60 + "分钟");
                            if (StringUtils.isBlank(time)) {
                                time = abs + "秒";
                            }

                            trackVo.setTimeOfDuration(time);
                        }
                    }
                    BigDecimal bigDecimal = new BigDecimal(commuterAndMileage.getData().get(0).getMileage());

                    trackVo.setWorkDistance(bigDecimal.divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_CEILING).toString());

                }

                // 油耗统计
                String oilAvgStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.trackOilAvg, params);
                if (StringUtils.isNotBlank(oilAvgStr)) {
                    try {
                        JSONObject statistics = JSONUtil.parseObj(oilAvgStr).getJSONObject("data").getJSONObject("statistics");
                        String avgOil100km = statistics.getStr("avg_oil_100km"); // 百公里油耗
                        if (StringUtils.isNotBlank(avgOil100km)) {
                            avgOil100km = new BigDecimal(avgOil100km).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        String totalOil = statistics.getStr("total_oil"); // 总油耗
                        if (StringUtils.isNotBlank(totalOil)) {
                            totalOil = new BigDecimal(totalOil).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        String totalFillOil = statistics.getStr("total_fill_oil"); // 加油量
                        if (StringUtils.isNotBlank(totalFillOil)) {
                            totalFillOil = new BigDecimal(totalFillOil).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        trackVo.setAvgOil100km(avgOil100km);
                        trackVo.setTotalOil(totalOil);
                        trackVo.setTotalFillOil(totalFillOil);
                    } catch (Exception e) {
                        log.error("从大数据取油量数据报错：[{}], 返回报文:[{}]", e.getMessage(), oilAvgStr);
                    }
                }

                TrackPositionDto.Statistics statistics = trackPositionDto.getStatistics();

                if (statistics.getTotalDistance() != null) {
                    BigDecimal bigDecimal = new BigDecimal(statistics.getTotalDistance());
                    trackVo.setTotalDistance(bigDecimal.divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_CEILING).toString());
                } else {
                    trackVo.setTotalDistance("0");
                }

                if (statistics.getAvgSpeed() != null) {
                    BigDecimal bigDecimal = new BigDecimal(statistics.getAvgSpeed());
                    trackVo.setAvgSpeed(bigDecimal.setScale(2, BigDecimal.ROUND_CEILING).toString());
                } else {
                    trackVo.setAvgSpeed("0");
                }

                if (statistics.getMaxSpeed() != null) {
                    BigDecimal bigDecimal = new BigDecimal(statistics.getMaxSpeed());
                    trackVo.setMaxSpeed(bigDecimal.setScale(2, BigDecimal.ROUND_CEILING).toString());
                } else {
                    trackVo.setMaxSpeed("0");
                }
                trackVo.setWorkCount(trackPositionDto.getRounds().toString());
                trackVo.setCarIconPicture(pic);
                if (CollectionUtil.isNotEmpty(trackPositionDto.getTracks())) {
                    List<Coords> coords = new ArrayList<>();
                    trackPositionDto.getTracks().forEach(position -> {
                        Coords coord = new Coords();
                        coord.setLatitude(position.getLat());
                        coord.setLongitude(position.getLng());
                        coords.add(coord);
                    });

                    DeviceExt deviceExt = deviceExtClient.getByAttrId(deviceInfo.getId(), CommonConstant.VEHICLE_WARCH_COORDS_CATEGORY_ID).getData();

                    //获取坐标系。默认为国测局坐标系
                    BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.GC02;
                    if (deviceExt == null) {
                        String attrValue = deviceExt.getAttrValue();
                        coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(Integer.parseInt(attrValue));
                    }

                    List<Coords> result = coords;

                    String coordsTypeStr = WebUtil.getHeader("coordsType");
                    if (StringUtil.isNotBlank(coordsTypeStr)) {
                        int coordsType = Integer.parseInt(coordsTypeStr);
                        BaiduMapUtils.CoordsSystem dest = BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType);
                        if (!coordsSystem.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value) && dest.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)) {

//                        coordsTypeConvertUtil.toWebConvert(coords);

                            result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);
                        }
                    }

                    for (int i = 0; i < trackPositionDto.getTracks().size(); i++) {
                        TrackPositionDto.Position position = trackPositionDto.getTracks().get(i);
                        Coords coords1 = result.get(i);
                        position.setLat(coords1.getLatitude());
                        position.setLng(coords1.getLongitude());
                        Date parse = dateFormat.parse(position.getEventTime());
                        position.setEventTime(String.valueOf(parse.getTime()));
                    }

                } else {
                    trackVo.setTotalDistance("0");
                    trackVo.setAvgSpeed("0");
                    trackVo.setMaxSpeed("0");
                    trackVo.setTimeOfDuration("0");
                }
                // 数据做格式化处理
                List<TrackPositionDto.Position> tracks = trackVo.getTracks();
                if (CollectionUtil.isNotEmpty(tracks)) {
                    tracks.forEach(position -> {
                        String speed = position.getSpeed();
                        if (StringUtils.isNotBlank(speed)) {
                            speed = new BigDecimal(speed).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                            position.setSpeed(speed);
                        }
                    });
                }
            }
            return R.data(trackVo);
        }
//        return R.fail("未绑定设备");
        return R.data(null);
    }


    @PostMapping("/getVehicleTrackList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询车辆历史轨迹列表", notes = "传入车辆id")
    @ApiLog(value = "查询车辆历史轨迹列表")
    public R<VehicleTrackVO> getVehicleTrackList(@RequestParam String vehicleId, @RequestParam Long beginTime, @RequestParam Long endTime, @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam(required = false) Integer accstatus) throws IOException, ParseException {
        String pic = ossClient.getObjectLink(VehicleConstant.BUCKET, VehicleConstant.VehicleStatusPicPath.ONLINE).getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
        List<DeviceInfo> deviceInfos = deviceClient.getForTrack(Long.parseLong(vehicleId), CommonConstant.ENTITY_TYPE.VEHICLE, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE, beginTime, endTime).getData();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            DeviceInfo deviceInfo = deviceInfos.get(deviceInfos.size() - 1);
            Map<String, Object> params = new HashMap<>();
            params.put("pageNO", pageNo);
            params.put("pageSize", pageSize);
            params.put("deviceId", deviceInfo.getDeviceCode());
            params.put("beginTime", dateFormat.format(new Date(beginTime)));
            params.put("endTime", dateFormat.format(new Date(endTime)));
            if (accstatus != null) {
                params.put("accStatus", accstatus);

            }
            BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
            List<TrackPositionDto> data = bigDataBody.getData();
            VehicleTrackVO trackVo = null;
            if (CollectionUtil.isNotEmpty(data)) {
                TrackPositionDto trackPositionDto = data.get(0);
                for (TrackPositionDto.Position position : trackPositionDto.getTracks()) {
                    Date parse = dateFormat.parse(position.getEventTime());
                    position.setEventTime(String.valueOf(parse.getTime()));
                }

                params.remove("pageNO");
                params.remove("pageSize");
                BigDataRespDto body1 = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
                List<TrackPositionDto.Position> lastTracks = body1.getData().get(0).getTracks();
                trackVo = BeanUtil.copy(trackPositionDto, VehicleTrackVO.class);

                Date lastDate = null;
                if (CollectionUtil.isNotEmpty(lastTracks)) {
                    lastDate = dateFormat.parse(lastTracks.get(lastTracks.size() - 1).getEventTime());
                }

//                VehicleInfo vehicleInfo = vehicleInfoService.getById(vehicleId);
                VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, Long.valueOf(vehicleId));
                trackVo.setPlateNumber(vehicleInfo.getPlateNumber());
                List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(vehicleInfo.getId(), VehicleConstant.WORKAREA_REL_VEHICLE).getData();
                WorkareaInfo workareaInfo = null;
                if (CollectionUtil.isNotEmpty(workareaRels)) {
                    Long workareaId = workareaRels.get(0).getWorkareaId();
                    workareaInfo = workareaClient.getWorkInfoById(workareaId).getData();
                }
                if (workareaInfo != null) {
                    trackVo.setScheduleName(workareaInfo.getAreaName());
                }

                Integer alarmCount = alarmInfoClient.countNoHandleAlarmInfoByEntity(Long.parseLong(vehicleId), CommonConstant.ENTITY_TYPE.VEHICLE).getData();
                trackVo.setAlarmCount(alarmCount.longValue());
                trackVo.setTotal(trackPositionDto.getStatistics().getTotalCount() == null ? 0 : Integer.parseInt(trackPositionDto.getStatistics().getTotalCount()));
                trackVo.setCarIconPicture(pic);
                List<Coords> coords = new ArrayList<>();
                trackPositionDto.getTracks().forEach(position -> {
                    Coords coord = new Coords();
                    coord.setLatitude(position.getLat());
                    coord.setLongitude(position.getLng());
                    coords.add(coord);
                });
                //获取坐标系。默认为国测局坐标系
                BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.GC02;

                List<Coords> result = coords;
                if (!coordsSystem.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)) {
                    result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);
                }
                List<Dict> device_status = dictClient.getList("BIGDATA_DEVICE_STATUS").getData();
                Map<String, Dict> dictMap = new HashMap<>();
                device_status.forEach(dict -> dictMap.put(dict.getDictKey(), dict));

                Coords lastCoords = null;
                TrackPositionDto.Position lastPosition = null;
                for (int i = 0; i < trackPositionDto.getTracks().size(); i++) {
                    TrackPositionDto.Position position = trackPositionDto.getTracks().get(i);
                    Coords coords1 = result.get(i);
                    position.setLat(coords1.getLatitude());
                    position.setLng(coords1.getLongitude());


                    if (StringUtils.isNotBlank(position.getEventTime())) {

                        String eventTime = position.getEventTime();
                        long time = Long.parseLong(eventTime);
                        Date date = new Date(time);

                        position.setLastOnlineTime(DateUtil.formatDateTime(date));
                    }

                    Boolean isNeedWork = scheduleClient.checkNeedWork(Long.parseLong(vehicleId), "1", new Date(Long.parseLong(position.getEventTime()))).getData();
                    String status = null;
                    if (!isNeedWork) {
                        status = VehicleStatusEnum.OFF_LINE.getDesc();
                    } else if (isNeedWork && position.getAccStatus().equals("1")) {
                        status = VehicleStatusEnum.ON_LINE.getDesc();
                    } else {
                        status = VehicleStatusEnum.OFFLINE_ALARM.getDesc();
                    }
                    position.setWorkStatus(status);

                    String accStatus = position.getAccStatus();
                    position.setAccStatus(dictMap.get(accStatus).getDictValue());


//
//                    if(DeviceConstant.BigDataDeviceStatus.ON.toString().equals(accStatus)){
//
//                        position.setAccStatus("开启");
//                    }else {
//                        position.setAccStatus("关闭");
//
//                    }


                    if (position.getDistance() != null) {
                        BigDecimal bigDecimal = new BigDecimal(position.getDistance());
                        position.setDistance(bigDecimal.divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_CEILING).toString());
                    } else {
                        position.setDistance("0");
                    }

                    if (lastCoords == null || getDistance(Double.parseDouble(coords1.getLongitude()), Double.parseDouble(coords1.getLatitude()), Double.parseDouble(lastCoords.getLongitude()), Double.parseDouble(lastCoords.getLatitude())) >= 5) {

                        BaiduMapReverseGeoCodingResult reverseGeoCoding = addressClient.getAddress(coords1).getData();
                        if (reverseGeoCoding == null || reverseGeoCoding.getResult() == null) {
                            reverseGeoCoding = baiduMapUtils.getReverseGeoCoding(coords1, BaiduMapUtils.CoordsSystem.BD09LL);
                            reverseGeoCoding.setBaiduCoords(coords1);
                            addressClient.saveAddress(reverseGeoCoding);
                        }
                        lastCoords = coords1;
                        lastPosition = position;
                        position.setAddress(reverseGeoCoding.getResult().getFormatted_address());
                    } else {
                        position.setAddress(lastPosition.getAddress());
                    }
                }

            }

            return R.data(trackVo);
        }
//        return R.fail("未绑定设备");
        return R.data(null);
    }


    /**
     * 车辆360视图车辆信息-查绑定的驾驶员
     */
    @GetMapping("/details/drivers")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据ID查询车辆360视图的车辆驾驶员", notes = "传入id")
    @ApiLog(value = "根据ID查询车辆360视图的车辆驾驶员")
    public R<List<VehicleDriverVO>> vehicleDrivers(@RequestParam Long vehicleId, BladeUser user) {
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(vehicleId).getData();
        List<VehicleDriverVO> vehicleDriverVOList = new ArrayList<VehicleDriverVO>();
        if (null != personVehicleRelList && personVehicleRelList.size() > 0) {
            for (PersonVehicleRel personVehicleRel :
                    personVehicleRelList) {
                long id = personVehicleRel.getPersonId();
//                Person person = personClient.getPerson(id).getData();
                Person person = PersonCache.getPersonById(null, id);
                VehicleDriverVO vehicleDriverVO = Objects.requireNonNull(BeanUtil.copy(person, VehicleDriverVO.class));
                vehicleDriverVO.setPersonDeptName(sysClient.getDeptName(person.getPersonDeptId()).getData());
                vehicleDriverVO.setDriverNumber(4567812345679L);
                vehicleDriverVO.setRelId(personVehicleRel.getId());
                vehicleDriverVO.setPersonPositionName(StationCache.getStationName(person.getPersonPositionId()));
                vehicleDriverVOList.add(vehicleDriverVO);
            }
        }
        return R.data(vehicleDriverVOList);
    }

    /**
     * 车辆360视图车辆信息-查绑定的车辆传感器
     */
    @GetMapping("/details/sensors")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "根据ID查询车辆360视图的车辆传感器设备", notes = "传入id")
    @ApiLog(value = "根据ID查询车辆360视图的车辆传感器设备")
    public R<List<DeviceViewVO>> vehicleSensors(@RequestParam Long vehicleId, BladeUser user) {
        List<Long> ids = new ArrayList<Long>();
        ids.add(DeviceConstant.DeviceCategory.VEHICLE_SENSOR_DEVICE);

        List<Long> entityCategoryIdList = entityCategoryClient.getSubCategoryIdByParentCategoryId(DeviceConstant.DeviceCategory.VEHICLE_SENSOR_DEVICE).getData();

        if (entityCategoryIdList.size() > 0) {
            ids.addAll(entityCategoryIdList);
        }

        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        List<DeviceViewVO> deviceViewVOList = new ArrayList<DeviceViewVO>();
        if (null != deviceRelList && deviceRelList.size() > 0) {
            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                Long id = deviceInfo.getEntityCategoryId();
                if (ids.contains(id)) {
                    //车辆传感器
                    DeviceViewVO deviceViewVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, DeviceViewVO.class));
                    deviceViewVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo.getEntityCategoryId()).getData());
                    List<DeviceExt> deviceExtList = deviceClient.getExtInfoByDeviceId(deviceId).getData();
                    deviceViewVO.setDeviceExtList(deviceExtList);
                    deviceViewVOList.add(deviceViewVO);
                }
            }
        }
        return R.data(deviceViewVOList);
    }

    /**
     * 车辆360视图车辆信息-查绑定的车辆监控终端
     */
    @GetMapping("/details/monitors")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID查询车辆360视图的车辆监控设备", notes = "传入id")
    @ApiLog(value = "根据ID查询车辆360视图的车辆监控设备")
    public R<List<DeviceViewVO>> vehicleMonitors(@RequestParam Long vehicleId, BladeUser user) {
        List<Long> ids = new ArrayList<Long>();
        ids.add(DeviceConstant.DeviceCategory.VEHICLE_MONITOR_DEVICE);

        List<Long> entityCategoryIdList = entityCategoryClient.getSubCategoryIdByParentCategoryId(DeviceConstant.DeviceCategory.VEHICLE_MONITOR_DEVICE).getData();

        if (entityCategoryIdList.size() > 0) {
            ids.addAll(entityCategoryIdList);
        }

        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        List<DeviceViewVO> monotorViewVOList = new ArrayList<DeviceViewVO>();

        if (null != deviceRelList && deviceRelList.size() > 0) {

            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                Long id = deviceInfo.getEntityCategoryId();
                if (ids.contains(id)) {
                    DeviceViewVO deviceViewVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, DeviceViewVO.class));
                    deviceViewVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo.getEntityCategoryId()).getData());
                    List<DeviceExt> deviceExtList = deviceClient.getExtInfoByDeviceId(deviceId).getData();
                    List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                    deviceViewVO.setDeviceChannelList(deviceChannelList);
                    deviceViewVO.setDeviceExtList(deviceExtList);
                    deviceViewVO.setDeviceFactoryName(DictCache.getValue("device_manufacturer", deviceInfo.getDeviceFactory()));
                    monotorViewVOList.add(deviceViewVO);
                }
            }
        }
        return R.data(monotorViewVOList);
    }

    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/info")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据ID查询车辆360视图的基本信息", notes = "传入id")
    @ApiLog(value = "根据ID查询车辆360视图的基本信息")
    public R<VehicleViewInfoVO> info(@RequestParam Long vehicleId, BladeUser user) throws IOException {

//        VehicleInfo detail = vehicleInfoService.getById(vehicleId);
        VehicleInfo detail = VehicleCache.getVehicleById(null, vehicleId);
        if (null == detail) {
            throw new ServiceException("无此辆车的信息");
        }
        VehicleViewInfoVO vehicleViewInfoVO = Objects.requireNonNull(BeanUtil.copy(detail, VehicleViewInfoVO.class));
        vehicleViewInfoVO.setEntryTime(DateUtil.format(detail.getDeptAddTime(), "YYYY-MM-dd"));

        if(ObjectUtil.isNotEmpty(detail.getDeptRemoveTime())){
            vehicleViewInfoVO.setQuitTime(DateUtil.format(detail.getDeptRemoveTime(), "YYYY-MM-dd"));

        }

        VehicleExt vehicleExt = vehicleExtService.getVehicleAttr(vehicleId, com.ai.apac.smartenv.common.constant.VehicleConstant.VehicleExtAttr.PIC_ATTR_ID);
        if (null != vehicleExt && StringUtil.isNotBlank(vehicleExt.getAttrValue())) {
            String uri = ossClient.getObjectLink("smartenv", vehicleExt.getAttrValue()).getData();
            //车头照
            vehicleViewInfoVO.setMediaURI(uri);
        }
        vehicleViewInfoVO.setVehicleKindCode(detail.getKindCode());
        vehicleViewInfoVO.setVehicleKindCodeName(VehicleCategoryCache.getCategoryNameByCode(detail.getKindCode().toString(),AuthUtil.getTenantId()));

        vehicleViewInfoVO.setVehicleCategoryId(detail.getEntityCategoryId());
        vehicleViewInfoVO.setVehicleCategoryName(VehicleCategoryCache.getCategoryNameByCode(detail.getEntityCategoryId().toString(),AuthUtil.getTenantId()));

        List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(detail.getId(), CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(workareaRels) && workareaRels.size() > 0) {
            WorkareaRel workareaRel = workareaRels.get(0);
            WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(workareaRel.getWorkareaId()).getData();
            if (ObjectUtil.isNotEmpty(workareaInfo)) {
                vehicleViewInfoVO.setWorkArea(workareaInfo.getAreaName());
            }
        }

        List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(detail.getId(),
				ArrangeConstant.ScheduleObjectEntityType.VEHICLE, LocalDate.now());
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			Schedule schedule = ScheduleCache.getScheduleById(scheduleObjectList.get(0).getScheduleId());
		    if (ObjectUtil.isNotEmpty(schedule) && ObjectUtil.isNotEmpty(schedule.getId())) {
		        vehicleViewInfoVO.setScheduleBeginTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getScheduleBeginTime()));
		        vehicleViewInfoVO.setScheduleEndTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getScheduleEndTime()));
		        vehicleViewInfoVO.setBreaksBeginTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getBreaksBeginTime()));
		        vehicleViewInfoVO.setBreaksEndTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getBreaksEndTime()));
		    }
		}

        //1
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(vehicleId).getData();

        Long dirverId = 0l;//驾驶员取第一个
        if (personVehicleRelList.size() > 0) {
            List<VehicleDriverVO> vehicleDriverVOList = new ArrayList<VehicleDriverVO>();
            for (PersonVehicleRel personVehicleRel : personVehicleRelList) {
                long id = personVehicleRel.getPersonId();
//                Person person = personClient.getPerson(id).getData();
                Person person = PersonCache.getPersonById(AuthUtil.getTenantId(), id);
                VehicleDriverVO vehicleDriverVO = Objects.requireNonNull(BeanUtil.copy(person, VehicleDriverVO.class));
                vehicleDriverVOList.add(vehicleDriverVO);
            }
            if (vehicleDriverVOList.size() > 0) {
                dirverId = vehicleDriverVOList.get(0).getId();
            }
            vehicleViewInfoVO.setVehicleDriverVOList(vehicleDriverVOList);
        }

        //取车的ACC状态
        String accStatus = DeviceConstant.DeviceStatus.NO_DEV;
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        vehicleViewInfoVO.setAccStatusId(Long.parseLong(accStatus));
        vehicleViewInfoVO.setAccStatus("未关联设备");
        if (deviceRelList.size() > 0) {
            String deviceIds = "";
            for (DeviceRel deviceRel : deviceRelList) {
                deviceIds = deviceIds + deviceRel.getDeviceId().toString() + ",";
            }
            List<DeviceInfo> deviceInfoList = deviceClient.getDevicesByParam(deviceIds, DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();
            if (deviceInfoList.size() > 0) {
                accStatus = deviceInfoList.get(0).getDeviceStatus().toString();
                vehicleViewInfoVO.setAccStatusId(Long.parseLong(accStatus));
                vehicleViewInfoVO.setAccStatus(DictCache.getValue("device_status", accStatus));
            } else {
                vehicleViewInfoVO.setAccStatusId(Long.parseLong(accStatus));
                vehicleViewInfoVO.setAccStatus("未关联设备");
            }


            // 查询当前油量
            Long beginTime = TimeUtil.getStartTime(new Date()).getTime();
            Long endTime = TimeUtil.getSysDate().getTime();

            List<DeviceInfo> deviceInfos = deviceClient.getForTrack(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE, beginTime, endTime).getData();

					if (deviceInfos != null && deviceInfos.size() > 0) {
						try {
							JSONObject param = new JSONObject();
							param.put("deviceId", deviceInfos.get(0).getDeviceCode());
                            param.put("beginTime", TimeUtil.getNoLineYYYYMMDDHHMISS(new Timestamp(beginTime)));
							param.put("endTime", TimeUtil.getNoLineYYYYMMDDHHMISS(new Timestamp(endTime)));
							String oilAvgStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.trackOilAvg, param);
                            if (StringUtils.isNotBlank(oilAvgStr)) {
                                try {
                                    JSONObject statistics = JSONUtil.parseObj(oilAvgStr).getJSONObject("data").getJSONObject("statistics");
                                    String avgOil100km = statistics.getStr("avg_oil_100km"); // 百公里油耗
                                    if (StringUtils.isNotBlank(avgOil100km)) {
                                        avgOil100km = new BigDecimal(avgOil100km).divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                                        vehicleViewInfoVO.setAvgOil(avgOil100km);
                                    }
                                } catch (Exception e) {
                                    log.error("从大数据取油量数据报错：[{}], 返回报文:[{}]", e.getMessage(), oilAvgStr);
                                }
                            }

						} catch (Exception e) {
							log.error("调用大数据查询百公里油耗失败：" + e.getMessage());
						}
					}


        }


        //调用大数据
//        JSONObject param = new JSONObject();
//        param.put("deviceId","867959034040070");
//        String reStr = BigDataHttpClient.getBigDataBody("/smartenv-api/device/status/search",param);
//        if(StringUtil.isNotBlank(reStr) && JSONUtil.parseObj(reStr).get("code").equals(0)){
//            JSONObject data = (JSONObject)JSONUtil.parseObj(reStr).get("data");
//            String accStatusId = data.getStr("status");
//            String accStatus = dictClient.getValue("device_status", accStatusId).getData();
//            vehicleViewInfoVO.setAccStatusId(Long.parseLong(accStatusId));
//            vehicleViewInfoVO.setAccStatus(accStatus);
//        }else{
//            throw new ServiceException("调用大数据获取设备数据错误，请稍后再试");
//        }
        //取人的手表状态

        if (dirverId > 0) {
            List<DeviceRel> deviceRelList_ = deviceRelClient.getEntityRels(dirverId, CommonConstant.ENTITY_TYPE.PERSON).getData();
            vehicleViewInfoVO.setWatchStatusId(Long.parseLong(DeviceConstant.DeviceStatus.NO_DEV));
            vehicleViewInfoVO.setWatchStatus("未关联设备");
            if (deviceRelList_.size() > 0) {
                String deviceIds = "";
                for (DeviceRel deviceRel : deviceRelList_) {
                    deviceIds = deviceIds + deviceRel.getDeviceId().toString() + ",";
                }
                List<DeviceInfo> deviceInfoList = deviceClient.getDevicesByParam(deviceIds, DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();
                if (deviceInfoList.size() > 0) {
                    vehicleViewInfoVO.setWatchStatusId(deviceInfoList.get(0).getDeviceStatus());
                    vehicleViewInfoVO.setWatchStatus(DictCache.getValue("device_status", deviceInfoList.get(0).getDeviceStatus().toString()));
                } else {
                    vehicleViewInfoVO.setWatchStatusId(Long.parseLong(DeviceConstant.DeviceStatus.NO_DEV));
                    vehicleViewInfoVO.setWatchStatus("未关联设备");
                }
            }
        } else {
            vehicleViewInfoVO.setWatchStatusId(Long.parseLong(DeviceConstant.DeviceStatus.NO_DEV));
            vehicleViewInfoVO.setWatchStatus("未关联设备");
        }


        //在岗（工作：ACC开，当前时间为工作时间），休息（当天不上班，当前时间为休息时间），脱岗（未工作：ACC关，当前时间为工作时间）
//        Boolean isNeedWork = scheduleClient.checkNowNeedWork(vehicleId, "1").getData();
//        if (isNeedWork) {
//            //ACC开
//            if (accStatus.equals(DeviceConstant.DeviceStatus.ON)) {
//                vehicleViewInfoVO.setWorkStatusId(CommonConstant.WORK_STATUS.ON);//在岗
//                String workStatus = DictCache.getValue("work_status", CommonConstant.WORK_STATUS.ON.toString());
//                vehicleViewInfoVO.setWorkStatus(workStatus);
//            } else {
//                vehicleViewInfoVO.setWorkStatusId(CommonConstant.WORK_STATUS.OFF);//脱岗
//                String workStatus = DictCache.getValue("work_status", CommonConstant.WORK_STATUS.OFF.toString());
//                vehicleViewInfoVO.setWorkStatus(workStatus);
//            }
//        } else {
//            vehicleViewInfoVO.setWorkStatusId(CommonConstant.WORK_STATUS.REST);//休息
//            String workStatus = DictCache.getValue("work_status", CommonConstant.WORK_STATUS.REST.toString());
//            vehicleViewInfoVO.setWorkStatus(workStatus);
//        }

        BasicVehicleInfoDTO basicVehicleInfoDTO = mongoTemplate.findById(vehicleId,BasicVehicleInfoDTO.class);
        if(ObjectUtil.isNotEmpty(basicVehicleInfoDTO)){
            vehicleViewInfoVO.setWorkStatusId(Long.parseLong(basicVehicleInfoDTO.getWorkStatus().toString()));
            vehicleViewInfoVO.setWorkStatus(basicVehicleInfoDTO.getWorkStatusName());
        }else{
            vehicleViewInfoVO.setWorkStatusId(Long.parseLong(PersonConstant.PersonStatus.ONLINE.toString()));
            vehicleViewInfoVO.setWorkStatus("正常");
        }

        vehicleViewInfoVO.setDeptName(DeptCache.getDeptName(String.valueOf(detail.getDeptId())));

        return R.data(vehicleViewInfoVO);
    }


    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/videos/live")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "获取车辆全部的实时视频地址", notes = "传入车辆id")
    @ApiLog(value = "获取车辆全部的实时视频地址")
    public R<VehicleVideoVO> getVehicleVideosLive(@RequestParam Long vehicleId,
                                                  @RequestParam(name = "device", required = false) String device,
                                                  @RequestParam(name = "channel", required = false) String channel, BladeUser user) {
        return R.data(vehicleInfoService.getVehicleVideosLive(vehicleId, device, channel));
    }


    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/videos/history")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "获取车辆某段时间内的历史视频地址", notes = "传入车辆id")
    @ApiLog(value = "获取车辆某段时间内的历史视频地址")
    public R<VehicleVideoVO> getVehicleVideosHistory(@RequestParam Long vehicleId,
                                                     @RequestParam(name = "device", required = false) String device,
                                                     @RequestParam(name = "channel", required = false) String channel,
                                                     @RequestParam(name = "startTime", required = false) String startTime,
                                                     @RequestParam(name = "endTime", required = false) String endTime,
                                                     @RequestParam(name = "isTransfer", required = false) Boolean isTransfer, BladeUser user) {
        return R.data(vehicleInfoService.getVehicleVideosHistory(vehicleId, device, channel, startTime, endTime, isTransfer));
    }


    @GetMapping("/videos/his")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "获取车辆某段时间内的历史视频地址(新)", notes = "传入车辆id")
    @ApiLog(value = "获取车辆某段时间内的历史视频地址(新)")
    public R<VehicleVideoVO> getVehicleVideosHis(@RequestParam Long vehicleId,
                                                 @RequestParam(name = "channel", required = false) String channelNo,
                                                 @RequestParam(name = "startTime", required = false) String startTime,
                                                 @RequestParam(name = "endTime", required = false) String endTime,
                                                 BladeUser user) {
        return R.data(vehicleInfoService.getHistoryVideoUrl(vehicleId, channelNo, startTime, endTime));
    }

    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/videos/live/stop")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "关闭车辆实时视频地址", notes = "传入车辆id和channel")
    @ApiLog(value = "关闭车辆实时视频地址")
    public R<Boolean> stopVehicleVideosLive(@RequestParam Long vehicleId,
                                            @RequestParam(name = "channel", required = false) Long channel,
                                            BladeUser user) throws IOException {
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                    List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                    if (ObjectUtil.isNotEmpty(deviceChannelList) && deviceChannelList.size() > 0) {
                        List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(deviceId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                        String guid = "";
                        if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                            guid = deviceExtList.get(0).getAttrValue();
                        }
                        SimInfo simInfo = simClient.getSimByDeviceId(deviceId).getData();
                        if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_LIVE_VIDEO_STOP_KEY);
                            for (DeviceChannel deviceChannel : deviceChannelList) {
                                String channelSeq = deviceChannel.getChannelSeq();
                                if (!ObjectUtil.isNotEmpty(channel) || channelSeq.equals(channel.toString())) {
                                    VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                                    vehicleVideoUrlVO.setDeviceId(deviceId.toString());
                                    vehicleVideoUrlVO.setChannelSeq(channelSeq);
                                    String uri = value.split(" ")[1];
                                    String simCode2 = simInfo.getSimCode2();
                                    uri = StrUtil.format(uri, guid, simCode2, channelSeq, 0, 0, 0, token);
                                    OkhttpUtil.getSync(uri);
                                }
                            }
                        }

                    }
                }
            }
        } else {
            throw new ServiceException("该车辆尚未绑定监控设备");
        }
        return R.data(true);
    }

    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/videos/history/stop")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "关闭车辆历史视频地址", notes = "传入车辆id和channel")
    @ApiLog(value = "关闭车辆历史视频地址")
    public R<Boolean> stopVehicleVideosHistory(@RequestParam Long vehicleId,
                                               @RequestParam(name = "channel", required = false) Long channel,
                                               BladeUser user) throws IOException {
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                    List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                    if (ObjectUtil.isNotEmpty(deviceChannelList) && deviceChannelList.size() > 0) {
                        List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(deviceId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                        String guid = "";
                        if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                            guid = deviceExtList.get(0).getAttrValue();
                        }
                        SimInfo simInfo = simClient.getSimByDeviceId(deviceId).getData();
                        if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_HISTORY_VIDEO_STOP_KEY);
                            for (DeviceChannel deviceChannel : deviceChannelList) {
                                String channelSeq = deviceChannel.getChannelSeq();
                                if (!ObjectUtil.isNotEmpty(channel) || channelSeq.equals(channel.toString())) {
                                    VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                                    vehicleVideoUrlVO.setDeviceId(deviceId.toString());
                                    vehicleVideoUrlVO.setChannelSeq(channelSeq);
                                    String uri = value.split(" ")[1];
                                    String simCode2 = simInfo.getSimCode2();
                                    uri = StrUtil.format(uri, guid, simCode2, channelSeq, 2, 0, token);
                                    OkhttpUtil.getSync(uri);
                                }
                            }
                        }

                    }
                }
            }
        } else {
            throw new ServiceException("该车辆尚未绑定监控设备");
        }
        return R.data(true);
    }

    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/video/Channels")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "获取车辆全部的视频频道", notes = "传入车辆id")
    @ApiLog(value = "获取车辆全部的视频频道")
    public R<VehicleVideoVO> getVehicleVideoChannels(@RequestParam String vehicleId, BladeUser user) throws IOException {
        VehicleVideoVO vehicleVideoVO = new VehicleVideoVO();
        vehicleVideoVO.setVerhicleId(vehicleId);
        List<VehicleVideoUrlVO> vehicleVideoUrlVOList = new ArrayList<VehicleVideoUrlVO>();
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(Long.parseLong(vehicleId), CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                    List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                    if (ObjectUtil.isNotEmpty(deviceChannelList) && deviceChannelList.size() > 0) {
                        for (DeviceChannel deviceChannel : deviceChannelList) {
                            VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                            vehicleVideoUrlVO.setDeviceId(deviceId.toString());
                            vehicleVideoUrlVO.setChannelSeq(deviceChannel.getChannelSeq());
                            vehicleVideoUrlVO.setChannelName(deviceChannel.getChannelName());
                            vehicleVideoUrlVOList.add(vehicleVideoUrlVO);
                        }
                    }
                }
            }
            vehicleVideoVO.setVehicleVideoUrlVOList(vehicleVideoUrlVOList);
        } else {
            throw new ServiceException("该车辆尚未绑定监控设备");
        }
        return R.data(vehicleVideoVO);
    }


    @GetMapping("/realWorkingArea")
    @ApiOperationSupport(order = 13)
    @ApiOperation(value = "获取车辆当前的实际工作面积", notes = "传入车辆id")
    @ApiLog(value = "获取车辆当前的实际工作面积")
    public R<Long> getVehicleRealWorkingArea(@RequestParam Long vehicleId, BladeUser user) throws IOException {
        return R.data(vehicleInfoService.getVehicleRealWorkingArea(vehicleId));

    }


}
