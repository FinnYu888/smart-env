package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.service.ISyncService;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.feign.IDeptClient;
import com.ai.apac.smartenv.system.feign.IRegionClient;
import com.ai.apac.smartenv.system.feign.IStationClient;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SyncServiceImpl implements ISyncService {

    private IPersonClient personClient;

    private IVehicleClient vehicleClient;

    private IFacilityClient facilityClient;

    private IWorkareaClient workareaClient;

    private IRegionClient regionClient;

    private IDeviceClient deviceClient;

    private IDeptClient  deptClient;

    private IStationClient stationClient;


    @Override
    @Async
    public void syncInfo(List<Object> datas,String optType,String actionType,String tenantId) {
        if (datas == null || datas.isEmpty()) {
            throw new ServiceException("Execl内容为空,请重新上传");
        }
        List<ThirdSyncImportResultModel> resultModel = new ArrayList<ThirdSyncImportResultModel>();

        List<List<String>> datasList = new ArrayList<List<String>>();
        for (Object object : datas) {
            List<String> params = new ArrayList<>();
            for (Object o : (List<?>) object) {
                params.add(String.class.cast(o));
            }
            datasList.add(params);
        }

        if(OmnicConstant.THIRD_INFO_TYPE.PERSON.equals(optType)){
            //人
            personClient.personInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.VEHICLE.equals(optType)){
            //车
            vehicleClient.vehicleInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.FACILITY.equals(optType)){
            //中转站
            facilityClient.facilityInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.WORKAREA.equals(optType)){
            //工作线路/区域
            workareaClient.workareaInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.AREA.equals(optType)){
            //行政/业务区域
            regionClient.regionInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.DEVICE.equals(optType)){
            //设备
            deviceClient.deviceInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.DEPT.equals(optType)){
            //部门
            deptClient.deptInfoAsync(datasList,tenantId,actionType);
        }else if(OmnicConstant.THIRD_INFO_TYPE.STATION.equals(optType)){
            //岗位
            stationClient.stationInfoAsync(datasList,tenantId,actionType);
        }
    }

}
