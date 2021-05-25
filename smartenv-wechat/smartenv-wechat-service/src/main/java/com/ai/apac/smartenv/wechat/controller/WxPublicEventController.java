package com.ai.apac.smartenv.wechat.controller;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.event.cache.PublicEventKpiCache;
import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import com.ai.apac.smartenv.system.cache.AdminCityCache;
import com.ai.apac.smartenv.system.vo.AdministrativeCityVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 7:42 下午
 **/
@RestController
@RequestMapping("/ma/public")
@Slf4j
@Api(value = "小程序公众投诉问题管理", tags = "小程序公众投诉问题管理")
public class WxPublicEventController {

    /**
     * 根据城市查询公众事件上报的KPI
     */
    @GetMapping("/publicEventKpi")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据城市查询公众事件上报的KPI", notes = "根据城市查询公众事件上报的KPI")
    public R<List<PublicEventKpi>> listPublicEventKpi(@RequestParam(required = false) String cityName, @RequestParam(required = false) Long cityId) {
        List<PublicEventKpi> list = null;
        if (cityId != null && cityId > 0L) {
            list = PublicEventKpiCache.getKpiByCityId(cityId);
        } else if(StringUtils.isNotBlank(cityName)){
            List<AdministrativeCityVO> cityList = AdminCityCache.getCityByName(cityName);
            if (CollUtil.isNotEmpty(cityList)) {
                list = PublicEventKpiCache.getKpiByCityId(cityList.get(0).getId());
            }
        }else{
            list = PublicEventKpiCache.getKpiByCityId(100000L);
        }
        return R.data(list);
    }

}
