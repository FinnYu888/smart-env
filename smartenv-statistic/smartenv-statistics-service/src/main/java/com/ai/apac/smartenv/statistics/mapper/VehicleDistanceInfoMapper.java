package com.ai.apac.smartenv.statistics.mapper;

import com.ai.apac.smartenv.statistics.entity.RptPersonInfo;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import com.ai.apac.smartenv.statistics.vo.RptPersonInfoVO;
import com.ai.apac.smartenv.statistics.vo.VehicleDistanceInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface VehicleDistanceInfoMapper extends BaseMapper<VehicleDistanceInfo> {

    /**
     * 自定义分页
     *
     * @param page
     * @param vehicleDistanceInfo
     * @return
     */
    List<VehicleDistanceInfoVO> selectVehicleDistanceInfoPage(IPage page, VehicleDistanceInfoVO vehicleDistanceInfo);

}
