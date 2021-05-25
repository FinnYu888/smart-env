package com.ai.apac.smartenv.statistics.controller;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.statistics.dto.GetDeviceLocationDTO;
import com.ai.apac.smartenv.statistics.service.IDeviceLocationService;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationGroupVO;
import com.ai.apac.smartenv.statistics.vo.DeviceLocationDetailVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianlong
 * @description 查询设备定位
 * @Date 2021/1/6 1:28 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/easyv/location")
@Api(value = "查询设备定位", tags = "查询设备定位")
@Slf4j
public class DeviceLocationController {

    private IDeviceLocationService deviceLocationService;

    @GetMapping("/device")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询设备位置", notes = "查询设备位置")
    public R<DeviceLocationGroupVO> listDeviceLocation(GetDeviceLocationDTO getDeviceLocationDTO) {
        return R.data(deviceLocationService.listDeviceLocation(getDeviceLocationDTO));
    }

    /**
     * 获取设备定位详细信息
     *
     * @param deviceObjectId
     * @param deviceObjType
     * @param coordsSystem
     * @return
     */
    @GetMapping("/device/location/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询设备定位实时信息", notes = "查询设备定位实时信息")
    R<DeviceLocationDetailVO> getDeviceLocationDetail(@RequestParam(required = false) String groupId, @RequestParam(required = false) Long deviceObjectId,
                                                      @RequestParam(required = false) Integer deviceObjType, @RequestParam(defaultValue = "gcj02") String coordsSystem) {
        if (StringUtils.isNotEmpty(groupId) && groupId.indexOf("-") > 0){
            String[] idInfo = groupId.split("-");
            deviceObjectId = Long.valueOf(idInfo[0]);
            deviceObjType = Integer.valueOf(idInfo[1]);
        }else if(deviceObjectId == null || deviceObjType == null){
            throw new ServiceException("缺少参数");
        }
        return R.data(deviceLocationService.getDeviceLocationDetail(deviceObjectId, deviceObjType, coordsSystem));
    }
}
