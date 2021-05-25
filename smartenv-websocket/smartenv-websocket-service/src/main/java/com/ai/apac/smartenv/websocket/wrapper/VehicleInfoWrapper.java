package com.ai.apac.smartenv.websocket.wrapper;

import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import org.springblade.core.tool.utils.BeanUtil;

import static com.ai.apac.smartenv.common.constant.VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM;

/**
 * @author qianlong
 * @description 视图包装类
 * @Date 2020/3/5 2:58 下午
 **/
public class VehicleInfoWrapper {

    public static VehicleInfoWrapper build() {
        return new VehicleInfoWrapper();
    }

    public VehicleInfoVO entityVO(VehicleInfo vehicleInfo, DeviceInfo deviceInfo, PicStatus picStatus) {
        if (vehicleInfo == null || deviceInfo == null) {
            return null;
        }
        VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
        BeanUtil.copy(vehicleInfo, vehicleInfoVO);

        vehicleInfoVO.setDeviceId(String.valueOf(deviceInfo.getId()));
        vehicleInfoVO.setDeviceCode(deviceInfo.getDeviceCode());
        vehicleInfoVO.setVehicleId(String.valueOf(vehicleInfo.getId()));
        if(picStatus != null){
            vehicleInfoVO.setStatus(picStatus.getPicStatus());
            vehicleInfoVO.setStatusName(VehicleStatusEnum.getDescByValue(vehicleInfoVO.getStatus()));
        }
        vehicleInfoVO.setShowFlag(false);
        String statusImg = VehicleCache.getVehicleStatusImg(picStatus.getPicStatus());
        vehicleInfoVO.setIcon(statusImg);
        return vehicleInfoVO;
    }
}
