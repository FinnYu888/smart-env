package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.entity.City;
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
public class CityWrapper extends BaseEntityWrapper<City, CityVO> {

    public static CityWrapper build() {
        return new CityWrapper();
    }

    @Override
    public CityVO entityVO(City entity) {
        CityVO cityInfoVO = BeanUtil.copy(entity, CityVO.class);
//        cityInfoVO.setCityZh(entity.getCityName());
//        cityInfoVO.setCityEn(entity.getCityName());
        Long parentId = cityInfoVO.getParentId();
        String parentName = CityCache.getCityNameById(parentId);
        cityInfoVO.setParentName(parentName);
        return cityInfoVO;
    }

    public List<INode> listNodeVO(List<City> list) {
        List<INode> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return ForestNodeMerger.merge(collect);
    }

    public List<CityVO> listTree(List<City> list){
        List<CityVO> cityList = list.stream().map(this::entityVO).collect(Collectors.toList());
        return ForestNodeMerger.merge(cityList);
    }
}
