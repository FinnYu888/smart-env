package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.mapper.CityMapper;
import com.ai.apac.smartenv.system.service.ICityService;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/8 10:10 上午
 **/
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements ICityService {

    /**
     * 获取所有城市
     *
     * @return
     */
    @Override
    public List<City> getAllCity() {
        return baseMapper.selectList(new QueryWrapper<City>());
    }

    /**
     * 获取城市树结构
     *
     * @return
     */
    @Override
    public List<CityVO> getCityTree() {
        List<City> allCity = getAllCity();
        return null;
    }

    /**
     * 根据主键查询城市信息
     *
     * @param cityId
     * @return
     */
    @Override
    public City getCityById(Long cityId) {
        return baseMapper.selectById(cityId);
    }

    /**
     * 根据主键查询城市名称
     *
     * @param cityId
     * @return
     */
    @Override
    public String getCityNameById(Long cityId) {
        City city = getCityById(cityId);
        if(city != null && city.getId() != null){
            return city.getCityName();
        }
        return null;
    }
}
