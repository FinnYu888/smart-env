package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.system.cache.CityCache;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.system.service.ICityService;
import com.ai.apac.smartenv.system.service.ICityWeatherService;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.apac.smartenv.system.wrapper.CityWrapper;
import com.ai.apac.smartenv.system.wrapper.RoleWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/28 8:57 上午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/city")
@Api(value = "城市信息", tags = "城市信息")
public class CityController extends BladeController {

    private ICityWeatherService cityWeatherService;

    private ICityService cityService;

    /**
     * 获取城市信息树结构列表
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取城市信息树结构列表", notes = "获取城市信息树结构列表")
    @ApiLog(value = "获取城市信息树结构列表")
    public R<List<CityVO>> getCityTree() {
        return R.data(CityCache.getCityTree());
    }

    /**
     * 查询城市列表
     *
     * @return
     */
    @GetMapping("")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "cityZh", value = "城市中文名", paramType = "query", dataType = "string")
    )
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询城市列表", notes = "查询城市列表")
    @ApiLog(value = "查询城市列表")
    public R<List<CityVO>> getCity(@RequestParam String cityZh) {
        return R.data(CityCache.getCityByName(cityZh));
    }
}
