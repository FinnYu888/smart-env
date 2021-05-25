package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description 车辆实时位置跟踪
 * @Date 2020/2/16 10:28 下午
 **/
@Getter
@Setter
@Slf4j
public class VehiclePositionTask extends BaseTask implements Runnable {

    public VehiclePositionTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    public R<VehicleMonitorVO> execute() {
        VehicleMonitorVO vehicleMonitorVO = new VehicleMonitorVO();
        vehicleMonitorVO.setTopicName(getWebsocketTask().getTopic());
        vehicleMonitorVO.setActionName(getWebsocketTask().getTaskType());
        vehicleMonitorVO.setTaskId(String.valueOf(getWebsocketTask().getId()));


        R<VehicleMonitorVO> result = null;
        try {
            // #region 以下代码移动到service中
            Map<String, Object> params = validParams();
            String vehicleIds = params.get("vehicleIds") == null ? null : (String) params.get("vehicleIds");
            Integer status = params.get("status") == null ? null : (Integer) params.get("status");

            String tenantIdStr = params.get("tenantId") == null ? null : (String) params.get("tenantId");
            Boolean isBigScreen = params.get("isBigScreen") == null ? false : (boolean) params.get("isBigScreen");

            Boolean isEasyV = params.get("isEasyV") == null ? false : (boolean) params.get("isEasyV");

            String regionId = params.get("regionId") == null ? null : (String) params.get("regionId");
            String categoryId = params.get("categoryId") == null ? null : (String) params.get("categoryId");
            Integer coord = (Integer) params.get("coordsSystem");
            BaiduMapUtils.CoordsSystem coordsSystem = coord == null ? BaiduMapUtils.CoordsSystem.BD09LL : BaiduMapUtils.CoordsSystem.getCoordsSystem(coord);

            String message = null;
            List<String> vehicleIdList = null;
            if (StringUtils.isBlank(vehicleIds) && status == null && StringUtil.isEmpty(tenantIdStr) && regionId == null && categoryId == null) {
                throw new ServiceException("The param should not be empty!");
            } else if (StringUtils.isNotBlank(vehicleIds)) {//根据车辆ID查询
                message = "查询车辆ID查询实时位置成功";
                vehicleIdList = Func.toStrList(vehicleIds);
            } else if (StringUtil.isNotBlank(tenantIdStr) && isBigScreen && isEasyV) {
                message = "查询车辆ID查询实时位置成功";
                List<String> tenantIds = Func.toStrList(tenantIdStr);
                Future<List<String>> vehicleEasyVList = getVehicleService().getVehicleEasyVList(tenantIds);
                if (vehicleEasyVList != null || vehicleEasyVList.get() != null) {
                    vehicleIdList = vehicleEasyVList.get();
                }
            } else if (StringUtil.isNotBlank(tenantIdStr) && isBigScreen) {
                message = "查询车辆ID查询实时位置成功";
                vehicleIdList = new ArrayList<>();
                List<String> tenantIds = Func.toStrList(tenantIdStr);
                for (String tenantId : tenantIds) {
                    Future<List<String>> personByWorkareaIdsAndStatus = getVehicleService().getVehicleByWorkareaIdsAndStatus(tenantId);
                    if (personByWorkareaIdsAndStatus.get() != null) {
                        vehicleIdList = personByWorkareaIdsAndStatus.get();
                    }

                }
            } else if (status != null) {//根据状态查询
                Future<List<OmnicVehicleInfo>> dataResult = getVehicleService().getVehicleByStatus(status, getTenantId());
                if (dataResult != null && dataResult.get() != null) {
                    List<OmnicVehicleInfo> vehicleInfoList = dataResult.get();
                    if (vehicleInfoList.size() > 0) {
                        vehicleIdList = new ArrayList<String>();
                        for (OmnicVehicleInfo vehicleInfo : vehicleInfoList) {
                            vehicleIdList.add(String.valueOf(vehicleInfo.getId()));
                        }
                    }
                }
                message = "查询车辆状态查询实时位置成功";
            } else if (StringUtil.isNotBlank(regionId)) {
                List<VehicleInfo> vehicleInfoList = getVehicleService().getVehicleInfoByRegionId(StringUtil.isNotBlank(regionId) ? Long.parseLong(regionId) : null);

                if (CollectionUtil.isNotEmpty(vehicleInfoList)) {
                    vehicleIdList = vehicleInfoList.stream().map(vehicleInfo -> vehicleInfo.getId().toString()).collect(Collectors.toList());
                }

            }
            //# region End

            if (vehicleIdList == null || vehicleIdList.size() == 0) {
                result = R.data(null, "没有符合条件的车辆");
                return result;
            }
            List<Long> allIdList = vehicleIdList.stream().map(Long::parseLong).collect(Collectors.toList());
            List<VehicleMonitorInfoVO> vehicleMonitorInfoVOList = getVehicleService().getVehicleMonitorInfo(allIdList, coordsSystem);

            for (String vehicleId : vehicleIdList) {
                getWebSocketTaskService().createEntityTask(this.getWebsocketTask(), vehicleId);
            }

            if (isEasyV) {
                getWebSocketTaskService().createEasyVTask(getWebsocketTask());
            }

            if (vehicleMonitorInfoVOList.size() == 0) {
                result = R.data(null, "没有符合条件的车辆");
                return result;
            }
            log.debug("================推送车辆实时位置================");
            vehicleMonitorVO.setVehicleList(vehicleMonitorInfoVOList);

            result = R.data(vehicleMonitorVO);
            result.setMsg(message);
        } catch (Exception ex) {
            result = R.data(null);
            result.setCode(ResultCode.FAILURE.getCode());
            result.setMsg(ResultCode.FAILURE.getMessage());
            return result;
//            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }

}
