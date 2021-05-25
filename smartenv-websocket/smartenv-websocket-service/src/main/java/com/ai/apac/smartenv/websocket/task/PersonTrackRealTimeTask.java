package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.PersonController;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonTrackRealTimeVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.wrapper.PersonInfoWrapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @description 当天人员轨迹实时推送
 * @Date 2020/2/18 21:28 下午
 **/
@Getter
@Setter
@Slf4j
public class PersonTrackRealTimeTask extends BaseTask implements Runnable {

    public PersonTrackRealTimeTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                //TODO 先简单实现,每30秒执行一次,以后再用定时任务来实现
//                Thread.sleep(10000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程[{}]中断:", "PersonTrackRealTimeTask", e.getMessage());
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
    protected R<PersonTrackRealTimeVO> execute() {
        R<PersonTrackRealTimeVO> result = null;
        try {
            String sessionId = getWebsocketTask().getSessionId();
            Map<String, Object> params = validParams();
            String personId = params.get("personId") == null ? null : (String) params.get("personId");

            if (StringUtils.isBlank(personId)) {
                throw new ServiceException("The param should not be empty!");
            }
            //为了保证轨迹一定在实时位置之后,先关闭之前的查询实时位置任务
            PersonTrackRealTimeVO personTrackRealTimeVO = new PersonTrackRealTimeVO();
            //根据人员获取绑定的手表设备
            Future<DeviceInfo> deviceInfoResult = getDeviceService().getDeviceByPerson(personId);
            if (deviceInfoResult == null || deviceInfoResult.get() == null) {
                result = R.data(null, "该人员没有绑定手表");
                return result;
            }
            DeviceInfo deviceInfo = deviceInfoResult.get();
            //调用大数据接口获取实时轨迹
            String deviceCode = deviceInfo.getDeviceCode();
//            deviceCode = "SS000001";
            Future<List<PositionDTO>> positionListResult = getDeviceService().getDeviceTrackRealTime(deviceCode);
            if (positionListResult == null || positionListResult.get() == null || positionListResult.get().size() == 0) {
                result = R.data(null, "没有符合条件的人员位置信息");
                return result;
            }
            List<PositionDTO> positionList = positionListResult.get();

            //然后再向前端推送实时轨迹
            personTrackRealTimeVO.setPositionList(positionList);
            personTrackRealTimeVO.setTopicName(getWebsocketTask().getTopic());
            personTrackRealTimeVO.setActionName(getWebsocketTask().getTaskType());
            personTrackRealTimeVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(personTrackRealTimeVO, "查询人员实时轨迹成功");

            /**
             * 将人员ID存入Redis,便于指定推送策略
             */
            getWebSocketTaskService().createEntityTask(this.getWebsocketTask(),personId);


        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }

}
