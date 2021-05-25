package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.system.cache.AdminCityCache;
import com.ai.apac.smartenv.system.entity.AdministrativeCity;
import com.ai.apac.smartenv.system.service.IAdministrativeCityService;
import com.ai.apac.smartenv.system.vo.AdministrativeCityVO;
import com.ai.apac.smartenv.system.wrapper.AdministrativeCityWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/16 9:07 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/administrativeCity")
@Api(value = "行政区域城市信息", tags = "行政区域城市信息")
public class AdministrativeCityController {

    private IAdministrativeCityService administrativeCityService;

    /**
     * 获取城市信息树结构列表
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取城市信息树结构列表", notes = "获取城市信息树结构列表")
    public R<List<AdministrativeCityVO>> getTree(){
        return R.data(AdminCityCache.getCityTree());
    }

    /**
     * 获取省份/直辖市列表
     *
     * @return
     */
    @GetMapping("/province")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取省份/直辖市列表", notes = "获取省份/直辖市列表")
    public R<List<AdministrativeCityVO>> getProvince(){
        List<AdministrativeCity> list = administrativeCityService.list(new LambdaQueryWrapper<AdministrativeCity>().eq(AdministrativeCity::getParentId,0));
        return R.data(AdministrativeCityWrapper.build().listVO(list));
    }

    /**
     * 根据上级区域ID获取下级区域树
     *
     * @return
     */
    @GetMapping("/childTree")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据上级区域ID获取下级区域树", notes = "根据上级区域ID获取下级区域树")
    public R<List<AdministrativeCityVO>> getChildTree(@RequestParam Long parentId){
        List<AdministrativeCity> list = administrativeCityService.list(new LambdaQueryWrapper<AdministrativeCity>().eq(AdministrativeCity::getParentId,parentId));
        return R.data(AdministrativeCityWrapper.build().listTree(list));
    }

    /**
     * 根据城市名称查询城市
     *
     * @return
     */
    @GetMapping("/list")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "cityZh", value = "城市中文名", paramType = "query", dataType = "string")
    )
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "根据城市名称查询城市", notes = "根据城市名称查询城市")
    public R<List<AdministrativeCityVO>> getCity(@RequestParam String cityZh) {
        return R.data(AdminCityCache.getCityByName(cityZh));
    }

    /**
     * 根据城市ID查询城市名称
     *
     * @return
     */
    @GetMapping("/cityName")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "cityId", value = "城市ID", paramType = "query", dataType = "string")
    )
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据城市ID查询城市名称", notes = "根据城市ID查询城市名称")
    public R<String> getCityName(@RequestParam Long cityId){
        return R.data(AdminCityCache.getCityNameById(cityId));
    }
}
