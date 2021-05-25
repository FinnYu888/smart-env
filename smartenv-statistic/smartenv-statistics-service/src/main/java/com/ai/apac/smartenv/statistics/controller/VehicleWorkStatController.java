package com.ai.apac.smartenv.statistics.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.statistics.service.IProjectService;
import com.ai.apac.smartenv.statistics.service.IVehicleWorkStatService;
import com.ai.apac.smartenv.statistics.vo.VehicleWorkStatVO;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/11 3:38 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/bi/vehicle")
@Api(value = "车辆作业情况分析", tags = "车辆作业情况分析")
@Slf4j
public class VehicleWorkStatController {

    private IVehicleWorkStatService vehicleWorkStatService;

    private IProjectService projectService;

    /**
     * 查询指定项目的机扫完成率
     *
     * @return
     */
    @GetMapping("/vehicleWorkInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询指定项目的机扫完成率", notes = "查询指定项目的机扫完成率")
    public R<VehicleWorkStatVO> getVehicleWorkInfo(@RequestParam(value = "projectCode", required = false) String projectCode, @RequestParam(value = "statDate", required = false) String statDate,
                                                   @RequestParam(value = "bladeAuth", required = false) String bladeAuth, BladeUser bladeUser) {
        if (StringUtils.isEmpty(statDate)) {
            statDate = DateUtil.today();
        }
        if (StringUtils.isNotEmpty(projectCode)) {
            return R.data(vehicleWorkStatService.getVehicleWorkStatVO(projectCode, statDate));
        } else if (StringUtils.isNotEmpty(bladeAuth) || bladeUser != null) {
            Claims claims = JwtUtil.parseJWT(bladeAuth);
            if (claims == null) {
                throw new ServiceException("请重新登录");
            } else {
                String userId = claims.get("user_id", String.class);
                List<String> projectCodeList = projectService.listProjectCodeByAccountId(Long.valueOf(userId));
                projectCode = CollUtil.join(projectCodeList, ",");
                return R.data(vehicleWorkStatService.getVehicleWorkStatVO(projectCode, statDate));
            }
        } else if (StringUtils.isEmpty(projectCode) && bladeUser == null) {
            throw new ServiceException("请选择项目");
        } else {
            Long userId = bladeUser.getUserId();
            List<String> projectCodeList = projectService.listProjectCodeByAccountId(userId);
            if (CollUtil.isNotEmpty(projectCodeList)) {
                projectCode = CollUtil.join(projectCodeList, ",");
            } else {
                throw new ServiceException("请选择项目");
            }
            return R.data(vehicleWorkStatService.getVehicleWorkStatVO(projectCode, statDate));
        }
    }

    @PostMapping("/mockData")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "生成指定项目的Mock数据", notes = "生成指定项目的Mock数据")
    public R genMockData(String projectCode, String statData) {
        vehicleWorkStatService.genMockData(projectCode, statData);
        return R.status(true);
    }

    @PostMapping("/statVehicleWork")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "统计项目下指定时间的作业完成率", notes = "统计项目下指定时间的作业完成率")
    public R statVehicleWork(@RequestParam String startTime, @RequestParam String endTime,@RequestParam String statDate, @RequestParam String projectCodeList) {
        List<String> projectCodeList_ = Arrays.asList(projectCodeList.split(","));
        vehicleWorkStatService.removeVehicleWorkStat(startTime, endTime, statDate,projectCodeList_);
        vehicleWorkStatService.vehicleWorkStatRun(startTime, endTime,statDate,projectCodeList_);
        return R.status(true);
    }

}
