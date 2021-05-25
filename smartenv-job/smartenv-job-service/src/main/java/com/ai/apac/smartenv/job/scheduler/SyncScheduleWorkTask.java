package com.ai.apac.smartenv.job.scheduler;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.dto.CommuterAndMileageDTO;
import com.ai.apac.smartenv.arrange.dto.CommuterAndMileageDTO.CommuterAndMileage;
import com.ai.apac.smartenv.arrange.dto.CommuterAndMileageDTO.Section;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.entity.ScheduleWork;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
@AllArgsConstructor
@Slf4j
public class SyncScheduleWorkTask {

	private IDeviceClient deviceClient;
	private IDeviceRelClient deviceRelClient;
	private IScheduleClient scheduleClient;
	
	/*
	 * 每月考勤放到缓存，每天1点执行
	 */
	//@Scheduled(cron = "0 10 1 * * ?")
	public void putScheduleObjectToCache() {
//		LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
		LocalDate firstDay = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1);
		for (int i = 0; i < 31; i++) {
			HashMap<Long, List<ScheduleObject>> scheduleObjectMap = new HashMap<>();
			List<ScheduleObject> scheduleObjectList = scheduleClient.listAllScheduleObjectByDate(firstDay.plusDays(i)).getData();
			if (scheduleObjectList != null) {
				scheduleObjectList.forEach(scheduleObject -> {
					Long entityId = scheduleObject.getEntityId();
					List<ScheduleObject> list = scheduleObjectMap.get(entityId);
					if (list == null) {
						list = new ArrayList<>();
					}
					list.add(scheduleObject);
					scheduleObjectMap.put(entityId, list);
				});
			}
			for (Entry<Long, List<ScheduleObject>> scheduleObjectEntry : scheduleObjectMap.entrySet()) {
				ScheduleCache.saveOrUpdateScheduleObject(scheduleObjectEntry.getValue());
			}
		}
	}
	/*
	 * 同步每天上下班时间，每天6点执行
	 */
    @Scheduled(cron = "0 0 6 * * ?")
    public void syncScheduleWork() throws IOException {
    	log.error("syncScheduleWork开始");
    	LocalDateTime now = LocalDateTime.now().minusDays(1);
    	String date = DateUtil.format(now, DatePattern.PURE_DATE_PATTERN);
    	Map<String, Object> params = new HashMap<>();
		params.put("date", date);
		// 调用大数据
		CommuterAndMileageDTO commuterAndMileage = BigDataHttpClient.getBigDataBodyToObjcet(
				BigDataHttpClient.syncCommuterTimeAndMileage, params, CommuterAndMileageDTO.class);
		List<CommuterAndMileage> dataList = commuterAndMileage.getData();
		log.error("syncScheduleWork大数据条数：" + dataList.size());
		for (CommuterAndMileage data : dataList) {
			try {
				String deviceCode = data.getDeviceCode();
				List<DeviceInfo> deviceInfoList = deviceClient.getDeviceByCode(deviceCode).getData();
				if (deviceInfoList != null && !deviceInfoList.isEmpty()) {
					// 一个设备只绑定一个实体
					DeviceInfo deviceInfo = deviceInfoList.get(0);
					Long deviceId = deviceInfo.getId();
					DeviceRel deviceRel = deviceRelClient.getDeviceRelByDeviceId(deviceId).getData();
					List<Section> sections = data.getSections();
					if (sections != null) {
						for (Section section : sections) {
							ScheduleWork scheduleWork = new ScheduleWork();
							scheduleWork.setWorkDate(now.toLocalDate());
							scheduleWork.setEntityId(deviceRel.getEntityId());
							if (StringUtils.isNotBlank(section.getFirstStart())) {
								if (section.getFirstStart().length() == 14) {
									scheduleWork.setWorkBeginTime(LocalDateTime.parse(section.getFirstStart(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toLocalTime());
								} else if (section.getFirstStart().length() == 19) {
									scheduleWork.setWorkBeginTime(LocalDateTime.parse(section.getFirstStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalTime());
								}
							}
							if (StringUtils.isNotBlank(section.getSecondOff())) {
								if (section.getSecondOff().length() == 14) {
									scheduleWork.setWorkEndTime(LocalDateTime.parse(section.getSecondOff(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toLocalTime());
								} else if (section.getSecondOff().length() == 19) {
									scheduleWork.setWorkEndTime(LocalDateTime.parse(section.getSecondOff(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalTime());
								}
							}
							scheduleWork.setWorkMileage(new BigDecimal(data.getMileage()));
							if (String.valueOf(CommonConstant.ENTITY_TYPE.PERSON).equals(deviceRel.getEntityType())) {
								scheduleWork.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
							} else if (String.valueOf(CommonConstant.ENTITY_TYPE.VEHICLE).equals(deviceRel.getEntityType())) {
								scheduleWork.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
							}
							scheduleClient.saveScheduleWork(scheduleWork);
						}
					}
				}
			} catch (Exception e) {
				log.error("syncScheduleWork错误：" + e.getMessage());
				// TODO: handle exception
			}
		}
		log.error("syncScheduleWork结束");
    }
}
