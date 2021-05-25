package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.system.entity.AdministrativeCity;
import com.ai.apac.smartenv.system.mapper.CityBaiduMapper;
import com.ai.apac.smartenv.system.service.IAdministrativeCityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/16 4:57 下午
 **/
@Service
public class AdministrativeCityServiceImpl extends ServiceImpl<CityBaiduMapper, AdministrativeCity> implements IAdministrativeCityService {


    /**
     * 获取所有数据
     *
     * @return
     */
    @Override
    public List<AdministrativeCity> getAllCity() {
        return baseMapper.selectList(new QueryWrapper<AdministrativeCity>());
    }

    /**
     * 获取城市树
     *
     * @return
     */
    @Override
    public List<AdministrativeCity> getCityTree() {
        return null;
    }
}
