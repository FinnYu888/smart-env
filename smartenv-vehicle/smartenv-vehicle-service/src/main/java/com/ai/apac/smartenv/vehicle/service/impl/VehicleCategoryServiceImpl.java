package com.ai.apac.smartenv.vehicle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.mapper.VehicleCategoryMapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleCategoryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆的分类信息 服务实现类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Service
public class VehicleCategoryServiceImpl extends BaseServiceImpl<VehicleCategoryMapper, VehicleCategory> implements IVehicleCategoryService {

    @Autowired
    private IVehicleInfoService vehicleInfoService;

    @Override
    public IPage<VehicleCategoryVO> selectVehicleCategoryPage(IPage<VehicleCategoryVO> page, VehicleCategoryVO vehicleCategory) {
        return page.setRecords(baseMapper.selectVehicleCategoryPage(page, vehicleCategory));
    }

    @Override
    public List<Long> getAllChildIdByParentId(Long categoryId) {
        List<Long> ids = new ArrayList<Long>();
        List<VehicleCategoryVO> parentVOS = new ArrayList<>();
        VehicleCategory wrapper = new VehicleCategory();
        wrapper.setParentCategoryId(categoryId);
        List<VehicleCategory> parent = baseMapper.selectList(Condition.getQueryWrapper(wrapper));
        if (CollectionUtil.isNotEmpty(parent)) {
            parentVOS = VehicleCategoryWrapper.build().listVO(parent);
            parentVOS.forEach(parentVO -> {
                ids.add(parentVO.getId());
                List<Long> children = this.getAllChildIdByParentId(parentVO.getId());
                ids.addAll(children);
            });
        }
        return ids;
    }

    @Override
    public List<VehicleCategoryVO> listVehicleCategoryByTenantId(String tenantId) {
        List<VehicleCategoryVO> vehicleCategoryVOList = new ArrayList<VehicleCategoryVO>();
        LambdaQueryWrapper<VehicleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleCategory::getTenantId, tenantId);
        wrapper.eq(VehicleCategory::getIsDeleted, BladeConstant.DB_NOT_DELETED);
        List<VehicleCategory> vehicleCategoryList = this.list(wrapper);
        if (CollectionUtil.isNotEmpty(vehicleCategoryList)) {
            vehicleCategoryVOList = VehicleCategoryWrapper.build().listVO(vehicleCategoryList);
        }
        return vehicleCategoryVOList;
    }

    @Override
    public boolean createVehicleCategory(VehicleCategory vehicleCategory) {
        if (isValidVehicleCategory(vehicleCategory)) {
           save(vehicleCategory);
           //再将CODE更新成ID
           vehicleCategory.setCategoryCode(vehicleCategory.getId().toString());
           vehicleCategory.setTenantId(AuthUtil.getTenantId());
           updateById(vehicleCategory);
           VehicleCategoryCache.saveOrUpdateVehicleCategory(VehicleCategoryWrapper.build().entityVO(vehicleCategory));
        }
        return true;
    }

    @Override
    public boolean updateVehicleCategory(VehicleCategory vehicleCategory) {
        if (!isValidVehicleCategory(vehicleCategory) || ObjectUtil.isEmpty(vehicleCategory.getId())) {
            throw new ServiceException("车辆ID不能为空");
        }
        updateById(vehicleCategory);
        vehicleCategory = this.getById(vehicleCategory.getId());
        VehicleCategoryCache.saveOrUpdateVehicleCategory(VehicleCategoryWrapper.build().entityVO(vehicleCategory));
        return true;
    }

    @Override
    public boolean deleteVehicleCategory(String vehicleCategoryIds) {
        List<Long> vehicleCategoryIdList = Func.toLongList(vehicleCategoryIds);
        vehicleCategoryIdList.stream().forEach(vehicleCategoryId -> {
            VehicleCategory vehicleCategory = this.getById(vehicleCategoryId);
            //判断该车辆类型是否已关联车辆,如果有关联则不能被删除
            List<VehicleInfo> vehicleInfos = vehicleInfoService.list(new QueryWrapper<VehicleInfo>().lambda().eq(VehicleInfo::getEntityCategoryId,vehicleCategory.getCategoryCode()));
            if (ObjectUtil.isNotEmpty(vehicleInfos) && vehicleInfos.size() > 0) {
                throw new ServiceException("该车辆类型已经与车辆关联,不能被删除");
            }
            //如果该车辆类型有下级车辆类型则不能被删除
            if (vehicleCategory != null) {
                Integer count = this.count(new LambdaQueryWrapper<VehicleCategory>().eq(VehicleCategory::getParentCategoryId, vehicleCategoryId));
                if (count > 0) {
                    throw new ServiceException("该车辆类型[" + vehicleCategory.getCategoryName() + "]有下级车辆类型,不能被删除");
                }
                boolean result = removeById(vehicleCategoryId);
                if (result) {
                    VehicleCategoryCache.deleteVehicleCategory(vehicleCategoryId);
                }
            }
        });
        return true;
    }

    @Override
    public List<VehicleCategoryVO> tree() {
        return ForestNodeMerger.merge(baseMapper.tree());

    }

    /**
     * 岗位信息校验
     *
     * @param vehicleCategory
     * @return
     */
    private boolean isValidVehicleCategory(VehicleCategory vehicleCategory) {
        if (StringUtils.isBlank(vehicleCategory.getCategoryName())) {
            throw new ServiceException("车辆类型名称不能为空");
        }
        if(ObjectUtil.isEmpty(vehicleCategory.getParentCategoryId())){
            vehicleCategory.setParentCategoryId(0L);
        }
        return true;
    }
}

