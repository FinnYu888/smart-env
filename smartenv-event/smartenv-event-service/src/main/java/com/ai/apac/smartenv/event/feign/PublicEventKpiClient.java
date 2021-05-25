package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import com.ai.apac.smartenv.event.service.IPublicEventKpiService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 4:24 下午
 **/
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class PublicEventKpiClient implements IPublicEventIKpiClient {

    @Autowired
    private IPublicEventKpiService publicEventKpiService;

    /**
     * 获取所有KPI
     *
     * @return
     */
    @Override
    @GetMapping(GET_ALL_KPI)
    public R<List<PublicEventKpi>> listAllKpi() {
        return R.data(publicEventKpiService.list(new QueryWrapper<PublicEventKpi>()));
    }

    /**
     * 根据城市ID获取所有KPI
     *
     * @param cityId
     * @return
     */
    @Override
    @GetMapping(GET_KPI_BY_CITY)
    public R<List<PublicEventKpi>> listKpiByCityId(@RequestParam Long cityId) {
        List<PublicEventKpi> list = publicEventKpiService.list(new LambdaQueryWrapper<PublicEventKpi>().eq(PublicEventKpi::getCityId, cityId));
        return R.data(list);
    }

    /**
     * 根据城市ID获取所有KPI
     *
     * @param kpiId
     * @return
     */
    @Override
    @GetMapping(GET_KPI_BY_ID)
    public R<PublicEventKpi> getKpiById(@RequestParam Long kpiId) {
        PublicEventKpi one = publicEventKpiService.getOne(new LambdaQueryWrapper<PublicEventKpi>().eq(PublicEventKpi::getId, kpiId));
        return R.data(one);
    }

}
