package com.ai.apac.smartenv.facility.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.service.IToiletInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-09-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/toiletinfo/sync")
@Api(value = "第三方公厕信息同步接口", tags = "第三方公厕信息同步接口")
public class ToiletSyncController  extends BladeController {

    private IToiletInfoService toiletInfoService;


    /**
     * 新增 车辆,设备,物资等实体的分类信息
     */
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增公厕信息", notes = "传入Toilet信息")
    @ApiLog(value = "新增公厕信息")
    public R<Boolean> syncthirdToilet(@RequestBody ToiletInfo toiletInfo) {

        return R.status(toiletInfoService.thirdToiletInfoAsync(toiletInfo, OmnicConstant.ACTION_TYPE.NEW));
    }


    /**
     * 删除 车辆,设备,物资等实体的分类信息
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "逻辑删除车辆分类", notes = "传入ids")
    @ApiLog(value = "删除实体分类信息")
    public R<Boolean> delthirdToilet(@RequestBody ToiletInfo toiletInfo) {
        return R.status(toiletInfoService.thirdToiletInfoAsync(toiletInfo,OmnicConstant.ACTION_TYPE.DELETE));
    }

}
