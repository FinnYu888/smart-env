package com.ai.apac.smartenv.statistics.service.impl;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import com.ai.apac.smartenv.statistics.mapper.VehicleDistanceInfoMapper;
import com.ai.apac.smartenv.statistics.mapper.VehicleWorkStatResultMapper;
import com.ai.apac.smartenv.statistics.service.IVehicleDistanceInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class VehicleDistanceInfoServiceImpl extends BaseServiceImpl<VehicleDistanceInfoMapper, VehicleDistanceInfo> implements IVehicleDistanceInfoService {

}
