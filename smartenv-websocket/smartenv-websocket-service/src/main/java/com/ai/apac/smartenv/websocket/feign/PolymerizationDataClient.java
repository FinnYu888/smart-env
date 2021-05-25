package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.feign.IAshcanClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.facility.feign.IToiletClient;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.service.IAlarmService;
import com.ai.apac.smartenv.websocket.service.IEventService;
import com.ai.apac.smartenv.websocket.service.IPolymerizationService;
import com.ai.apac.smartenv.websocket.service.ITaskService;
import com.ai.smartenv.cache.util.SmartCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020/9/22 Asiainfo
 *
 * @ClassName: PolymerizationDataClient
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/9/22
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/22  16:54    zhanglei25          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@Slf4j
public class PolymerizationDataClient implements IPolymerizationDataClient{

    private IPolymerizationService polymerizationService;

    private ITaskService taskService;


    @Override
    public R<Boolean> updatePolymerizationCountRedis(String tenantId,String entityType) {
//        polymerizationService.updatePolymerizationCountRedis(tenantId,entityType);
//        taskService.wakePolymerizationAlarmAmountUpdateTask(tenantId);
        return R.data(true);
    }
}
