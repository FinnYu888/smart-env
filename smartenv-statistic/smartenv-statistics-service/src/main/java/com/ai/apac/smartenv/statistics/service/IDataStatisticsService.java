package com.ai.apac.smartenv.statistics.service;

import com.ai.apac.smartenv.statistics.dto.*;

/**
 * 综合数据统计服务类
 */
public interface IDataStatisticsService {

    Boolean initialSynthInfo(SynthInfoDTO synthInfoDTO);

    Boolean synthVehicleWorkInfo(VehicleWorkSynthInfoDTO vehicleWorkSynthInfoDTO);

    Boolean synthPersonWorkInfo(PersonWorkSynthInfoDTO personWorkSynthInfoDTO);

    Boolean synthVehicleStatInfo(VehicleStatSynthInfoDTO vehicleStatSynthInfoDTO);

    Boolean synthPersonStatInfo(PersonStatSynthInfoDTO personStatSynthInfoDTO);

    Boolean synthAlarmInfo(AlarmSynthInfoDTO alarmSynthInfoDTO);


}
