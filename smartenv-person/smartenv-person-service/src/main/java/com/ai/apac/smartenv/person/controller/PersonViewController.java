package com.ai.apac.smartenv.person.controller;

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
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceExtClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.feign.ISimClient;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.omnic.dto.BigDataRespDto;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.PersonTrackVo;
import com.ai.apac.smartenv.person.vo.PersonViewInfoVO;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.ai.apac.smartenv.workarea.vo.VehicleVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import static com.ai.apac.smartenv.common.utils.CommonUtil.getDistance;

/**
 * @ClassName VehicleViewController
 * @Desc 车辆360视图页面
 * @Author ZHANGLEI25
 * @Date 2020/2/6 15:21
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personView")
@Api(value = "人员360视图接口", tags = "人员360视图接口")
public class PersonViewController {

    private IPersonService personService;

    private IScheduleClient scheduleClient;

    private IPersonClient personClient;

    private IDictClient dictClient;

    private IEntityCategoryClient entityCategoryClient;

    private ISysClient sysClient;

    private IOssClient ossClient;

    private ISimClient simClient;

    private IDeviceClient deviceClient;

    private IPersonVehicleRelClient personVehicleRelClient;

    private IVehicleClient vehicleClient;

    private IDeviceRelClient deviceRelClient;

    private IWorkareaNodeClient workareaNodeClient;

    private IWorkareaClient workareaClient;

    private IWorkareaRelClient workareaRelClient;

    private BaiduMapUtils baiduMapUtils;

    private IAlarmInfoClient alarmInfoClient;

    private IReverseAddressClient addressClient;

    private IDeviceExtClient deviceExtClient;

    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    private MongoTemplate mongoTemplate;

    @PostMapping("/getPersonTrack/{personId}/{beginTime}/{endTime}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询人员历史轨迹", notes = "传入人员id")
    @ApiLog(value = "查询人员历史轨迹")
    public R<PersonTrackVo> getPersonTrack(@PathVariable String personId, @PathVariable Long beginTime, @PathVariable Long endTime) throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
        List<DeviceInfo> deviceInfos = deviceClient.getForTrack(Long.parseLong(personId), CommonConstant.ENTITY_TYPE.PERSON, VehicleConstant.PERSON_POSITION_DEVICE_TYPE, beginTime, endTime).getData();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            DeviceInfo deviceInfo = deviceInfos.get(deviceInfos.size() - 1);
            Map<String, Object> params = new HashMap<>();
            params.put("deviceId", deviceInfo.getDeviceCode());
            params.put("beginTime", dateFormat.format(new Date(beginTime)));
            params.put("endTime", dateFormat.format(new Date(endTime)));

            BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
            List<TrackPositionDto> data = bigDataBody.getData();


            PersonTrackVo trackVo = new PersonTrackVo();
            Person person = PersonCache.getPersonById(null, Long.parseLong(personId));
            //工作路线获取
            List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(person.getId(), PersonConstant.WORKAREA_REL_PERSON).getData();
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
                    List<WorkareaNode> workareaNodes1=  coordsTypeConvertUtil.toWebConvert(workareaNodeClient.queryRegionNodesList(regionId).getData());
                    workareaNodeList.add(workareaNodes1);
                    scheduleName = scheduleName.concat(workareaInfo.getAreaName()).concat(",");
                }
                trackVo.setRegionNodes(workareaNodeList);
                trackVo.setAreaNodes(areaNodes);
                trackVo.setRoadNodes(roadNodes);

                scheduleName.substring(0, scheduleName.length() - 1);
            }else {
                trackVo.setWorkDistance("0");
            }
            trackVo.setScheduleName(scheduleName);

            Integer alarmCount;
            AlarmInfoQueryDTO alarmInfoQuery = new AlarmInfoQueryDTO();
            alarmInfoQuery.setIsHandle(0);
            alarmInfoQuery.setPersonId(Long.parseLong(personId));
            alarmInfoQuery.setStartTime(beginTime);
            alarmInfoQuery.setEndTime(endTime);
            alarmInfoQuery.setTenantId(AuthUtil.getTenantId());
            alarmCount = alarmInfoClient.countAlarmInfoByCondition(alarmInfoQuery).getData().intValue();
            trackVo.setAlarmCount(alarmCount.longValue());
            trackVo.setAlarmCount(alarmCount.longValue());

            trackVo.setPersonName(person.getPersonName());


            if (CollectionUtil.isNotEmpty(data)) {
                TrackPositionDto trackPositionDto = data.get(0);
                BeanUtil.copy(trackPositionDto,trackVo);

                Map<String, Object> synparams = new HashMap<>();
                String date = DateUtil.format(new Date(beginTime), "yyyyMMdd");
                synparams.put("date", date);
                synparams.put("deviceCode", deviceInfo.getDeviceCode());
                CommuterAndMileageDTO commuterAndMileage = BigDataHttpClient.getBigDataBodyToObjcet(
                        BigDataHttpClient.syncCommuterTimeAndMileage, synparams, CommuterAndMileageDTO.class);
                if (commuterAndMileage != null && CollectionUtil.isNotEmpty(commuterAndMileage.getData())) {
                    trackVo.setWorkBeginTime(commuterAndMileage.getData().get(0).getWorkBeginTime());

                    if (commuterAndMileage.getData().get(0).getWorkBeginTime()!=null&&commuterAndMileage.getData().get(0).getWorkOffTime()!=null){
//                        Date st = DateUtil.parse(commuterAndMileage.getData().get(0).getWorkBeginTime(),BigDataHttpClient.bigDataTimeFormat);
//                        Date end = DateUtil.parse(commuterAndMileage.getData().get(0).getWorkOffTime(),BigDataHttpClient.bigDataTimeFormat);
                        Date st = cn.hutool.core.date.DateUtil.parse(commuterAndMileage.getData().get(0).getWorkBeginTime());
                        Date end = cn.hutool.core.date.DateUtil.parse(commuterAndMileage.getData().get(0).getWorkOffTime());
                        if (st!=null&&end!=null){
                            long abs = Math.abs(end.getTime() - st.getTime());
                            abs/=1000;
//                        BigDecimal absDecimal=new BigDecimal(abs);
//                        BigDecimal timeOfHour=new BigDecimal(3600);
//                        BigDecimal divide = absDecimal.divide(timeOfHour, 2);
                            String time=(abs/3600==0?"":abs/3600+"小时")+""+(abs%3600/60==0?"":abs%3600/60+"分钟");
                            if (StringUtils.isBlank(time)){
                                time=abs+"秒";
                            }

                            trackVo.setTimeOfDuration(time);
                        }
                    }
                    trackVo.setWorkDistance(new BigDecimal(commuterAndMileage.getData().get(0).getMileage()).setScale(2,BigDecimal.ROUND_CEILING).toString());

                }


                trackVo.setWorkCount(trackPositionDto.getRounds().toString());
                TrackPositionDto.Statistics statistics = trackPositionDto.getStatistics();
                if (statistics.getTotalDistance() != null) {
                    BigDecimal bigDecimal = new BigDecimal(statistics.getTotalDistance());
//                    trackVo.setTotalDistance(bigDecimal.divide(new BigDecimal(1000)).setScale(0, BigDecimal.ROUND_CEILING).toString());
                    trackVo.setTotalDistance(bigDecimal.toString());
                } else {
                    trackVo.setTotalDistance("0");
                }


                if (CollectionUtil.isNotEmpty(trackPositionDto.getTracks())) {
                    TrackPositionDto.Position lastPosition = trackPositionDto.getTracks().get(trackPositionDto.getTracks().size() - 1);

                    List<Coords> coords = new ArrayList<>();
                    trackPositionDto.getTracks().forEach(position -> {
                        Coords coord = new Coords();
                        coord.setLatitude(position.getLat());
                        coord.setLongitude(position.getLng());
                        coords.add(coord);
                    });
                    List<Coords> result = coords;
//
//                    result = coordsTypeConvertUtil.deviceToWebConvert(coords);


                    String coordsTypeStr = WebUtil.getHeader("coordsType");
                    if (StringUtil.isNotBlank(coordsTypeStr)) {
                        int coordsType = Integer.parseInt(coordsTypeStr);
                        BaiduMapUtils.CoordsSystem dest = BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType);
                        if (dest.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)) {

//                        coordsTypeConvertUtil.toWebConvert(coords);

                            result = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.GC02, coords);
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
//                    trackVo.setTimeOfDuration("0");
                }

            }
            return R.data(trackVo);
        }
        return R.data(null);
    }

    @PostMapping("/getPersonTrackList")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询人员历史轨迹列表", notes = "传入人员id")
    @ApiLog(value = "查询人员历史轨迹")
    public R<PersonTrackVo> getVehicleTrackList(@RequestParam String personId, @RequestParam Long beginTime, @RequestParam Long endTime, @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam(required = false) Integer deviceStatus) throws IOException, ParseException {
        String pic = ossClient.getObjectLink(VehicleConstant.BUCKET, VehicleConstant.VehicleStatusPicPath.ONLINE).getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
        List<DeviceInfo> deviceInfos = deviceClient.getForTrack(Long.parseLong(personId), CommonConstant.ENTITY_TYPE.PERSON, VehicleConstant.PERSON_POSITION_DEVICE_TYPE, beginTime, endTime).getData();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            DeviceInfo deviceInfo = deviceInfos.get(deviceInfos.size() - 1);
            Map<String, Object> params = new HashMap<>();
//            params.put("deviceId", "867959034040020");
//            params.put("beginTime", "2020021012130101");
//            params.put("endTime", "2021021012131401");
            params.put("pageNO", pageNo);
            params.put("pageSize", pageSize);
            params.put("deviceId", deviceInfo.getDeviceCode());
            params.put("beginTime", dateFormat.format(new Date(beginTime)));
            params.put("endTime", dateFormat.format(new Date(endTime)));
            if (deviceStatus != null) {
                params.put("accStatus", deviceStatus);
            }
            BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
            List<TrackPositionDto> data = bigDataBody.getData();
            PersonTrackVo trackVo = null;
            if (CollectionUtil.isNotEmpty(data)) {
                TrackPositionDto trackPositionDto = data.get(0);
                for (TrackPositionDto.Position position : trackPositionDto.getTracks()) {
                    Date parse = dateFormat.parse(position.getEventTime());
                    position.setEventTime(String.valueOf(parse.getTime()));
                }
                trackVo = BeanUtil.copy(trackPositionDto, PersonTrackVo.class);
//                params.put("pageNO",trackPositionDto.getTracks().size());
//                params.put("pageSize", 1);
                params.remove("pageNO");
                params.remove("pageSize");

                BigDataRespDto body1 = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
                List<TrackPositionDto.Position> lastTracks = body1.getData().get(0).getTracks();

                Date lastDate = null;
                if (CollectionUtil.isNotEmpty(lastTracks)) {
                    lastDate = dateFormat.parse(lastTracks.get(lastTracks.size() - 1).getEventTime());
                }

                Person personById = PersonCache.getPersonById(null, Long.valueOf(personId));
                trackVo.setPersonName(personById.getPersonName());
                List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(personById.getId(), CommonConstant.ENTITY_TYPE.PERSON).getData();
                WorkareaInfo workareaInfo = null;
                if (CollectionUtil.isNotEmpty(workareaRels)) {
                    Long workareaId = workareaRels.get(0).getWorkareaId();
                    workareaInfo = workareaClient.getWorkInfoById(workareaId).getData();
                }
                if (workareaInfo != null) {
                    trackVo.setScheduleName(workareaInfo.getAreaName());
                }

                Integer alarmCount = alarmInfoClient.countNoHandleAlarmInfoByEntity(Long.parseLong(personId), CommonConstant.ENTITY_TYPE.PERSON).getData();
                trackVo.setAlarmCount(alarmCount.longValue());
                trackVo.setTotal(trackPositionDto.getStatistics().getTotalCount() == null ? 0 : Integer.parseInt(trackPositionDto.getStatistics().getTotalCount()));
                List<Coords> coords = new ArrayList<>();
                trackPositionDto.getTracks().forEach(position -> {
                    Coords coord = new Coords();
                    coord.setLatitude(position.getLat());
                    coord.setLongitude(position.getLng());
                    coords.add(coord);
                });

                DeviceExt deviceExt = deviceExtClient.getByAttrId(deviceInfo.getId(), CommonConstant.PERSON_WARCH_COORDS_CATEGORY_ID).getData();

                //获取坐标系。默认为国测局坐标系
                BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.GC02;
                List<Coords> result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);

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

                    Boolean isNeedWork = scheduleClient.checkNeedWork(Long.parseLong(personId), "2", new Date(Long.parseLong(position.getEventTime()))).getData();
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

    /**
     * 车辆360视图基本信息
     */
    @GetMapping("/info")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据ID查询人员360视图的基本信息", notes = "传入id")
    @ApiLog(value = "根据ID查询人员360视图的基本信息")
    public R<PersonViewInfoVO> info(@RequestParam Long personId, BladeUser user) throws IOException {
        Person detail = PersonCache.getPersonById(null, personId);
        Set<ConstraintViolation<@Valid Person>> validateSet = Validation.buildDefaultValidatorFactory().getValidator()
                .validate(detail, new Class[0]);
        if ((validateSet != null && !validateSet.isEmpty()) || detail.getPersonPositionId() == null
                || detail.getPersonPositionId() <= 0) {
            throw new ServiceException("人员信息不完整，请将信息补充完整");
        }
        PersonViewInfoVO personViewInfoVO = Objects.requireNonNull(BeanUtil.copy(detail, PersonViewInfoVO.class));
        if (detail.getEntryTime() != null) {
            personViewInfoVO.setEntryTime(DateUtil.format(detail.getEntryTime(), "YYYY-MM-dd"));
        }
        String imgaeName = detail.getImage();
        if (StringUtil.isNotBlank(imgaeName)) {
            String uri = ossClient.getObjectLink(PersonConstant.BUCKET, imgaeName).getData();
            personViewInfoVO.setMediaURI(uri);
        }

        if (null != personViewInfoVO.getPersonDeptId()) {
            personViewInfoVO.setDeptName(sysClient.getDeptName(personViewInfoVO.getPersonDeptId()).getData());
        }

        if (null != personViewInfoVO.getPersonPositionId()) {
            personViewInfoVO.setPositionName(StationCache.getStationName(personViewInfoVO.getPersonPositionId()));
        }

        List<DeviceRel> deviceRelList_ = deviceRelClient.getEntityRels(personId, CommonConstant.ENTITY_TYPE.PERSON).getData();
        String watchStatus = DeviceConstant.DeviceStatus.NO_DEV;

        personViewInfoVO.setWatchStatusId(Long.parseLong(watchStatus));
        personViewInfoVO.setWatchStatus("未关联指定设备");
        if (deviceRelList_.size() > 0) {
            String deviceIds = "";
            for (DeviceRel deviceRel : deviceRelList_) {
                deviceIds = deviceIds + deviceRel.getDeviceId().toString() + ",";
            }
            List<DeviceInfo> deviceInfoList = deviceClient.getDevicesByParam(deviceIds, DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();
            if (deviceInfoList.size() > 0) {
                watchStatus = deviceInfoList.get(0).getDeviceStatus().toString();
                personViewInfoVO.setWatchStatusId(deviceInfoList.get(0).getDeviceStatus());
                personViewInfoVO.setWatchStatus(dictClient.getValue("device_status", watchStatus).getData());
            } else {
                personViewInfoVO.setWatchStatusId(Long.parseLong(watchStatus));
                personViewInfoVO.setWatchStatus("未关联指定设备");
            }
        }
        //在岗（工作：ACC开，当前时间为工作时间），休息（当天不上班，当前时间为休息时间），脱岗（未工作：ACC关，当前时间为工作时间）
//        Boolean isNeedWork = scheduleClient.checkNowNeedWork(personId, ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
//        if (isNeedWork) {
//            if (watchStatus.equals(DeviceConstant.DeviceStatus.ON)) {
//                personViewInfoVO.setWorkStatusId(1L);//在岗
//                String workStatus = dictClient.getValue("work_status", "1").getData();
//                personViewInfoVO.setWorkStatus(workStatus);
//            } else {
//                personViewInfoVO.setWorkStatusId(2L);//脱岗
//                String workStatus = dictClient.getValue("work_status", "2").getData();
//                personViewInfoVO.setWorkStatus(workStatus);
//            }
//        } else {
//            personViewInfoVO.setWorkStatusId(3L);//休息
//            String workStatus = dictClient.getValue("work_status", "3").getData();
//            personViewInfoVO.setWorkStatus(workStatus);
//        }

        BasicPersonDTO basicPersonDTO = mongoTemplate.findById(personId,BasicPersonDTO.class);
        if(ObjectUtil.isNotEmpty(basicPersonDTO)){
            personViewInfoVO.setWorkStatusId(Long.parseLong(basicPersonDTO.getWorkStatus().toString()));
            personViewInfoVO.setWorkStatus(basicPersonDTO.getWorkStatusName());
        }else{
            personViewInfoVO.setWorkStatusId(Long.parseLong(PersonConstant.PersonStatus.ONLINE.toString()));
            personViewInfoVO.setWorkStatus("正常");
        }

        // 路线
		List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(detail.getId(), CommonConstant.ENTITY_TYPE.PERSON).getData();
		if (ObjectUtil.isNotEmpty(workareaRels) && workareaRels.size() > 0) {
			WorkareaRel workareaRel = workareaRels.get(0);
			WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(workareaRel.getWorkareaId()).getData();
			if (ObjectUtil.isNotEmpty(workareaInfo)) {
				personViewInfoVO.setWorkArea(workareaInfo.getAreaName());
			}
		}
		// 排班时间
		List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(detail.getId(),
				ArrangeConstant.ScheduleObjectEntityType.PERSON, LocalDate.now());
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			Schedule schedule = scheduleClient.getScheduleById(scheduleObjectList.get(0).getScheduleId()).getData();
			if (ObjectUtil.isNotEmpty(schedule) && ObjectUtil.isNotEmpty(schedule.getId())) {
				personViewInfoVO.setScheduleBeginTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getScheduleBeginTime()));
				personViewInfoVO.setScheduleEndTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getScheduleEndTime()));
				personViewInfoVO.setBreaksBeginTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getBreaksBeginTime()));
				personViewInfoVO.setBreaksEndTime(TimeUtil.getYYYYMMDDHHMMSS(schedule.getBreaksEndTime()));
			}
		}

        return R.data(personViewInfoVO);
    }

    /**
     * 车辆360视图基本信息
     * 根据ID查询人员360视图的人员绑定的设备信息
     */
    @GetMapping("/personDevice")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据ID查询人员360视图的人员绑定的设备信息", notes = "传入id")
    @ApiLog(value = "根据ID查询人员360视图的人员绑定的设备信息")
    public R<List<PersonDeviceVO>> getPersonDeviceInfo(@RequestParam Long personId, BladeUser user) throws IOException {
        List<PersonDeviceVO> personDeviceVOList = new ArrayList<>();
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(personId, CommonConstant.ENTITY_TYPE.PERSON).getData();
        if (deviceRelList.size() > 0) {
            deviceRelList.forEach(deviceRel -> {
                DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceRel.getDeviceId().toString()).getData();
                if (!ObjectUtils.isEmpty(deviceInfo) && !ObjectUtils.isEmpty(deviceInfo.getId())) {
                    PersonDeviceVO personDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo, PersonDeviceVO.class));
                    String categoryName = entityCategoryClient.getCategoryName(deviceInfo.getEntityCategoryId()).getData();
                    personDeviceVO.setEntityCategoryName(categoryName);
                    List<DeviceExt> deviceExtList = deviceClient.getExtInfoByDeviceId(deviceInfo.getId()).getData();
                    if (deviceExtList.size() > 0) {
                        deviceExtList.forEach(deviceExt -> {
                            if (deviceExt.getAttrId().equals(DeviceConstant.DeviceCharSpec.PERSON_DEVICE_ICCID)) {
                                personDeviceVO.setAuthCode(deviceExt.getAttrDisplayValue());
                            }
                        });
                    }
                    SimInfo simInfo = simClient.getSimByDeviceId(deviceInfo.getId()).getData();
                    if (!ObjectUtils.isEmpty(simInfo) && !ObjectUtils.isEmpty(simInfo.getId())) {
                        personDeviceVO.setSim(simInfo.getSimCode());
                        personDeviceVO.setSimId(simInfo.getId().toString());
                    }
                    personDeviceVOList.add(personDeviceVO);
                }
            });
        }
        return R.data(personDeviceVOList);
    }

    /**
     * 车辆360视图基本信息
     * 根据ID查询人员360视图的人员绑定的车
     */
    @GetMapping("/vehicles")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据ID查询人员360视图的人员绑定的车", notes = "传入id")
    @ApiLog(value = "根据ID查询人员360视图的人员绑定的车")
    public R<List<VehicleVO>> getRelVehiclesInfo(@RequestParam Long personId, BladeUser user) throws IOException {
        List<VehicleVO> vehicleVOList = new ArrayList<>();

        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getVehicleByPersonId(personId).getData();
        if (personVehicleRelList.size() > 0) {
            personVehicleRelList.forEach(personVehicleRel -> {
                VehicleVO vehicleVO = new VehicleVO();
//                VehicleInfo vehicleInfo = vehicleClient.vehicleInfoById(personVehicleRel.getVehicleId()).getData();
                VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, personVehicleRel.getVehicleId());
                if (ObjectUtil.isNotEmpty(vehicleInfo) && ObjectUtil.isNotEmpty(vehicleInfo.getId())) {
                    vehicleVO.setId(vehicleInfo.getId());
                    vehicleVO.setPlateNumber(vehicleInfo.getPlateNumber());
                    vehicleVO.setDeptId(vehicleInfo.getDeptId());
                    vehicleVO.setDeptName(sysClient.getDeptName(vehicleInfo.getDeptId()).getData());
                    vehicleVO.setDeptAddTime(DateUtil.format(vehicleInfo.getDeptAddTime(), "YYYY-MM-dd"));
                    vehicleVO.setCategoryName(VehicleCategoryCache.getCategoryNameByCode(vehicleInfo.getEntityCategoryId().toString(),AuthUtil.getTenantId()));
                    vehicleVO.setRelId(personVehicleRel.getId());
                    vehicleVOList.add(vehicleVO);
                }
            });
        }
        return R.data(vehicleVOList);
    }
}
