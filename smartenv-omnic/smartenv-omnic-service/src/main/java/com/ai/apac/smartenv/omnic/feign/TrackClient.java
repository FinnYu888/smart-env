package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceExtClient;
import com.ai.apac.smartenv.omnic.dto.BigDataRespDto;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: TrackClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/21  2:23    panfeng          v1.0.0             修改原因
 */
@RestController
@RequiredArgsConstructor
public class TrackClient implements ITrackClient {

    @Autowired
    private IDeviceClient deviceClient;
    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Autowired
    private IDeviceExtClient deviceExtClient;

    /**
     * 获取历史轨迹。不会对坐标进行历史转换
     *
     * @param entityId
     * @param entityType
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    @GetMapping(getBigdataTrack)
    public R<TrackPositionDto> getBigdataTrack(@RequestParam Long entityId, @RequestParam Long entityType, @RequestParam Long startTime, @RequestParam Long endTime) throws IOException, ParseException {
        Long positionCategory = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(entityType)) {

            positionCategory = VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE;
        } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(entityType)) {
            positionCategory = VehicleConstant.PERSON_POSITION_DEVICE_TYPE;
        }

        List<DeviceInfo> deviceInfos = deviceClient.getForTrack(entityId, entityType, positionCategory, startTime, endTime).getData();
        if (CollectionUtil.isNotEmpty(deviceInfos)) {
            TrackPositionDto trackPositionAll = new TrackPositionDto();
            trackPositionAll.setRounds(0);
            for (int i = 0; i < deviceInfos.size(); i++) {
                if (i == deviceInfos.size() - 1) {
                    break;
                }
                DeviceInfo deviceInfo = deviceInfos.get(i);
                DeviceInfo next = deviceInfos.get(i + 1);
                Date start = deviceInfo.getUpdateTime();
                Date end = next.getUpdateTime();
                Map<String, Object> params = new HashMap<>();
                params.put("deviceId", deviceInfo.getDeviceCode());
                params.put("beginTime", dateFormat.format(start));
                params.put("endTime", dateFormat.format(end));
                BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
                List<TrackPositionDto> data = bigDataBody.getData();
                if (CollectionUtil.isNotEmpty(data)) {
                    TrackPositionDto trackPositionDto = data.get(0);
                    trackPositionAll.setDeviceId(trackPositionDto.getDeviceId());
                    trackPositionAll.setPosition(trackPositionDto.getPosition());
                    trackPositionAll.setRounds(trackPositionAll.getRounds() + trackPositionDto.getRounds());
                    if (CollectionUtil.isNotEmpty(trackPositionDto.getTracks())) {
                        List<TrackPositionDto.Position> positions = trackPositionAll.getTracks() == null ? new ArrayList<TrackPositionDto.Position>() : trackPositionAll.getTracks();
                        positions.addAll(trackPositionDto.getTracks());
                        trackPositionAll.setTracks(positions);
                    }
                    if (trackPositionDto.getStatistics() != null) {
                        TrackPositionDto.Statistics statistics = trackPositionAll.getStatistics() == null ? new TrackPositionDto.Statistics() : trackPositionAll.getStatistics();
                        Double avgSpeed = statistics.getAvgSpeed() == null ? 0L : Double.parseDouble(statistics.getAvgSpeed());
                        Long totalDistance = statistics.getTotalDistance() == null ? 0L : Long.parseLong(statistics.getTotalCount());
                        Double maxSpeed = statistics.getMaxSpeed() == null ? 0L : Double.parseDouble(statistics.getMaxSpeed());
                        Long totalCount = statistics.getTotalCount() == null ? 0L : Long.parseLong(statistics.getTotalCount());

                        Double newAvgSpeed = trackPositionDto.getStatistics().getAvgSpeed() == null ? 0 : Double.parseDouble(trackPositionDto.getStatistics().getAvgSpeed());
                        Long newTotalDistance = trackPositionDto.getStatistics().getTotalDistance() == null ? 0L : Long.parseLong(trackPositionDto.getStatistics().getTotalCount());
                        Double newMaxSpeed = trackPositionDto.getStatistics().getMaxSpeed() == null ? 0L : Double.parseDouble(trackPositionDto.getStatistics().getMaxSpeed());
                        Long newTotalCount = trackPositionDto.getStatistics().getTotalCount() == null ? 0L : Long.parseLong(trackPositionDto.getStatistics().getTotalCount());

                        Double avgResult = (avgSpeed + newAvgSpeed) / 2;
                        Long distanceResult = totalDistance + newTotalDistance;
                        Double maxSpeedResult = maxSpeed.doubleValue() > newAvgSpeed.doubleValue() ? maxSpeed : newMaxSpeed;
                        Long totalCountResult = totalCount + newTotalCount;

                        statistics.setAvgSpeed(avgResult.toString());
                        statistics.setTotalDistance(distanceResult.toString());
                        statistics.setMaxSpeed(maxSpeedResult.toString());
                        statistics.setTotalCount(totalCountResult.toString());
                        trackPositionDto.setStatistics(statistics);

                    }
                    //进行坐标系转换，统一转为百度地图坐标系
                    for (TrackPositionDto.Position position : trackPositionDto.getTracks()) {
                        Date parse = dateFormat.parse(position.getEventTime());
                        position.setEventTime(String.valueOf(parse.getTime()));


                    }
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
                    //TODO 后期考虑是否要把坐标系转换的也放在MongoDB
                    List<Coords> result = baiduMapUtils.coordsToBaiduMapllAll(coordsSystem, coords);
                    for (int j = 0; j < trackPositionDto.getTracks().size(); j++) {
                        TrackPositionDto.Position position = trackPositionDto.getTracks().get(j);
                        Coords coords1 = result.get(j);


                        position.setLat(coords1.getLatitude());
                        position.setLng(coords1.getLongitude());
                    }
                }

            }
            return R.data(trackPositionAll);
        }
        return R.data(null);
    }


}
