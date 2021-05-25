package com.ai.apac.smartenv.job.scheduler.workstat.task;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import com.ai.apac.smartenv.statistics.feign.IStatisticsClient;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.mongodb.Mongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Slf4j
public class VehicleWorkStatRun implements Runnable{


    public String startTime; //1:00
    public String endTime;   //9:00
    public List<String> projectCodeList;

    @Override
    public void run() {
        log.info("VehicleWorkStatRun--------Start--------"+TimeUtil.getYYYYMMDDHHMMSS(new Date()));
        IStatisticsClient statisticsClient = SpringUtil.getBean(IStatisticsClient.class);
        statisticsClient.vehicleWorkStatRun(startTime,endTime,projectCodeList);

    }
}
