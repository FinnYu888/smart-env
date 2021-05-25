package com.ai.apac.smartenv.vehicle.mapper;

import com.ai.apac.smartenv.event.vo.EventKpiCatalogVO;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.vo.EntityCategoryVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VehicleCategoryMapper extends BaseMapper<VehicleCategory> {

    /**
     * 自定义分页
     *
     * @param page
     * @return
     */
    List<VehicleCategoryVO> selectVehicleCategoryPage(IPage page, VehicleCategoryVO vehicleCategory);


    List<VehicleCategoryVO> tree();


}