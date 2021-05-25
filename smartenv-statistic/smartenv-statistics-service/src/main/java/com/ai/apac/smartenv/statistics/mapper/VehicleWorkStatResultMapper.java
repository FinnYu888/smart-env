package com.ai.apac.smartenv.statistics.mapper;

import com.ai.apac.smartenv.statistics.entity.VehicleWorkStatResult;
import com.ai.apac.smartenv.statistics.vo.RealWorkAcreage;
import com.ai.apac.smartenv.statistics.vo.VehicleWorkstatResultVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleWorkStatResultMapper extends BaseMapper<VehicleWorkStatResult> {

    /**
     * 自定义分页
     *
     * @param page
     * @param vehicleWorkstatResult
     * @return
     */
    List<VehicleWorkstatResultVO> selectVehicleWorkstatResultPage(IPage page, VehicleWorkstatResultVO vehicleWorkstatResult);

    /**
     * 根据项目、作业类型查询真实作业面积总和
     * @param projectList
     * @param vehicleWorkType
     * @return
     */
    List<RealWorkAcreage> sumRealWorkAcreage(@Param("projectList") String projectList,@Param("vehicleWorkType") Integer vehicleWorkType);
}
