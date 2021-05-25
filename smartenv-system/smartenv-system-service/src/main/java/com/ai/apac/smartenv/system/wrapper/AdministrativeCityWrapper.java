package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.system.cache.AdminCityCache;
import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.entity.AdministrativeCity;
import com.ai.apac.smartenv.system.vo.AdministrativeCityVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/8 10:15 上午
 **/
public class AdministrativeCityWrapper extends BaseEntityWrapper<AdministrativeCity, AdministrativeCityVO> {

    public static AdministrativeCityWrapper build() {
        return new AdministrativeCityWrapper();
    }

    @Override
    public AdministrativeCityVO entityVO(AdministrativeCity entity) {
        AdministrativeCityVO cityInfoVO = BeanUtil.copy(entity, AdministrativeCityVO.class);
        Long parentId = cityInfoVO.getParentId();
        String parentName = AdminCityCache.getCityNameById(parentId);
        cityInfoVO.setParentName(parentName);
        return cityInfoVO;
    }

    public List<INode> listNodeVO(List<AdministrativeCity> list) {
        List<INode> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return ForestNodeMerger.merge(collect);
    }

    public List<AdministrativeCityVO> listTree(List<AdministrativeCity> list){
        List<AdministrativeCityVO> cityList = list.stream().map(this::entityVO).collect(Collectors.toList());
        return ForestNodeMerger.merge(cityList);
    }
}
