package com.ai.apac.smartenv.statistics.service;

import com.ai.apac.smartenv.statistics.entity.VehicleWorkStatResult;
import com.ai.apac.smartenv.statistics.vo.VehicleWorkStatVO;
import org.springblade.core.mp.base.BaseService;
import org.springframework.scheduling.annotation.Async;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/11 3:42 下午
 **/
public interface IVehicleWorkStatService extends BaseService<VehicleWorkStatResult> {

    /**
     * 查询指定日期下车辆机扫率统计数据
     * @param projectCode
     * @param statDate
     * @return
     */
    VehicleWorkStatVO getVehicleWorkStatVO(String projectCode,String statDate);

    /**
     * 新增Mock数据
     * @param projectCode
     */
    void genMockData(String projectCode,String statDate);

    Boolean statVehicleDistanceInfo(String tenantId, Date beginTime, Date endTime);

    void vehicleWorkStatRun(String startTime,String endTime, String statDate,List<String> projectCodeList);

    void removeVehicleWorkStat(String startTime,String endTime,String statDate,List<String> projectCodeList);

}
