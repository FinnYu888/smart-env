package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.system.dto.CityDTO;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.system.dto.WeatherInfoDTO;
import com.ai.apac.smartenv.system.service.IWeatherService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/9 8:36 上午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/weather")
@Api(value = "天气", tags = "天气")
public class WeatherController extends BladeController {

    private IWeatherService weatherService;

    /**
     * 根据城市ID或城市名称获取天气情况
     */
    @GetMapping("")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityCode", value = "城市编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "cityName", value = "城市名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取天气情况(sojson)", notes = "通过sojson.com根据城市ID或城市名称获取天气情况")
    public R<WeatherDTO> getWeather(@RequestParam(required = false) String cityCode,
                                              @RequestParam(required = false) String cityName) {
        return weatherService.getWeather(cityCode, cityName);
    }

    /**
     * 根据城市名称获取天气情况
     */
    @GetMapping("/{cityName}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityName", value = "城市名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "根据城市名称获取天气情况(tianqiapi)", notes = "通过tianqiapi根据城市名称获取天气情况")
    public R<WeatherInfoDTO> getWeatherByCityName(@PathVariable String cityName) {
        return weatherService.getWeatherByName(cityName);
    }

    /**
     * 根据城市名称获取城市信息
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/city")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据城市名称获取城市信息", notes = "根据城市名称获取城市信息")
    public R<List<CityDTO>> getCityByName(@RequestParam String cityName) {
        return R.data(weatherService.getCityByName(cityName));
    }

    /**
     * 获取所有城市信息
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/allCity")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取所有城市信息", notes = "获取所有城市信息")
    public R<List<CityDTO>> getAllCity() {
        return R.data(weatherService.getAllCity());
    }
}
