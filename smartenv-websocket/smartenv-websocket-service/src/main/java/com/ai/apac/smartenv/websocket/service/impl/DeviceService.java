package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceExtClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.BigDataRespDto;
import com.ai.apac.smartenv.omnic.dto.RealTimePositionDTO;
import com.ai.apac.smartenv.omnic.dto.RealTimePositionResp;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.service.IDeviceService;
import com.alibaba.fastjson.JSON;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

import static com.ai.apac.smartenv.common.cache.CacheNames.DEVICE_LAST_INFO;
import static com.ai.apac.smartenv.common.cache.CacheNames.ExpirationTime.EXPIRATION_TIME_24HOURS;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/19 9:26 上午
 **/
@Service
@Slf4j
public class DeviceService implements IDeviceService {

    @Autowired
    private IDeviceRelClient deviceRelClient;

    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @Autowired
    private IDeviceExtClient deviceExtClient;


    @Autowired
    private BaiduMapUtils baiduMapUtils;


    @Autowired
    private CoordsTypeConvertUtil coordsTypeConvertUtil;

    /**
     * 根据车辆ID获取车辆绑定的位置设备信息
     *
     * @param vehicleId
     * @return
     */
    @Async
    @Override
    public Future<DeviceInfo> getPositionDeviceByVehicle(String vehicleId) {
        DeviceInfo deviceInfo = this.getDeviceInfo(vehicleId, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE);

        if (deviceInfo != null && deviceInfo.getId() != null && !DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
            return new AsyncResult<DeviceInfo>(deviceInfo);
        }

        DeviceInfo cvr = this.getDeviceInfo(vehicleId, DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE);
        if (cvr != null && cvr.getId() != null && DeviceConstant.DeviceFactory.MINICREATE.equals(cvr.getDeviceFactory())) {
            return new AsyncResult<DeviceInfo>(cvr);
        }
        DeviceInfo nvr = this.getDeviceInfo(vehicleId, DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE);
        if (nvr != null && nvr.getId() != null && DeviceConstant.DeviceFactory.MINICREATE.equals(nvr.getDeviceFactory())) {
            return new AsyncResult<DeviceInfo>(nvr);
        }
        return null;
    }


    /**
     * 根据车辆ID获取车辆绑定的ACC设备信息
     *
     * @param vehicleId
     * @return
     */
    @Async
    @Override
    public Future<DeviceInfo> getAccDeviceByVehicle(String vehicleId) {
        DeviceInfo deviceInfo = this.getDeviceInfo(vehicleId, VehicleConstant.VEHICLE_ACC_DEVICE_TYPE);
//        DeviceInfo deviceInfo = this.getDeviceInfo(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE);
        return new AsyncResult<DeviceInfo>(deviceInfo);
    }


    /**
     * 根据人员ID获取人员绑定的手表设备信息
     *
     * @param personId
     * @return
     */
    @Override
    public Future<DeviceInfo> getDeviceByPerson(String personId) {
        DeviceInfo deviceInfo = this.getDeviceInfo(personId, VehicleConstant.PERSON_POSITION_DEVICE_TYPE);
//        DeviceInfo deviceInfo = this.getDeviceInfo(personId, CommonConstant.ENTITY_TYPE.PERSON);
        return new AsyncResult<DeviceInfo>(deviceInfo);
    }

    private DeviceInfo getDeviceInfo(String entityId, Long entityType) {
//        R<List<DeviceRel>> deviceRelData = deviceRelClient.getEntityRels(Long.valueOf(entityId), entityType);
////        R<List<DeviceRel>> deviceRelData = deviceRelClient.getByEntityAndCategory(Long.valueOf(entityId), entityType);
//        if (deviceRelData.isSuccess() && CollectionUtil.isNotEmpty(deviceRelData.getData())) {
//            DeviceRel deviceRel = deviceRelData.getData().get(0);
//            Long deviceId = deviceRel.getDeviceId();
//            DeviceInfo deviceInfo = DeviceCache.getDeviceById(deviceRel.getTenantId(), Long.valueOf(deviceId));
//            return deviceInfo;
//        }
//        return null;

        DeviceInfo deviceInfo = deviceClient.getByEntityAndCategory(Long.parseLong(entityId), entityType).getData();
        return deviceInfo;
    }

    /**
     * 根据设备编号查询设备当前位置
     *
     * @param deviceCode
     * @param coordsSystem
     * @return
     */
    @Async
    @Override
    public Future<PositionDTO> getDevicePosition(String deviceCode, BaiduMapUtils.CoordsSystem coordsSystem) {
//        if (StringUtils.isBlank(deviceCode)) {
//            return null;
//        }
//        try {
//            Date endTime = DateTime.now();
//            Date startTime = DateUtil.offsetMinute(endTime, -10).toJdkDate();
//            Future<List<PositionDTO>> positionListResult = getDeviceTrackPosition(deviceCode, startTime, endTime);
//            if (positionListResult != null || positionListResult.get() != null) {
//                List<PositionDTO> positionList = positionListResult.get();
//                return new AsyncResult<PositionDTO>(positionList.get(positionList.size() - 1));
//            }
//            return null;
//        } catch (Exception ex) {
//            throw new ServiceException(ResultCode.FAILURE, ex);
//        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("deviceIds", deviceCode);
        try {
            Future<HashMap<String, PositionDTO>> positionMapResult = batchGetDevicePosition(deviceCode,coordsSystem);
            if (positionMapResult == null || positionMapResult.get() == null
                    || positionMapResult.get().size() == 0) {
                return null;
            }
            return new AsyncResult<PositionDTO>(positionMapResult.get().get(deviceCode));
        } catch (Exception ex) {
            log.error("从大数据侧获取实时位置异常", ex);
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }

    /**
     * 根据设备号列表批量获取位置信息,返回一个Map，key是deviceCode
     *
     * @param deviceCodes
     * @return
     */
    @Override
    public Future<HashMap<String, PositionDTO>> batchGetDevicePosition(String deviceCodes, BaiduMapUtils.CoordsSystem coordsSystem) {
        if (StringUtils.isBlank(deviceCodes)) {
            throw new ServiceException("设备号不能为空");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("deviceIds", deviceCodes);
        try {
            log.info("batchGetDevicePosition From BigData:{}", JSON.toJSONString(params));
            RealTimePositionResp resp = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.getPersonCarRealTime,
                    params, RealTimePositionResp.class);
            if (resp != null && resp.getCode() == 0) {
                HashMap<String, PositionDTO> positionMap = new HashMap<String, PositionDTO>();
                List<RealTimePositionDTO.Position> positionList = resp.getData().getPositions();


                DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(null, deviceCodes);
//                R<DeviceExt> byAttrId = deviceExtClient.getByAttrId(deviceInfo.getId(), CommonConstant.VEHICLE_WARCH_COORDS_CATEGORY_ID);
//                DeviceExt deviceExt = byAttrId == null ? null : byAttrId.getData();

                List<Coords> coords = new ArrayList<>();
                //获取坐标系。默认为国测局坐标系
                BaiduMapUtils.CoordsSystem deviceCoordsSystem = BaiduMapUtils.CoordsSystem.GC02;
//                if (deviceExt != null) {
//                    String attrValue = deviceExt.getAttrValue();
//                    coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(Integer.parseInt(attrValue));
//                }
                positionList.forEach(position -> {
                    Coords coord = new Coords();
                    coord.setLatitude(position.getLat());
                    coord.setLongitude(position.getLng());
                    coords.add(coord);
                });
                List<Coords> result = coords;

//                if (BaiduMapUtils.CoordsSystem.BD09LL.equals(coordsSystem) && !deviceCoordsSystem.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)) {
//                    result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);
//                }
                result=coordsTypeConvertUtil.coordsConvert(BaiduMapUtils.CoordsSystem.GC02,coordsSystem,coords);

                if (positionList != null && positionList.size() > 0) {

                    for (int i = 0; i < positionList.size(); i++) {
                        RealTimePositionDTO.Position position = positionList.get(i);
                        PositionDTO positionDTO = new PositionDTO();
                        positionDTO.setLat(CollectionUtil.isNotEmpty(result) ? result.get(i).getLatitude() : position.getLat());
                        positionDTO.setLng(CollectionUtil.isNotEmpty(result) ? result.get(i).getLongitude() : position.getLng());
                        positionDTO.setTime(position.getTime());
                        positionDTO.setSpeed(position.getSpeed());
                        positionDTO.setTimestamp(TimeUtil.getTimestamp(position.getTime(), TimeUtil.YYYYMMDDHHMMSS));
                        positionMap.put(position.getDeviceId(), positionDTO);
                    }
                    return new AsyncResult<HashMap<String, PositionDTO>>(positionMap);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.error("从大数据侧获取实时位置异常", ex);
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }

    /**
     * 根据设备编号、起始时间段查询设备历史轨迹
     *
     * @param deviceCode
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Future<List<PositionDTO>> getDeviceTrackPosition(String deviceCode, Date beginTime, Date endTime) {
        if (StringUtils.isBlank(deviceCode)) {
            return null;
        }
        String beingTimeStr = DateUtil.format(beginTime, DatePattern.PURE_DATETIME_PATTERN);
        String endTimeStr = DateUtil.format(endTime, DatePattern.PURE_DATETIME_PATTERN);
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceCode);
        params.put("beginTime", beingTimeStr);
        params.put("endTime", endTimeStr);
        try {
            log.info("GetDeviceTrackPosition From BigData:{}", JSON.toJSONString(params));
            BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
            List<TrackPositionDto> data = bigDataBody.getData();
            if (data != null && data.size() > 0) {
                TrackPositionDto trackPositionDto = data.get(0);
                List<TrackPositionDto.Position> positionList = trackPositionDto.getTracks();
                if (positionList != null && positionList.size() > 0) {
                    List<PositionDTO> positionDTOList = new ArrayList<PositionDTO>();
                    List<TrackPositionDto.Position> trackList = trackPositionDto.getTracks();

                    DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(null, deviceCode);
                    DeviceExt deviceExt = deviceExtClient.getByAttrId(deviceInfo.getId(), CommonConstant.VEHICLE_WARCH_COORDS_CATEGORY_ID).getData();

                    List<Coords> coords = new ArrayList<>();
                    //获取坐标系。默认为国测局坐标系
                    BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.GC02;
                    if (deviceExt == null) {
                        String attrValue = deviceExt.getAttrValue();
                        coordsSystem = BaiduMapUtils.CoordsSystem.getCoordsSystem(Integer.parseInt(attrValue));
                    }
                    positionList.forEach(position -> {
                        Coords coord = new Coords();
                        coord.setLatitude(position.getLat());
                        coord.setLongitude(position.getLng());
                        coords.add(coord);
                    });
                    List<Coords> result = coords;

                    if (!coordsSystem.value.equals(BaiduMapUtils.CoordsSystem.BD09LL.value)) {
                        //TODO 后期考虑是否要把坐标系转换的也放在MongoDB
                        result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);
                    }
                    if (trackList != null && trackList.size() > 0) {
                        trackList.stream().forEach(position -> {

                        });

                        for (int i = 0; i < trackList.size(); i++) {
                            TrackPositionDto.Position position = trackList.get(i);
                            PositionDTO positionDTO = new PositionDTO();
                            positionDTO.setLng(CollectionUtil.isNotEmpty(result) ? result.get(i).getLongitude() : position.getLng());
                            positionDTO.setLat(CollectionUtil.isNotEmpty(result) ? result.get(i).getLatitude() : position.getLat());
                            positionDTO.setTime(position.getEventTime());
                            positionDTOList.add(positionDTO);

                        }

                        TrackPositionDto.Statistics statistics = trackPositionDto.getStatistics();
                        TrackPositionDto lastInfo = new TrackPositionDto();
                        statistics.setTotalDistance(statistics.getTotalDistance());
                        statistics.setAvgSpeed(statistics.getAvgSpeed());
                        statistics.setMaxSpeed(statistics.getMaxSpeed());

                        lastInfo.setStatistics(statistics);
                        lastInfo.setDeviceId(deviceCode);
                        lastInfo.setPosition(trackList.get(trackList.size() - 1));
                        //将设备最新情况保存到redis中,过期时间是24小时
                        String cacheName = DEVICE_LAST_INFO + StringPool.COLON + deviceCode;
                        bladeRedisCache.setEx(cacheName, lastInfo, EXPIRATION_TIME_24HOURS);
                        return new AsyncResult<List<PositionDTO>>(positionDTOList);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("从大数据侧获取实时轨迹异常", ex);
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return null;
    }

    /**
     * 格式化数字,保留2位小数
     *
     * @param number
     * @return
     */
    private String formatNumber(String number) {
        if (StringUtils.isNotBlank(number)) {
            String format = NumberUtil.decimalFormat("#.00", Double.valueOf(number));
            return format;
        }
        return number;
    }

    /**
     * 获取设备今天0点开始到当前时间的历史轨迹
     *
     * @param deviceCode
     * @return
     */
    @Override
    public Future<List<PositionDTO>> getDeviceTrackRealTime(String deviceCode) {
        Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
        Date endTime = DateTime.now();
        return getDeviceTrackPosition(deviceCode, startTime, endTime);
    }


    /**
     * 根据设备获取最新信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    public Future<TrackPositionDto> getLastDeviceInfo(String deviceCode) {
        try {
            String cacheName = DEVICE_LAST_INFO + StringPool.COLON + deviceCode;
            TrackPositionDto lastInfo = bladeRedisCache.get(cacheName);
            if (lastInfo != null) {
                return new AsyncResult<TrackPositionDto>(lastInfo);
            }
            //执行一下获取最新轨迹后再从缓存中取,如果没有就返回null
            getDeviceTrackRealTime(deviceCode);
            lastInfo = bladeRedisCache.get(cacheName);
            if (lastInfo != null) {
                return new AsyncResult<TrackPositionDto>(lastInfo);
            }
            return null;
        } catch (Exception ex) {
            log.error("getLastDeviceInfo Exception", ex);
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }

    /**
     * 根据设备获取最新的行驶信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    public Future<TrackPositionDto.Statistics> getLastDeviceRunInfo(String deviceCode) {
        try {
            Future<TrackPositionDto> lastDeviceInfoResult = getLastDeviceInfo(deviceCode);
            if (lastDeviceInfoResult != null && lastDeviceInfoResult.get() != null) {
                TrackPositionDto realTimePosition = lastDeviceInfoResult.get();
                TrackPositionDto.Statistics statistics = realTimePosition.getStatistics();
                if (statistics != null) {
                    return new AsyncResult<TrackPositionDto.Statistics>(statistics);
                }
                return null;
            }
            return null;
        } catch (Exception ex) {
            log.error("getLastDeviceRunInfo Exception", ex);
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }
}
