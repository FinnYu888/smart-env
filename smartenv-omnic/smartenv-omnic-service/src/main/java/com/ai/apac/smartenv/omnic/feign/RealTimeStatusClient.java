package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.service.RealPicStatusService;
import com.ai.apac.smartenv.omnic.service.RealStatusService;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealTimeStatusClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/17  15:32    panfeng          v1.0.0             修改原因
 */
//@ApiIgnore
@RestController
@RequiredArgsConstructor
public class RealTimeStatusClient implements IRealTimeStatusClient{

    @Autowired
    private RealStatusService realStatusService;
    @Autowired
    private RealPicStatusService realPicStatusService;


    private IDeviceClient deviceClient;





    /**
     * 根据状态查询所有车辆
     * @param status
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_VEHICLE_STATUS)
    public R<List<OmnicVehicleInfo>> getVehicleByStatus(@RequestParam Integer status,@RequestParam String tenantId){
        if (status==null){
            R.fail("状态不能为空");
        }
        return R.data(realStatusService.getVehicleInfoByStatus(status,tenantId));
    }

    /**
     * 根据车辆ID获取图片状态
     * @param vehicleId
     * @return
     */
    @Override
    @GetMapping(GET_PIC_STATUS)
    public R<PicStatus> getPicStatusByVehicleId(@RequestParam String vehicleId){
        PicStatus picStatus = realPicStatusService.selectVehiclePicStatusById(vehicleId);
        if (picStatus==null){
            picStatus=new PicStatus();
            picStatus.setEntityId(vehicleId);
            picStatus.setPicStatus(2);
        }

        return R.data(picStatus);
    }



    /**
     * 获取车辆监控各个状态的数量
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_ALL_STATUS_COUNT)
    public R<StatusCount> getAllVehicleStatusCount(@RequestParam String tenantId){
        StatusCount statusCount = realStatusService.selectAllVehicleDeviceStatusCount(tenantId);
        if (statusCount==null){
            return R.data(statusCount);

        }
        if (statusCount!=null&&statusCount.getWorking()==null){
            statusCount.setWorking(0L);
        }

        if (statusCount!=null&&statusCount.getAlarm()==null){
            statusCount.setAlarm(0L);
        }
        if (statusCount!=null&&statusCount.getDeparture()==null){
            statusCount.setDeparture(0L);
        }
        if (statusCount!=null&&statusCount.getSitBack()==null){
            statusCount.setSitBack(0L);
        }



        return R.data(statusCount);
    }


//---------------------------------------------------人员实时状态-----------------------------------------------------

    /**
     * 根据状态查询所有人员
     * @param status
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_STATUS)
    public R<List<OmnicPersonInfo>> getPersonByStatus(@RequestParam Integer status, @RequestParam String tenantId){
        if (status==null){
            R.fail("状态不能为空");
        }
        return R.data(realStatusService.getPersonInfoByStatus(status,tenantId));
    }

    /**
     * 根据人员ID获取图片状态
     * @param personId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_PIC_STATUS)
    public R<PicStatus> getPicStatusByPersonId(@RequestParam String personId){
        PicStatus picStatus = realPicStatusService.selectPersonPicStatusById(personId);
        if (picStatus==null){
            picStatus=new PicStatus();
            picStatus.setEntityId(personId);
            picStatus.setPicStatus(2);
        }

        return R.data(picStatus);
    }

    /**
     * 人员监控的四个数字
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_ALL_PERSON_STATUS_COUNT)
    public R<StatusCount> getAllPersonStatusCount(@RequestParam String tenantId){
        StatusCount statusCount = realStatusService.selectAllPersonDeviceStatusCount(tenantId.toString());
        if (statusCount==null){
            return R.data(statusCount);

        }
        if (statusCount!=null&&statusCount.getWorking()==null){
            statusCount.setWorking(0L);
        }

        if (statusCount!=null&&statusCount.getAlarm()==null){
            statusCount.setAlarm(0L);
        }
        if (statusCount!=null&&statusCount.getDeparture()==null){
            statusCount.setDeparture(0L);
        }
        if (statusCount!=null&&statusCount.getSitBack()==null){
            statusCount.setSitBack(0L);
        }


        return R.data(statusCount);
    }


}
