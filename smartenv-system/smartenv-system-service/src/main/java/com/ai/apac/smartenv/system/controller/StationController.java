package com.ai.apac.smartenv.system.controller;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/26 5:54 下午
 **/

import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.service.IStationService;
import com.ai.apac.smartenv.system.vo.SimpleDictVO;
import com.ai.apac.smartenv.system.vo.StationVO;
import com.ai.apac.smartenv.system.wrapper.DictWrapper;
import com.ai.apac.smartenv.system.wrapper.StationWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制器
 *
 * @author qianlong
 */
@RestController
@AllArgsConstructor
@RequestMapping("/station")
@Api(value = "岗位管理", tags = "岗位管理")
public class StationController {

    private IStationService stationService;

    /**
     * 新增
     */
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增岗位", notes = "传入station")
    @ApiLog(value = "新增岗位信息")
    public R create(@Valid @RequestBody Station station) {
        return R.status(stationService.createStation(station));
    }

    /**
     * 修改
     */
    @PutMapping("/{stationId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改岗位", notes = "传入station")
    @ApiLog(value = "修改岗位信息")
    public R update(@PathVariable Long stationId, @Valid @RequestBody Station station) {
        station.setId(stationId);
        return R.status(stationService.updateStation(station));
    }

    /**
     * 获取岗位树形结构
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationName", value = "岗位名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "status", value = "岗位状态", paramType = "query", dataType = "integer")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "获取岗位树", notes = "获取岗位树")
    public R<List<INode>> getTree(@ApiIgnore @RequestParam Map<String, Object> queryCond, BladeUser bladeUser) {
        QueryWrapper<Station> queryWrapper = Condition.getQueryWrapper(queryCond, Station.class);
        List<Station> stationList = stationService.list(queryWrapper.lambda().eq(Station::getTenantId, bladeUser.getTenantId()));
        return R.data(StationWrapper.build().listNodeVO(stationList));
    }

    /**
     * 根据ID查询岗位详情
     *
     * @return
     */
    @GetMapping("/{stationId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationId", value = "岗位ID", paramType = "path", dataType = "long")
    })
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "根据ID查询岗位详情", notes = "根据ID查询岗位详情")
    public R<StationVO> getStationById(@PathVariable("stationId") Long stationId) {
        return R.data(StationWrapper.build().entityVO(stationService.getById(stationId)));
    }

    /**
     * 删除
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "删除岗位", notes = "传入岗位集合列表")
    @ApiLog(value = "修改岗位信息")
    public R deleteStation(@ApiParam(value = "岗位主键集合", required = true) @RequestParam("stationIds") String stationIds) {
        return R.status(stationService.deleteStation(stationIds));
    }

    /**
     * 获取可选的父级岗位树
     *
     * @return
     */
    @GetMapping("/{stationId}/parentTree")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationId", value = "当前岗位ID", paramType = "path", dataType = "long", required = true),
            @ApiImplicitParam(name = "stationLevel", value = "岗位级别", paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "stationName", value = "岗位名称", paramType = "query", dataType = "String")
    })
    @ApiOperation(value = "获取可选的父级岗位树", notes = "获取可选的父级岗位树")
    public R<List<INode>> getParentTree(@PathVariable Long stationId, @RequestParam(required = false) Integer stationLevel,
                                        @RequestParam(required = false) String stationName, BladeUser bladeUser) {
        List<Station> stationList = stationService.getParentStation(stationLevel, stationId, stationName, bladeUser.getTenantId());
        return R.data(StationWrapper.build().listNodeVO(stationList));
    }

    /**
     * 根据父岗位查询可供选择的岗位级别
     *
     * @param parentStationId
     * @return
     */
    @GetMapping("/stationLevel")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentStationId", value = "父岗位ID", paramType = "query", dataType = "long")
    })
    @ApiOperation(value = "根据父岗位查询可供选择的岗位级别", notes = "根据父岗位查询可供选择的岗位级别")
    public R<List<SimpleDictVO>> getStationLevel(@RequestParam(value = "parentStationId", required = false) Long parentStationId) {
        List<Dict> dictList = DictCache.getList("station_level");
        if (parentStationId == null || parentStationId == 0L) {
            return R.data(DictWrapper.build().listSimpleVO(dictList));
        } else {
            Station parentStation = StationCache.getStation(parentStationId);
            Integer parentStationLevel = parentStation.getStationLevel();
            List<Dict> result = dictList.stream().filter(dict -> Integer.valueOf(dict.getDictKey()) < parentStationLevel)
                    .collect(Collectors.toList());
            return R.data(DictWrapper.build().listSimpleVO(result));
        }
    }

    /**
     * 删除
     */
    @PutMapping("/status")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationIds", value = "岗位主键集合", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "newStatus", value = "新状态(1-启用,2-禁用)", paramType = "query", dataType = "integer")
    })
    @ApiOperation(value = "修改状态", notes = "修改状态")
    @ApiLog(value = "修改岗位状态")
    public R changeStationStatus(@RequestParam("stationIds") String stationIds, @RequestParam("newStatus") Integer newStatus) {
        return R.status(stationService.changeStationStatus(stationIds, newStatus));
    }
    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationName", value = "岗位名称", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入Station")
    @ApiLog(value = "查询岗位列表")
    public R<List<Station>> list(@ApiIgnore @RequestParam Map<String, Object> station, BladeUser bladeUser) {
        QueryWrapper<Station> queryWrapper = Condition.getQueryWrapper(station, Station.class);
        List<Station> dbList = stationService.list(queryWrapper);

        return R.data(dbList);
    }
}
