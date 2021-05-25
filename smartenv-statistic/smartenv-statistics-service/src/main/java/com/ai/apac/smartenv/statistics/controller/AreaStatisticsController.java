package com.ai.apac.smartenv.statistics.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.omnic.vo.TrashStatVO;
import com.ai.apac.smartenv.statistics.dto.DeviceOnlineInfoDTO;
import com.ai.apac.smartenv.statistics.dto.SynthInfoDTO;
import com.ai.apac.smartenv.statistics.dto.VehicleWorkSynthInfoDTO;
import com.ai.apac.smartenv.statistics.entity.*;
import com.ai.apac.smartenv.statistics.service.IAreaStatisticsService;
import com.ai.apac.smartenv.statistics.vo.AssessStatVO;
import com.ai.apac.smartenv.system.cache.AdminCityCache;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.core.spi.service.StatisticsService;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 按区域查询数据,供EasyV大屏使用
 *
 * @author qianlong
 */
@RestController
@AllArgsConstructor
@RequestMapping("/easyv/area")
@Api(value = "省市级数据汇总分析", tags = "省市级数据汇总分析")
@Slf4j
public class AreaStatisticsController {

    private static List<String> assessItemList = new ArrayList<String>();

    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    static HashMap<String, AreaWorkInfo> areaWorkInfoMap = new HashMap<String, AreaWorkInfo>();

    static List<String> areaProjectList = new ArrayList<String>();

    @Autowired
    private BladeRedis bladeRedis;

    static {
        areaProjectList.add("100000");
        areaProjectList.add("360000");
        areaProjectList.add("130000");
        areaProjectList.add("440000");
        areaProjectList.add("130900");
        areaProjectList.add("360426");
        areaProjectList.add("361000");
        areaProjectList.add("361022");
        areaProjectList.add("441900");
        areaProjectList.add("130900");
        areaProjectList.add("361128");
        areaProjectList.add("361128");
    }

    static {
        assessItemList.add("主干道普扫");
        assessItemList.add("处理响应不及时");
        assessItemList.add("景观道清扫");
        assessItemList.add("河道清洁");
        assessItemList.add("中转站有异味");
        assessItemList.add("小区垃圾桶收运不及时");
        assessItemList.add("零星建筑垃圾清运");
        assessItemList.add("大件废弃物清运");
        assessItemList.add("公厕异味");
        assessItemList.add("中转站周边不干净");
        assessItemList.add("交通护栏清洁");
        assessItemList.add("主干道绿化带清洁");
    }

    @Autowired
    private IAreaStatisticsService areaStatisticsService;

    /**
     * 根据区域查询该区域下所有项目的汇总数据
     *
     * @return
     */
    @GetMapping("/projectSummary")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据区域查询该区域下所有项目的汇总数据", notes = "根据区域查询该区域下所有项目的汇总数据")
    public AreaProjectInfo getAreaProjectInfo(@RequestParam(value = "areaCode", required = false) String areaCode, @RequestParam(value = "statDate", required = false) String statDate) {
        log.info("getAreaProjectSummary for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        AreaProjectInfo areaProjectInfo = bladeRedis.get("AreaProjectInfo:" + areaCode);
        if (areaProjectInfo != null) {
            return areaProjectInfo;
        }
        areaProjectInfo = areaStatisticsService.getAreaProjectInfo(areaCode, statDate);
        log.info("Project[{}] is null?:{}", areaCode, (areaProjectInfo == null));
        if (areaProjectInfo == null) {
            areaProjectInfo = new AreaProjectInfo(null, areaCode, 0, 0, 0, 0, DateUtil.today());
        }
        bladeRedis.setEx("AreaProjectInfo:" + areaCode, areaProjectInfo, 300L);
        return areaProjectInfo;
    }

    /**
     * 保存区域项目汇总数据
     *
     * @return
     */
    @PostMapping("/projectSummaryInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "保存区域项目汇总数据", notes = "保存区域项目汇总数据")
    public R saveAreaProjectInfo(@RequestBody AreaProjectInfo areaProjectInfo) {
        return R.status(areaStatisticsService.saveAreaProjectInfo(areaProjectInfo));
    }

    /**
     * 根据区域查询该区域下的工作完成率
     *
     * @return
     */
    @GetMapping("/areaWorkInfo")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据区域查询该区域下的工作完成率", notes = "根据区域查询该区域下的工作完成率")
    public AreaWorkInfo getAreaWorkInfo(@RequestParam(value = "areaCode", required = false) String areaCode, @RequestParam(value = "statDate", required = false) String statDate) {
        log.info("getAreaWorkInfo for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
//        if (!areaProjectList.contains(areaCode)) {
//            return null;
//        }
        AreaWorkInfo areaWorkInfo = bladeRedis.get("AreaWorkInfo:" + areaCode);
        if (areaWorkInfo == null) {
            areaWorkInfo = areaStatisticsService.getAreaWorkInfo(areaCode, statDate);
            if (areaWorkInfo == null) {
                statDate = cn.hutool.core.date.DateUtil.today();
                if (areaWorkInfoMap.get(areaCode) == null) {
                    areaWorkInfo = new AreaWorkInfo();
                    areaWorkInfo.setAreaCode(areaCode);
                    areaWorkInfo.setStatDate(statDate);
//                areaWorkInfo.setTotalWorkAreaForVehicle(RandomUtil.randomDouble(1500.00, 3000.00, 2, RoundingMode.CEILING));
//                areaWorkInfo.setCompletedWorkAreaForVehicle(Double.valueOf(DECIMAL_FORMAT.format(areaWorkInfo.getTotalWorkAreaForVehicle() * RandomUtil.randomDouble(0.4, 0.8, 2, RoundingMode.CEILING))));
//                areaWorkInfo.setTotalWorkAreaForPerson(RandomUtil.randomDouble(80.00, 200.00, 2, RoundingMode.CEILING));
//                areaWorkInfo.setCompletedWorkAreaForPerson(Double.valueOf(DECIMAL_FORMAT.format(areaWorkInfo.getTotalWorkAreaForPerson() * RandomUtil.randomDouble(0.4, 0.8, 2, RoundingMode.CEILING))));
                    areaWorkInfoMap.put(areaCode, areaWorkInfo);
                }
                areaWorkInfo = areaWorkInfoMap.get(areaCode);
            }
            bladeRedis.setEx("AreaWorkInfo:" + areaCode, areaWorkInfo, 300L);
        }
        return areaWorkInfo;
    }

    /**
     * 保存区域工作完成情况数据
     *
     * @return
     */
    @PostMapping("/areaWorkInfo")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "保存区域工作完成情况数据", notes = "保存区域工作完成情况数据")
    public R saveAreaWorkInfo(@RequestBody AreaWorkInfo areaWorkInfo) {
        return R.status(areaStatisticsService.saveAreaWorkInfo(areaWorkInfo));
    }

    /**
     * 根据区域查询该区域下的垃圾收运数据
     *
     * @return
     */
    @GetMapping("/areaTrashInfo")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据区域查询该区域下的垃圾收运数据", notes = "根据区域查询该区域下的垃圾收运数据")
    public AreaTrashInfo getAreaTrashInfo(@RequestParam(value = "areaCode", required = false) String areaCode, @RequestParam(value = "statDate", required = false) String statDate) {
        log.info("getAreaTrashInfo for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        AreaTrashInfo areaTrashInfo = bladeRedis.get("AreaTrashInfo:" + areaCode);
        if (areaTrashInfo == null) {
            areaTrashInfo = areaStatisticsService.getAreaTrashInfo(areaCode, statDate);
            if (areaTrashInfo == null) {
                areaTrashInfo = new AreaTrashInfo();
            }
            bladeRedis.setEx("AreaTrashInfo:" + areaCode, areaTrashInfo, 300L);
        }
        return areaTrashInfo;
    }

    /**
     * 根据区域查询最近7天(不包含今天)的垃圾收运历史数字统计
     *
     * @return
     */
    @GetMapping("/last7DaysAreaTrashCount")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询最近7天垃圾收运数字统计", notes = "查询最近7天垃圾收运数字统计")
    public List<AreaTrashInfo> last7DaysAreaTrashCount(@RequestParam(value = "areaCode", required = false) String areaCode) {
        log.info("last7DaysAreaTrashCount for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        Date endDate = DateUtil.minusDays(new Date(), 1);
        Date startDate = DateUtil.minusDays(new Date(), 7);
        List<AreaTrashInfo> list = bladeRedis.get("last7DaysAreaTrashCount:" + areaCode);
        if (CollUtil.isEmpty(list)) {
            list = areaStatisticsService.getAreaTrashInfoHistory(areaCode, DateUtil.format(startDate, DatePattern.NORM_DATE_PATTERN), DateUtil.format(endDate, DatePattern.NORM_DATE_PATTERN));
            bladeRedis.setEx("last7DaysAreaTrashCount:" + areaCode, list, 300L);
        }
        return list;
//        List<AreaTrashInfo> dataList = new ArrayList<>();
//        //查询最近7天的数据
//        for (int i = 7; i >= 1; i--) {
//            Date date = DateUtil.minusDays(new Date(), i);
//            String dateStr = DateUtil.format(date, "yyyy-MM-dd");
//            AreaTrashInfo data = new AreaTrashInfo();
//            data.setAreaCode(areaCode);
//            data.setItemType("all");
//            data.setStatDate(dateStr);
////            data.setWeight(RandomUtil.randomDouble(100.00, 180.00, 2, RoundingMode.CEILING));
//            data.setWeight(0.1);
//            dataList.add(data);
//        }
//        return dataList;
    }

    /**
     * 保存区域垃圾收运情况数据
     *
     * @return
     */
    @PostMapping("/areaTrashInfo")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "保存区域垃圾收运情况数据", notes = "保存区域垃圾收运情况数据")
    public R saveAreaWorkInfo(@RequestBody AreaTrashInfo areaTrashInfo) {
        return R.status(areaStatisticsService.saveAreaTrashInfo(areaTrashInfo));
    }

    /**
     * 根据区域查询该区域下的历史告警数字统计
     *
     * @return
     */
    @GetMapping("/areaAlarmCountHistory")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据区域查询该区域下的历史告警数字统计", notes = "根据区域查询该区域下的历史告警数字统计")
    public List<AreaAlarmCountInfo> getAreaAlarmInfo(@RequestParam(value = "areaCode", required = false) String areaCode, @RequestParam String startDate, @RequestParam String endDate) {
        log.info("getAreaAlarmInfo for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        List<AreaAlarmCountInfo> list = bladeRedis.get("getAreaAlarmInfo:" + areaCode);
        if (CollUtil.isEmpty(list)) {
            list = areaStatisticsService.getAreaAlarmCountHistory(areaCode, startDate, endDate);
            bladeRedis.setEx("getAreaAlarmInfo:" + areaCode, list, 300L);
        }
        return list;
    }

    /**
     * 根据区域查询最近7天(不包含今天)的历史告警数字统计
     *
     * @return
     */
    @GetMapping("/last7DaysAreaAlarmCount")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "根据区域查询最近7天告警数字统计", notes = "根据区域查询最近7天告警数字统计")
    public List<AreaAlarmCountInfo> last7DaysAreaAlarmCount(@RequestParam(value = "areaCode", required = false) String areaCode) {
        log.info("last7DaysAreaAlarmCount for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        if (!areaProjectList.contains(areaCode)) {
            return null;
        }
        Date endDate = DateUtil.minusDays(new Date(), 1);
        Date startDate = DateUtil.minusDays(new Date(), 7);

//        List<AreaAlarmCountInfo> dataList = new ArrayList<>();
//        //查询最近7天的数据
//        for (int i = 7; i >= 1; i--) {
//            Date date = DateUtil.minusDays(new Date(), i);
//            String dateStr = DateUtil.format(date, "yyyy-MM-dd");
//            AreaAlarmCountInfo areaAlarmCountInfo = new AreaAlarmCountInfo();
//            areaAlarmCountInfo.setAreaCode(areaCode);
//            areaAlarmCountInfo.setStatDate(dateStr);
//            areaAlarmCountInfo.setCount(RandomUtil.randomLong(50, 200));
//            dataList.add(areaAlarmCountInfo);
//        }
//        return dataList;
        List<AreaAlarmCountInfo> list = bladeRedis.get("last7DaysAreaAlarmCount:" + areaCode);
        if (CollUtil.isEmpty(list)) {
            list = areaStatisticsService.getAreaAlarmCountHistory(areaCode, DateUtil.format(startDate, DatePattern.NORM_DATE_PATTERN), DateUtil.format(endDate, DatePattern.NORM_DATE_PATTERN));
            bladeRedis.setEx("last7DaysAreaAlarmCount:" + areaCode, list, 300L);
        }
        return list;
    }

    /**
     * 批量新增区域告警数据
     *
     * @param areaAlarmCountInfoList
     * @return
     */
    @PostMapping("/batchAreaAlarmCount")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "批量新增区域告警数据", notes = "批量新增区域告警数据")
    public R batchSaveAreaAlarmCount(@RequestBody List<AreaAlarmCountInfo> areaAlarmCountInfoList) {
        areaAlarmCountInfoList.stream().forEach(areaAlarmCountInfo -> {
            if (areaAlarmCountInfo.getCount() <= 0L) {
                areaAlarmCountInfo.setCount(RandomUtil.randomLong(100, 500));
            }
            areaStatisticsService.saveAlarmCount(areaAlarmCountInfo);
        });
        return R.status(true);
    }

    /**
     * 查询考核问题数据
     *
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询考核问题数据", notes = "查询考核问题数据")
    @GetMapping("/assessStatInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 10)
    @ResponseBody
    public List<AssessStatVO> getAssessStatInfo(@RequestParam(required = false) String areaCode) {
        log.info("getAssessStatInfo for {}", areaCode);
        List<AssessStatVO> dataList = bladeRedis.get("getAssessStatInfo:" + areaCode);
        if (CollUtil.isNotEmpty(dataList)) {
            return dataList;
        }
        List<AreaIllegalBehaviorInfo> areaIllegalBehaviorForVehicleList = areaStatisticsService.getAreaIllegalBehaviorForVehicle(areaCode, null, null);
        if (CollUtil.isEmpty(areaIllegalBehaviorForVehicleList)) {
            return null;
        }
        List<AssessStatVO> list = new ArrayList<AssessStatVO>();
        assessItemList.stream().forEach(assessItemName -> {
            list.add(new AssessStatVO(assessItemName, RandomUtil.randomInt(10, 200)));
        });
        bladeRedis.setEx("getAssessStatInfo:" + areaCode, list, 300L);
        return dataList;
    }

    /**
     * 查询车辆告警分析
     *
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询车辆告警分析", notes = "查询车辆告警分析")
    @GetMapping("/areaIllegalBehaviorForVehicle")
    @ApiOperationSupport(order = 11)
    @ResponseBody
    public List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForVehicle(@RequestParam(required = false) String areaCode, @RequestParam(required = false) String projectCode,
                                                                          @RequestParam(value = "statDate", required = false) String statDate) {
        log.info("getAreaIllegalBehaviorForVehicle for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        statDate = cn.hutool.core.date.DateUtil.today();
        List<AreaIllegalBehaviorInfo> dataList = bladeRedis.get("getAreaIllegalBehaviorForVehicle:" + areaCode);
        if (CollUtil.isEmpty(dataList)) {
            dataList = areaStatisticsService.getAreaIllegalBehaviorForVehicle(areaCode, projectCode, statDate);
//        if (CollUtil.isEmpty(dataList)) {
//            dataList = new ArrayList<AreaIllegalBehaviorInfo>();
//            dataList.add(new AreaIllegalBehaviorInfo(RandomUtil.randomString(5), areaCode, statDate, "滞留", "1", "vehicle", RandomUtil.randomInt(100, 200), statDate));
//            dataList.add(new AreaIllegalBehaviorInfo(RandomUtil.randomString(5), areaCode, statDate, "越界", "2", "vehicle", RandomUtil.randomInt(100, 200), statDate));
//            dataList.add(new AreaIllegalBehaviorInfo(RandomUtil.randomString(5), areaCode, statDate, "超速", "3", "vehicle", RandomUtil.randomInt(100, 200), statDate));
//        }
            bladeRedis.setEx("getAreaIllegalBehaviorForVehicle:" + areaCode, dataList, 30L);
        }
        return dataList;
    }

    /**
     * 查询人员告警分析
     *
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询人员告警分析", notes = "查询人员告警分析")
    @GetMapping("/areaIllegalBehaviorForPerson")
    @ApiOperationSupport(order = 12)
    @ResponseBody
    public List<AreaIllegalBehaviorInfo> getAreaIllegalBehaviorForPerson(@RequestParam(required = false) String areaCode, @RequestParam(required = false) String projectCode,
                                                                         @RequestParam(value = "statDate", required = false) String statDate) {
        log.info("getAreaIllegalBehaviorForPerson for {}", areaCode);
        if (StringUtils.isBlank(areaCode)) {
            areaCode = "100000";
        }
        List<AreaIllegalBehaviorInfo> dataList = bladeRedis.get("getAreaIllegalBehaviorForPerson:" + areaCode);
        if (CollUtil.isEmpty(dataList)) {
            dataList = areaStatisticsService.getAreaIllegalBehaviorForPerson(areaCode, projectCode, statDate);
//        if (CollUtil.isEmpty(dataList)) {
//            dataList = new ArrayList<AreaIllegalBehaviorInfo>();
//            dataList.add(new AreaIllegalBehaviorInfo(RandomUtil.randomString(5), areaCode, statDate, "滞留", "1", "person", RandomUtil.randomInt(100, 200), statDate));
//            dataList.add(new AreaIllegalBehaviorInfo(RandomUtil.randomString(5), areaCode, statDate, "越界", "2", "person", RandomUtil.randomInt(100, 200), statDate));
//        }
            bladeRedis.setEx("getAreaIllegalBehaviorForPerson:" + areaCode, dataList, 30L);
        }
        return dataList;
    }

    @ApiOperation(value = "批量保存告警分析数据", notes = "批量保存告警分析数据")
    @PostMapping("/batchAreaIllegalBehaviorInfo")
    @ApiOperationSupport(order = 13)
    @ResponseBody
    public R batchSaveAreaIllegalBehaviorInfo(@RequestBody List<AreaIllegalBehaviorInfo> areaIllegalBehaviorInfoList) {
        areaIllegalBehaviorInfoList.stream().forEach(areaIllegalBehaviorInfo -> {
            areaStatisticsService.saveAreaIllegalBehaviorInfo(areaIllegalBehaviorInfo);
        });
        return R.status(true);
    }

    @ApiOperation(value = "批量保存垃圾收运数据", notes = "批量保存垃圾收运数据")
    @PostMapping("/batchAreaTrashInfo")
    @ApiOperationSupport(order = 14)
    @ResponseBody
    public R batchSaveTrashInfo(@RequestBody List<AreaTrashInfo> areaTrashInfoList) {
        areaTrashInfoList.stream().forEach(areaTrashInfo -> {
            areaStatisticsService.saveAreaTrashInfo(areaTrashInfo);
        });
        return R.status(true);
    }


    /**
     * 根据地区ID或者公司ID或者项目ID查询项目的数据统计信息
     *
     * @return
     */
    @GetMapping("/synthInfo")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "根据地区ID或者公司ID或者项目ID查询项目的数据统计信息", notes = "根据地区ID或者公司ID或者项目ID查询项目的数据统计信息")
    public R<SynthInfoDTO> getSynthInfo(
            @RequestParam(name = "areaId", required = false) String areaId,
            @RequestParam(name = "adcode", required = false) String adcode,
            @RequestParam(name = "comyId", required = false) String comyId,
            @RequestParam(name = "projectCode", required = false) String projectCode) {
        if (ObjectUtil.isEmpty(adcode)) {
            adcode = areaId;
        }
        return R.data(areaStatisticsService.getSynthInfo(adcode, comyId, projectCode));
    }

    @GetMapping("/areaName")
    @ApiOperation(value = "根据区域Code获取区域名称", notes = "根据区域Code获取区域名称")
    @ApiOperationSupport(order = 16)
    @ResponseBody
    public R<String> getAreaName(@RequestParam String areaCode) {
        return R.data(AdminCityCache.getCityNameById(Long.valueOf(areaCode)));
    }

    @PostMapping("/projectInfo")
    @ApiOperation(value = "保存项目综合信息", notes = "保存项目综合信息")
    @ApiOperationSupport(order = 17)
    @ResponseBody
    public R saveProjectInfo(@RequestBody ProjectInfo projectInfo) {
        areaStatisticsService.saveProjectInfo(projectInfo);
        return R.status(true);
    }

    @GetMapping("/projectList")
    @ApiOperation(value = "获取项目综合信息", notes = "获取项目综合信息")
    @ApiOperationSupport(order = 18)
    @ResponseBody
    public R<List<ProjectInfo>> getProjectList(@RequestParam(required = false, defaultValue = "100000") String areaCode) {
        List<ProjectInfo> projectInfoList = bladeRedis.get("getProjectList:" + areaCode);
        if (CollUtil.isEmpty(projectInfoList)) {
            projectInfoList = areaStatisticsService.listProjectInfo(areaCode);
            bladeRedis.setEx("getProjectList:" + areaCode, projectInfoList, 300L);
        }
        return R.data(projectInfoList);
    }


    /**
     * 根据地区ID查询项目下的所有车辆实时作业完成率
     *
     * @return
     */
    @GetMapping("/vehicle/operationrate")
    @ApiOperationSupport(order = 19)
    @ApiOperation(value = "根据地区ID查询项目下的所有车辆最新一次的实时作业完成率", notes = "根据地区ID查询项目下的所有车辆最新一次的实时作业完成率")
    public R<List<VehicleWorkSynthInfoDTO>> getRealVehicleOperationrate(@RequestParam(name = "adcode", required = true) String adcode,
                                                                        @RequestParam(name = "projectCode", required = false) String projectCode,
                                                                        @RequestParam(name = "today", required = false) String today) {
        return R.data(areaStatisticsService.getRealVehicleOperationrate(adcode, projectCode,today));

    }

    /**
     * 根据项目编码查询设备在线统计
     *
     * @return
     */
    @GetMapping("/deviceOnline")
    @ApiOperationSupport(order = 20)
    @ApiOperation(value = "根据项目编码查询设备在线统计", notes = "根据项目编码查询设备在线统计")
    public R<List<DeviceOnlineInfoDTO>> listDeviceOnlineInfo(@RequestParam(name = "adcode", required = true) String adcode,
                                                             @RequestParam(name = "projectCode", required = false) String projectCode) {
        if (StringUtils.isEmpty(adcode) && StringUtils.isEmpty(projectCode)) {
            throw new ServiceException("项目编码");
        }
        return R.data(areaStatisticsService.listDeviceOnlineInfo(adcode, projectCode));

    }

}
