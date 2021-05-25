package com.ai.apac.smartenv.job.scheduler.statistics;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.statistics.dto.VehicleWorkSynthInfoDTO;
import com.ai.apac.smartenv.statistics.feign.IStatisticsClient;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.workarea.cache.WorkareaCache;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class VehicleStayTask {

	private IStatisticsClient statisticsClient;

	private IScheduleClient scheduleClient;

	private ISysClient sysClient;

	private MongoTemplate mongoTemplate;

	private IWorkareaRelClient workareaRelClient;

	/*
	 */
//	@Scheduled(cron = "0 0 1 * * ?")
	public void syncVehicleStay() {
		LocalDateTime now = LocalDateTime.now().minusDays(1);
    	String date = DateUtil.format(now, DatePattern.NORM_DATE_PATTERN);
		statisticsClient.syncVehicleStay(date);
	}



	/**
	 * 实时同步沧州车辆的实际里程。每1小时同步一次3600000
	 */
	//@Scheduled(fixedDelay = 3600000)
	public void vehicleWorkSynthInfo() {
		//0.查询所有的租户
		List<Tenant> tenantList = sysClient.getAllTenant().getData();
		if(ObjectUtil.isNotEmpty(tenantList) && tenantList.size() > 0){
			for(Tenant tenant:tenantList){
				String tenantId = tenant.getTenantId();
				//String tenantId = "969149";
				//1.查询某一租户下今日有工作排班的车辆
				List<ScheduleObject> scheduleObjects = scheduleClient.listVehicleForToday(tenantId).getData();
				if(ObjectUtil.isNotEmpty(scheduleObjects) && scheduleObjects.size() > 0){
					//1.1 今天排班的车辆ID
					Set<Long> vehicleIdSet = scheduleObjects.stream().map(ScheduleObject::getEntityId).collect(Collectors.toSet());

					//2.查询实时车辆工作里程
					JSONObject param = new JSONObject();
					param.put("tenantId", tenantId);
					param.put("beginTime", TimeUtil.getNoLineYYYYMMDDHHMISS(TimeUtil.getStartTime(new Date())));
					param.put("endTime", TimeUtil.getNoLineYYYYMMDDHHMISS(new Date()));
					Map<String,String> vehicleIdMap = new HashMap<String,String>();
					try {
						String res = BigDataHttpClient.postDataToBigData("/smartenv-api/sync/vehicleWorkDistance", param.toString());
						JSONObject resObj = JSONUtil.parseObj(res);
						if(resObj.getInt("code") == 0){
							JSONObject data = resObj.getJSONObject("data");
							if(ObjectUtil.isNotEmpty(data)){
								JSONArray distances = data.getJSONArray("distances");
								if(ObjectUtil.isNotEmpty(distances) && distances.size()>0){
									distances.jsonIter().forEach(object -> {
											String deviceCode = object.getStr("deviceid");
											String distance = object.getStr("distance");
											DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(tenantId,deviceCode);
											if(ObjectUtil.isNotEmpty(deviceInfo) && ObjectUtil.isNotEmpty(deviceInfo.getId())){
												DeviceRel deviceRel = DeviceRelCache.getDeviceRel(deviceInfo.getId());
												if(ObjectUtil.isNotEmpty(deviceRel) && ObjectUtil.isNotEmpty(deviceRel.getEntityId())) {
												//2.1 得到车辆ID和实时工作里程的MAP
												vehicleIdMap.put(deviceRel.getEntityId().toString(), distance);
												}
											}
									});
								}
							}
						}
						//3.循环今天排班的车辆ID,保存数据。
						for(Long vehicleId:vehicleIdSet){
							Query query = new Query();
							query.addCriteria(Criteria.where("vehicleId").is(vehicleId.toString()));
							query.addCriteria(Criteria.where("workDate").is(TimeUtil.getNoLineYYYYMMDD(new Date())));
							VehicleWorkSynthInfoDTO vehicleWorkSynthInfoDTO = mongoTemplate.findOne(query,VehicleWorkSynthInfoDTO.class);
							String distance = vehicleIdMap.get(vehicleId.toString())==null?"0":vehicleIdMap.get(vehicleId.toString());//获取到大数据侧的车辆实时里程
							if(ObjectUtil.isNotEmpty(vehicleWorkSynthInfoDTO)){
								//今日作业实时完成率更新
								Update update = new Update();
								Double distanceArea =Double.parseDouble(distance)*Double.parseDouble(vehicleWorkSynthInfoDTO.getWorkareaWidth());
								Double operationRateValue = distanceArea/(Double.parseDouble(vehicleWorkSynthInfoDTO.getWorkareaLength())*Double.parseDouble(vehicleWorkSynthInfoDTO.getWorkareaWidth()));
								String operationRate = Double.toString(new BigDecimal(operationRateValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								update.set("operationRate", operationRate);
								update.set("distance",distance);
								update.set("distanceArea",distanceArea);
								update.set("updateTime", TimeUtil.getNoLineYYYYMMDDHHMISS(new Date()));
								mongoTemplate.findAndModify(query, update, vehicleWorkSynthInfoDTO.getClass());
							}else{
								//今日作业实时完成率第一次保存
								List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
								if(ObjectUtil.isNotEmpty(workareaRelList) && workareaRelList.size() > 0){
									WorkareaRel workareaRel = workareaRelList.get(0);
									if(ObjectUtil.isNotEmpty(workareaRel) && ObjectUtil.isNotEmpty(workareaRel.getWorkareaId())){
										//取到车辆的路线信息,只有当路线有长度和宽度才保存车辆实时作业完成率数据。
										WorkareaInfo workareaInfo = WorkareaCache.getWorkareaById(tenantId,workareaRel.getWorkareaId());
										if(ObjectUtil.isNotEmpty(workareaInfo) && ObjectUtil.isNotEmpty(workareaInfo.getLength()) && ObjectUtil.isNotEmpty(workareaInfo.getWidth())){
											Double distanceArea =Double.parseDouble(distance)*Double.parseDouble(workareaInfo.getWidth());
											Double operationRateValue = distanceArea/(Double.parseDouble(workareaInfo.getLength())*Double.parseDouble(workareaInfo.getWidth()));
											String operationRate = Double.toString(new BigDecimal(operationRateValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
											vehicleWorkSynthInfoDTO = new VehicleWorkSynthInfoDTO();
											vehicleWorkSynthInfoDTO.setUpdateTime(TimeUtil.getNoLineYYYYMMDDHHMISS(new Date()));
											vehicleWorkSynthInfoDTO.setOperationRate(operationRate);
											vehicleWorkSynthInfoDTO.setDistance(distance);
											vehicleWorkSynthInfoDTO.setDistanceArea(Double.toString(new BigDecimal(distanceArea).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
											vehicleWorkSynthInfoDTO.setWorkareaLength(workareaInfo.getLength());
											vehicleWorkSynthInfoDTO.setWorkareaWidth(workareaInfo.getWidth());
											vehicleWorkSynthInfoDTO.setProjectCode(tenantId);
											vehicleWorkSynthInfoDTO.setVehicleId(vehicleId.toString());
											vehicleWorkSynthInfoDTO.setWorkDate(TimeUtil.getNoLineYYYYMMDD(new Date()));
											mongoTemplate.save(vehicleWorkSynthInfoDTO);
										}
									}
								}
							}
						};
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}
}
