package com.ai.apac.smartenv.device.controller;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.device.vo.GreenDeviceVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName GreenDeviceController
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/27 10:17
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/greenDevice")
@Api(value = "记录绿化设备信息", tags = "记录绿化设备信息接口")
public class GreenDeviceController {

    private IDeviceInfoService deviceInfoService;

    private IEntityCategoryClient entityCategoryClient;

    private IDeviceRelService deviceRelService;

    @GetMapping("/page")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "绿化设备分页展现", notes = "绿化设备分页展现")
    @ApiLog(value = "绿化设备分页展现")
    public R<IPage<GreenDeviceVO>> page(DeviceInfo deviceInfo, Query query,
                                        @RequestParam(name = "tag", required = false) String tag,
                                        @RequestParam(name = "simCode", required = false) String simCode) {
        IPage<DeviceInfo> pages = deviceInfoService.pageDevices(deviceInfo, query,tag,simCode);
        List<DeviceInfo> deviceInfoList = pages.getRecords();
        List<GreenDeviceVO> reDeviceInfoVOList = new ArrayList<GreenDeviceVO>();
        deviceInfoList.forEach(deviceInfo_ -> {
            GreenDeviceVO vehicleDeviceVO = Objects.requireNonNull(BeanUtil.copy(deviceInfo_, GreenDeviceVO.class));
            vehicleDeviceVO.setDeviceFactoryName(DictCache.getValue("device_manufacturer",deviceInfo_.getDeviceFactory()));
            vehicleDeviceVO.setEntityCategoryName(entityCategoryClient.getCategoryName(deviceInfo_.getEntityCategoryId()).getData());
            reDeviceInfoVOList.add(vehicleDeviceVO);
        });
        IPage<GreenDeviceVO> iPage = new Page<>(query.getCurrent(), query.getSize(), pages.getTotal());
        iPage.setRecords(reDeviceInfoVOList);
        return R.data(iPage);
    }


    /**
     * 更新车辆基本信息表
     */
    @PutMapping("")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新绿化终端位置", notes = "更新绿化终端位置")
    @Transactional(rollbackFor = Exception.class)
    @ApiLog(value = "更新绿化终端位置")
    public R put(@Valid @RequestBody GreenDeviceVO greenDeviceVO) throws IOException {

        DeviceInfo deviceInfo = deviceInfoService.getById(greenDeviceVO.getId());
        if(ObjectUtil.isEmpty(deviceInfo)){
            throw new ServiceException("终端不存在");
        }
        deviceInfo.setDeviceLocation(greenDeviceVO.getDeviceLocation());
        Boolean save = deviceInfoService.updateById(deviceInfo);
        return R.status(save);
    }
}
