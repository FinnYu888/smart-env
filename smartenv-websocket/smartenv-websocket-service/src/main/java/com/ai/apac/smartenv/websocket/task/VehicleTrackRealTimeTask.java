package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.VehicleController;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleMonitorVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleTrackRealTimeVO;
import com.ai.apac.smartenv.websocket.wrapper.VehicleInfoWrapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @description 当天车辆轨迹实时推送
 * @Date 2020/2/18 21:28 下午
 **/
@Getter
@Setter
@Slf4j
public class VehicleTrackRealTimeTask extends BaseTask implements Runnable {

    public VehicleTrackRealTimeTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                runTask();
//                //TODO 先简单实现,每30秒执行一次,以后再用定时任务来实现
//                Thread.sleep(30000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程[{}]中断:", "VehicleTrackRealTimeTask", e.getMessage());
//            return;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            log.error("当天车辆轨迹实时推送异常:", ex.getMessage());
//            return;
//        }
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    protected R<VehicleTrackRealTimeVO> execute() {
        R<VehicleTrackRealTimeVO> result = null;
        try {
            String sessionId = getWebsocketTask().getSessionId();
            Map<String, Object> params = validParams();
            String vehicleId = params.get("vehicleId") == null ? null : (String) params.get("vehicleId");

            if (StringUtils.isBlank(vehicleId)) {
                throw new ServiceException("The param should not be empty!");
            }
            //为了保证轨迹一定在实时位置之后,先关闭之前的查询实时位置任务
//            String userId = getWebsocketTask().getUserId();
//            getWebSocketTaskService().deleteSameTask(userId, sessionId, VehicleController.GET_VEHICLE_POSITION);

            VehicleTrackRealTimeVO vehicleTrackVO = new VehicleTrackRealTimeVO();
            //根据车辆获取绑定的位置跟踪设备
            Future<DeviceInfo> deviceInfoResult = getDeviceService().getPositionDeviceByVehicle(vehicleId);
            if (deviceInfoResult == null || deviceInfoResult.get() == null) {
                result = R.data(null, "该车辆没有绑定位置跟踪设备");
                return result;
            }
            DeviceInfo deviceInfo = deviceInfoResult.get();
            //调用大数据接口获取实时轨迹
            String deviceCode = deviceInfo.getDeviceCode();
//            deviceCode = "SS000001";
            Future<List<PositionDTO>> positionListResult = getDeviceService().getDeviceTrackRealTime(deviceCode);
            if (positionListResult == null || positionListResult.get() == null || positionListResult.get().size() == 0) {
                result = R.data(null, "没有符合条件的车辆位置信息");
                return result;
            }
            List<PositionDTO> positionList = positionListResult.get();


            log.debug("================推送车辆轨迹================");
            vehicleTrackVO.setPositionList(positionList);
            vehicleTrackVO.setTopicName(getWebsocketTask().getTopic());
            vehicleTrackVO.setActionName(getWebsocketTask().getTaskType());
            vehicleTrackVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(vehicleTrackVO);


            /**
             * 将车辆ID存入Redis
             */
            getWebSocketTaskService().createEntityTask(this.getWebsocketTask(), vehicleId);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
