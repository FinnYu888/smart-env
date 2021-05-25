package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
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

/**
 * @author qianlong
 * @description 车辆信息实时推送，对应车辆监控打开的弹框
 * @Date 2020/2/18 16:28 下午
 **/
@Getter
@Setter
@Slf4j
public class VehicleDetailTask extends BaseTask implements Runnable {

    public VehicleDetailTask(WebsocketTask websocketTask) {
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
    protected R<VehicleDetailVO> execute() {
        R<VehicleDetailVO> result = null;
        try {
            Map<String, Object> params = validParams();
            String vehicleId = params.get("vehicleId") == null ? null : (String) params.get("vehicleId");
            BasicVehicleInfoDTO basicVehicleInfoDto = params.get("basicVehicleInfoDto") == null ? null : (BasicVehicleInfoDTO) params.get("basicVehicleInfoDto");
            if (StringUtils.isBlank(vehicleId)&&basicVehicleInfoDto==null) {
                throw new ServiceException("The param should not be empty!");
            }

            BaiduMapUtils.CoordsSystem coordsSystem= BaiduMapUtils.CoordsSystem.BD09LL;
            VehicleDetailVO vehicleTrackVO=null;
            if (basicVehicleInfoDto!=null){
                vehicleTrackVO=BeanUtil.copy(basicVehicleInfoDto,VehicleDetailVO.class);
            }else {
                vehicleTrackVO = getVehicleService().getVehicleDetailRealTime(Long.valueOf(vehicleId), getTenantId(), coordsSystem);
            }



            vehicleTrackVO.setId(vehicleId);
            if (vehicleTrackVO == null) {
                return R.data(null,"没有匹配的数据");
            }


            /**
             * 将车辆ID存入Redis
             */
            getWebSocketTaskService().createEntityTask(this.getWebsocketTask(), vehicleId);

            vehicleTrackVO.setTopicName(getWebsocketTask().getTopic());
            vehicleTrackVO.setActionName(getWebsocketTask().getTaskType());
            vehicleTrackVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(vehicleTrackVO);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
