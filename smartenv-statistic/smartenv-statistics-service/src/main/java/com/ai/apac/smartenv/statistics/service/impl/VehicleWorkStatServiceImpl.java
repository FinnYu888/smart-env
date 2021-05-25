package com.ai.apac.smartenv.statistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.enums.WorkAreaLevelEnum;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import com.ai.apac.smartenv.statistics.entity.VehicleWorkStatResult;
import com.ai.apac.smartenv.statistics.feign.IStatisticsClient;
import com.ai.apac.smartenv.statistics.mapper.VehicleWorkStatResultMapper;
import com.ai.apac.smartenv.statistics.service.IVehicleDistanceInfoService;
import com.ai.apac.smartenv.statistics.service.IVehicleWorkStatService;
import com.ai.apac.smartenv.statistics.vo.RoadInfoVO;
import com.ai.apac.smartenv.statistics.vo.VehicleWorkStatVO;
import com.ai.apac.smartenv.statistics.vo.WorkCompleteRateVO;
import com.ai.apac.smartenv.statistics.vo.WorkInfoVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRoadInfoClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/11 3:44 下午
 **/
@Service
@Slf4j
@AllArgsConstructor
public class VehicleWorkStatServiceImpl extends BaseServiceImpl<VehicleWorkStatResultMapper, VehicleWorkStatResult> implements IVehicleWorkStatService {

    private MongoTemplate mongoTemplate;

    private IVehicleDistanceInfoService vehicleDistanceInfoService;

    private IWorkareaClient workareaClient;

    private IWorkareaRoadInfoClient workareaRoadInfoClient;


    private IWorkareaRelClient workareaRelClient;

    /**
     * 查询指定日期下车辆机扫率统计数据
     *
     * @param projectCode
     * @param statDate
     * @return
     */
    @Override
    public VehicleWorkStatVO getVehicleWorkStatVO(String projectCode, String statDate) {
        VehicleWorkStatVO workStatVO = new VehicleWorkStatVO();

        //先查询所有符合条件的数据
        List<String> projectCodeList = Func.toStrList(projectCode);
        List<VehicleWorkStatResult> dataList = baseMapper.selectList(new LambdaQueryWrapper<VehicleWorkStatResult>()
                .in(VehicleWorkStatResult::getTenantId, projectCodeList)
                .eq(VehicleWorkStatResult::getStatDate, DateUtil.parseDate(statDate))
                .orderByAsc(VehicleWorkStatResult::getWorkareaLevel).orderByAsc(VehicleWorkStatResult::getVehicleWorktype)
                .orderByAsc(VehicleWorkStatResult::getBeginTime));
        if (CollUtil.isNotEmpty(dataList)) {
            List<RoadInfoVO> roadInfoVOList = new ArrayList<RoadInfoVO>();

            //按道路级别进行分组
            Map<Integer, List<VehicleWorkStatResult>> vehicleWorkStatResultMap = dataList.stream().collect(Collectors.groupingBy(VehicleWorkStatResult::getWorkareaLevel));
            for (int i = 1; i <= 4; i++) {
                RoadInfoVO roadInfoVO = buildRoadInfo(i, vehicleWorkStatResultMap.get(i));
                if (roadInfoVO != null) {
                    roadInfoVOList.add(roadInfoVO);
                }
            }
            workStatVO.setStatDate(statDate);
            workStatVO.setRoadInfoList(roadInfoVOList);
            return workStatVO;
        } else {
            return workStatVO;
        }
    }

    /**
     * 构造返回数据
     *
     * @param workAreaLevel
     * @return
     */
    private RoadInfoVO buildRoadInfo(Integer workAreaLevel, List<VehicleWorkStatResult> vehicleWorkStatResultList) {
        if (CollUtil.isEmpty(vehicleWorkStatResultList)) {
            return null;
        }
        RoadInfoVO roadInfoVO = new RoadInfoVO();
        roadInfoVO.setRoadLevel(WorkAreaLevelEnum.getDescByValue(workAreaLevel));

        //单位是万平米
        roadInfoVO.setTotalArea(String.valueOf(vehicleWorkStatResultList.get(0).getWorkareaAcreage() / 10000));

        //根据作业类型进行分组
        Map<String, List<VehicleWorkStatResult>> workTypeMap = vehicleWorkStatResultList.stream().collect(Collectors.groupingBy(VehicleWorkStatResult::getVehicleWorktype));
        List<WorkInfoVO> workInfoList = workTypeMap.entrySet().stream().map(entry -> {
            String workType = entry.getKey();
            List<VehicleWorkStatResult> workStatList = entry.getValue();
            WorkInfoVO workInfoVO = new WorkInfoVO();
            workInfoVO.setRoadLevel(WorkAreaLevelEnum.getDescByValue(workAreaLevel));
            workInfoVO.setWorkType(workType);
            workInfoVO.setWorkTypeName(DictCache.getValue("vehicle_work_type", workType));
            workInfoVO.setWorkAreaAcreage(vehicleWorkStatResultList.get(0).getWorkareaAcreage());
            List<WorkCompleteRateVO> workCompleteRateList = workStatList.stream().map(vehicleWorkStatResult -> {
                WorkCompleteRateVO workCompleteRateVO = new WorkCompleteRateVO();
                workCompleteRateVO.setAreaUnit("万平米");
                String beginTimeStr = vehicleWorkStatResult.getBeginTime();
                if(beginTimeStr.length() > 10){
                    beginTimeStr = beginTimeStr.substring(11,16);
                }
                String endTimeStr = vehicleWorkStatResult.getEndTime();
                if(endTimeStr.length() > 10){
                    endTimeStr = endTimeStr.substring(11,16);
                }
                workCompleteRateVO.setWorkTimePeriod(beginTimeStr + StringPool.DASH + endTimeStr);
                Double completeRate = Double.valueOf(vehicleWorkStatResult.getRealWorkPerc()) * 100;
                workCompleteRateVO.setCompleteRate(NumberUtil.roundStr(completeRate, 2));
                workCompleteRateVO.setRealWorkAcreage(NumberUtil.roundStr(vehicleWorkStatResult.getRealWorkAcreage() / 10000, 2));
                workCompleteRateVO.setRoadLevel(WorkAreaLevelEnum.getDescByValue(workAreaLevel));
                workCompleteRateVO.setWorkType(workType);
                workCompleteRateVO.setWorkAreaAcreage(NumberUtil.roundStr(vehicleWorkStatResult.getWorkareaAcreage() / 10000, 2));
                workCompleteRateVO.setProjectCode(vehicleWorkStatResult.getTenantId());
                workCompleteRateVO.setProjectName(ProjectCache.getProjectNameByCode(vehicleWorkStatResult.getTenantId()));
                return workCompleteRateVO;
            }).collect(Collectors.toList());
            workInfoVO.setWorkCompleteRateList(workCompleteRateList);
            return workInfoVO;
        }).collect(Collectors.toList());

        roadInfoVO.setWorkInfoList(workInfoList);
        return roadInfoVO;
    }

    /**
     * 新增Mock数据
     *
     * @param projectCode
     */
    @Override
    public void genMockData(String projectCode, String statDate) {
        List<VehicleWorkStatResult> dataList = baseMapper.selectList(new LambdaQueryWrapper<VehicleWorkStatResult>().eq(VehicleWorkStatResult::getTenantId, projectCode).eq(VehicleWorkStatResult::getStatDate, DateUtil.parseDate(statDate)));
        List<Long> idList = dataList.stream().map(vehicleWorkStatResult -> {
            return vehicleWorkStatResult.getId();
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(idList)) {
            baseMapper.deleteBatchIds(idList);
        }

        List<VehicleWorkStatResult> list = new ArrayList<>();

        //一级道路数据
        //机扫作业
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "1", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 03:00:00", statDate + " 06:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "1", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 08:00:00",statDate + " 10:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "1", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 13:00:00", statDate + " 15:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "1", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 18:00:00", statDate + " 06:30:00", DateUtil.parseDate(statDate)));

        //洗扫作业
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "2", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 03:00:00", statDate + " 06:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "2", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)),statDate + " 14:00:00", statDate + " 17:30:00", DateUtil.parseDate(statDate)));

        //冲洗作业
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "3", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 03:00:00", statDate + " 06:30:00",DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 1, 250000.00, "3", 250000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 14:00:00", statDate + " 17:30:00", DateUtil.parseDate(statDate)));

        //二级道路数据
        //机扫作业
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "1", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 03:00:00", statDate + " 06:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "1", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 08:00:00", statDate + " 10:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "1", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 13:00:00", statDate + " 15:30:00", DateUtil.parseDate(statDate)));

        //洗扫作业
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "2", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 03:00:00", statDate + " 06:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "2", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 14:00:00", statDate + " 17:30:00", DateUtil.parseDate(statDate)));

        //冲洗作业
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "2", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 14:00:00", statDate + " 17:30:00", DateUtil.parseDate(statDate)));
        list.add(new VehicleWorkStatResult(null, 2, 300000.00, "3", 300000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)),statDate + " 14:00:00", statDate + " 17:30:00", DateUtil.parseDate(statDate)));


        //三级道路数据
        list.add(new VehicleWorkStatResult(null, 3, 800000.00, "1", 800000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 08:00:00", statDate + " 10:30:00", DateUtil.parseDate(statDate)));

        //四级道路数据
        list.add(new VehicleWorkStatResult(null, 4, 500000.00, "1", 800000 * RandomUtil.randomDouble(0.2, 0.8, 4, RoundingMode.CEILING), String.valueOf(RandomUtil.randomDouble(0.4000, 1.00, 4, RoundingMode.CEILING)), statDate + " 08:00:00", statDate + " 10:30:00",  DateUtil.parseDate(statDate)));

        list.stream().forEach(vehicleWorkStatResult -> {
            vehicleWorkStatResult.setId(System.currentTimeMillis());
            vehicleWorkStatResult.setStatus(1);
            vehicleWorkStatResult.setIsDeleted(0);
            vehicleWorkStatResult.setTenantId(projectCode);
            baseMapper.insert(vehicleWorkStatResult);
        });
    }

    @Override
    public Boolean statVehicleDistanceInfo(String tenantId, Date beginTime, Date endTime) {
        log.info("statVehicleDistanceInfo.begin---------------------done");

        //0.先计算规划里程
        List<RoadAreaDTO> roadAreaDTOList = workareaRoadInfoClient.getRoadAreaByTenantId(tenantId).getData();
        if(ObjectUtil.isNotEmpty(roadAreaDTOList) && roadAreaDTOList.size() > 0){
            Map<String,VehicleWorkStatResult> vehicleWorkStatResultMap = new HashMap<String,VehicleWorkStatResult>();
            for(RoadAreaDTO roadAreaDTO:roadAreaDTOList){
                VehicleWorkStatResult vehicleWorkStatResult1 = new VehicleWorkStatResult();
                vehicleWorkStatResult1.setBeginTime(TimeUtil.getYYYYMMDDHHMMSS(beginTime));
                vehicleWorkStatResult1.setEndTime(TimeUtil.getYYYYMMDDHHMMSS(endTime));
                vehicleWorkStatResult1.setStatDate(beginTime);
                vehicleWorkStatResult1.setWorkareaAcreage(new BigDecimal(roadAreaDTO.getRoadArea()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                vehicleWorkStatResult1.setWorkareaLevel(Integer.parseInt(roadAreaDTO.getRoadLevel()));
                vehicleWorkStatResult1.setVehicleWorktype("1");
                vehicleWorkStatResult1.setTenantId(tenantId);
                vehicleWorkStatResult1.setRealWorkAcreage(0.0);
                vehicleWorkStatResult1.setRealWorkPerc("0");
                vehicleWorkStatResultMap.put(roadAreaDTO.getRoadLevel()+"|1",vehicleWorkStatResult1);
                VehicleWorkStatResult vehicleWorkStatResult2 = new VehicleWorkStatResult();
                vehicleWorkStatResult2.setBeginTime(TimeUtil.getYYYYMMDDHHMMSS(beginTime));
                vehicleWorkStatResult2.setEndTime(TimeUtil.getYYYYMMDDHHMMSS(endTime));
                vehicleWorkStatResult2.setStatDate(beginTime);
                vehicleWorkStatResult2.setWorkareaAcreage(new BigDecimal(roadAreaDTO.getRoadArea()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                vehicleWorkStatResult2.setWorkareaLevel(Integer.parseInt(roadAreaDTO.getRoadLevel()));
                vehicleWorkStatResult2.setVehicleWorktype("2");
                vehicleWorkStatResult2.setTenantId(tenantId);
                vehicleWorkStatResult2.setRealWorkAcreage(0.0);
                vehicleWorkStatResult2.setRealWorkPerc("0");
                vehicleWorkStatResultMap.put(roadAreaDTO.getRoadLevel()+"|2",vehicleWorkStatResult2);
                VehicleWorkStatResult vehicleWorkStatResult3 = new VehicleWorkStatResult();
                vehicleWorkStatResult3.setBeginTime(TimeUtil.getYYYYMMDDHHMMSS(beginTime));
                vehicleWorkStatResult3.setEndTime(TimeUtil.getYYYYMMDDHHMMSS(endTime));
                vehicleWorkStatResult3.setStatDate(beginTime);
                vehicleWorkStatResult3.setWorkareaAcreage(new BigDecimal(roadAreaDTO.getRoadArea()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                vehicleWorkStatResult3.setWorkareaLevel(Integer.parseInt(roadAreaDTO.getRoadLevel()));
                vehicleWorkStatResult3.setVehicleWorktype("3");
                vehicleWorkStatResult3.setTenantId(tenantId);
                vehicleWorkStatResult3.setRealWorkAcreage(0.0);
                vehicleWorkStatResult3.setRealWorkPerc("0");
                vehicleWorkStatResultMap.put(roadAreaDTO.getRoadLevel()+"|3",vehicleWorkStatResult3);
            }

            QueryWrapper<VehicleDistanceInfo> wrapper = new QueryWrapper<VehicleDistanceInfo>();
            wrapper.lambda().eq(VehicleDistanceInfo::getTenantId, tenantId);
            wrapper.lambda().ge(VehicleDistanceInfo::getBeginTime, TimeUtil.getYYYYMMDDHHMMSS(beginTime));
            wrapper.lambda().le(VehicleDistanceInfo::getEndTime, TimeUtil.getYYYYMMDDHHMMSS(endTime));
            //1.根据项目编码+统计的开始时间+统计的结束时间得到所有的实时里程
            List<VehicleDistanceInfo> vehicleDistanceInfoList = vehicleDistanceInfoService.list(wrapper);
            if (ObjectUtil.isNotEmpty(vehicleDistanceInfoList) && vehicleDistanceInfoList.size() > 0) {
                Map<Integer, List<VehicleDistanceInfo>> level2VehicleDistanceInfoMap = vehicleDistanceInfoList.stream().collect(Collectors.groupingBy(VehicleDistanceInfo::getWorkareaLevel));
                //2.得到相同道路等级的道路集合
                List<List<VehicleDistanceInfo>> level2Infoss = level2VehicleDistanceInfoMap.values().stream().collect(Collectors.toList());
                for (List<VehicleDistanceInfo> level2results : level2Infoss) {
                    Map<String, List<VehicleDistanceInfo>> workType2VehicleDistanceInfoMap = level2results.stream().collect(Collectors.groupingBy(VehicleDistanceInfo::getVehicleWorktype));
                    //3.得到相同道路等级相同工作类型的道路集合
                    List<List<VehicleDistanceInfo>> workType2Infoss = workType2VehicleDistanceInfoMap.values().stream().collect(Collectors.toList());
                    for (List<VehicleDistanceInfo> workType2Infos : workType2Infoss) {
                        //4.计算相同道路等级相同工作类型的道路作业完成率
                        VehicleWorkStatResult vehicleWorkStatResult = vehicleWorkStatResultMap.get(workType2Infos.get(0).getWorkareaLevel()+"|"+workType2Infos.get(0).getVehicleWorktype());
                        if(ObjectUtil.isNotEmpty(vehicleWorkStatResult)) {
                            double acreage = vehicleWorkStatResult.getWorkareaAcreage();;
                            double realAcreage = 0;
                            double realWorkPerc = 0;
                            for (VehicleDistanceInfo vehicleDistanceInfo : workType2Infos) {
                                realAcreage = realAcreage + Double.parseDouble(vehicleDistanceInfo.getRealDistance()) * Double.parseDouble(vehicleDistanceInfo.getWorkareaWidth());
                            }
                            realWorkPerc = acreage <= 0 ? 1.0000 : (realAcreage / acreage);
                            vehicleWorkStatResult.setRealWorkAcreage(new BigDecimal(realAcreage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            vehicleWorkStatResult.setRealWorkPerc(new BigDecimal(realWorkPerc).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
                            vehicleWorkStatResultMap.put(workType2Infos.get(0).getWorkareaLevel()+"|"+workType2Infos.get(0).getVehicleWorktype(),vehicleWorkStatResult);
;
                        }
                    }
                }

            }
            saveBatch(vehicleWorkStatResultMap.values().stream().collect(Collectors.toList()));
        }
        log.info("statVehicleDistanceInfo.end---------------------done");

        return true;
    }

    @Override
    @Async
    public void vehicleWorkStatRun(String startTime, String endTime, String statDate,List<String> projectCodeList) {
        log.info("vehicleWorkStatRun.begin---------------------done"+projectCodeList);

        Date now = new Date();
        if(ObjectUtil.isNotEmpty(statDate)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            try {
                now = simpleDateFormat.parse(statDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(now);
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.split(":")[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(startTime.split(":")[1]));
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND,0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(now);
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.split(":")[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(endTime.split(":")[1]));
        cal2.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND,0);

        List<VehicleDistanceInfo> vehicleDistanceInfoList = new ArrayList<VehicleDistanceInfo>();
        for(String projectCode:projectCodeList){
            try {
                log.info("vehicleWorkStatRun projectCode --------------------"+projectCode);

                //2.查询每个租户下所有的工作路线。
                List<WorkareaInfo> workareaInfos = workareaClient.getWorkareaInfoByTenantId(projectCode).getData();
                //所有路线ID
                List<String> workareaIdList = new ArrayList<String>();
                //路线ID对应VehicleDistanceInfo
                Map<String, VehicleDistanceInfo> vehicleDistanceInfoMap = new HashMap<String,VehicleDistanceInfo>();
                if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(workareaInfos) && workareaInfos.size() > 0) {
                    for (WorkareaInfo workareaInfo : workareaInfos) {
                        //3.每条线路生成vehicleDistanceInfo对象
                        if (workareaInfo.getAreaType().toString().equals("1") && workareaInfo.getBindType().toString().equals("2")) {

                                VehicleDistanceInfo vehicleDistanceInfo = new VehicleDistanceInfo();
                                vehicleDistanceInfo.setWorkareaId(workareaInfo.getId());
                                vehicleDistanceInfo.setWorkareaWidth(ObjectUtil.isNotEmpty(workareaInfo.getWidth())?workareaInfo.getWidth():"15");
                                vehicleDistanceInfo.setWorkareaLength(workareaInfo.getLength());
                                vehicleDistanceInfo.setWorkareaLevel(workareaInfo.getAreaLevel());
                                vehicleDistanceInfo.setBeginTime(TimeUtil.getYYYYMMDDHHMMSS(cal1.getTime()));
                                vehicleDistanceInfo.setEndTime(TimeUtil.getYYYYMMDDHHMMSS(cal2.getTime()));
                                vehicleDistanceInfo.setStatDate(now);
                                vehicleDistanceInfo.setRealDistance("0");
                                vehicleDistanceInfo.setVehicleWorktype("1");
                                vehicleDistanceInfo.setTenantId(projectCode);
                                workareaIdList.add(workareaInfo.getId().toString());
                                vehicleDistanceInfoMap.put(workareaInfo.getId().toString(), vehicleDistanceInfo);

                        } else if (workareaInfo.getAreaType().toString().equals("2") && workareaInfo.getBindType().toString().equals("2")) {
                            //车辆区域目前暂不考虑
                        }
                    }
                    log.info("vehicleWorkStatRun.begin-workareaIdList.size() --------------------"+workareaIdList.size());

                    if (workareaIdList.size() > 0) {
                        //2.1 查询路线绑定的车辆
                        Map<Long, Long> workarea2vehicleMap = new HashMap<Long, Long>();
                        List<String> vehicleIdList2 = new ArrayList<String>();
                        List<String> workareaIdList2 = new ArrayList<String>();
                        List<WorkareaRel> workareaRelList = workareaRelClient.getByWorkareaIds(workareaIdList, 2L).getData();
                        if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(workareaRelList) && workareaRelList.size() > 0) {
                            for (WorkareaRel workareaRel : workareaRelList) {
                                if (org.springblade.core.tool.utils.ObjectUtil.isEmpty(workarea2vehicleMap.get(workareaRel.getWorkareaId()))) {
                                    workarea2vehicleMap.put(workareaRel.getWorkareaId(), workareaRel.getEntityId());
                                    vehicleIdList2.add(workareaRel.getEntityId().toString());
                                    workareaIdList2.add(workareaRel.getWorkareaId().toString());
                                }
                            }
                        }

                        if (vehicleIdList2.size() > 0) {
                            Query query = new Query();
                            query.addCriteria(Criteria.where("tenantId").is(projectCode));
                            query.addCriteria(Criteria.where("id").in(vehicleIdList2));
                            List<BasicVehicleInfoDTO> basicVehicleInfoDTOList = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
                            if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(basicVehicleInfoDTOList) && basicVehicleInfoDTOList.size() > 0) {
                                for (BasicVehicleInfoDTO basicVehicleInfoDTO : basicVehicleInfoDTOList) {
                                    for (int i = 0; i < vehicleIdList2.size(); i++) {
                                        if (vehicleIdList2.get(i).equals(basicVehicleInfoDTO.getId())) {
                                            vehicleDistanceInfoMap.get(workareaIdList2.get(i)).setVehicleId(basicVehicleInfoDTO.getId());
                                            vehicleDistanceInfoMap.get(workareaIdList2.get(i)).setPlateNumber(basicVehicleInfoDTO.getPlateNumber());
                                            vehicleDistanceInfoMap.get(workareaIdList2.get(i)).setVehicleWorktype(ObjectUtil.isNotEmpty(basicVehicleInfoDTO.getVehicleWorkTypeCode())?basicVehicleInfoDTO.getVehicleWorkTypeCode():"1");
                                        }
                                    }
                                }
                            }
                        }


                        //4.调用大数据得到每条线路的实际里程和工作车辆
                        Map<String, String> workareaDeviceCodeMap = new HashMap<String, String>();
                        List<String> deviceCodeList = new ArrayList<String>();
                        JSONObject param = new JSONObject();
                        param.put("tenantId", projectCode);
                        param.put("beginTime", TimeUtil.getNoLineYYYYMMDDHHMISS(cal1.getTime()));
                        param.put("endTime", TimeUtil.getNoLineYYYYMMDDHHMISS(cal2.getTime()));
                        String res = BigDataHttpClient.postDataToBigData("/smartenv-api/sync/vehicleWorkDistance", param.toString());
                        log.info("/smartenv-api/sync/vehicleWorkDistance --------------------"+res);

                        JSONObject resObj = JSONUtil.parseObj(res);
                        if (resObj.getInt("code") == 0) {
                            JSONObject data = resObj.getJSONObject("data");
                            if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(data) && org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(data.get("distances"))) {
                                JSONArray distances = data.getJSONArray("distances");
                                if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(distances) && distances.size() > 0) {
                                    distances.jsonIter().forEach(object -> {
                                        String areaId = object.getStr("areaid");
                                        String distance = object.getStr("distance");
                                        String deviceCode = object.getStr("deviceid");
                                        workareaDeviceCodeMap.put(deviceCode, areaId);
                                        deviceCodeList.add(deviceCode);
                                        if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(vehicleDistanceInfoMap.get(areaId))) {
                                            vehicleDistanceInfoMap.get(areaId).setRealDistance(distance);
                                        }
                                    });
                                }
                            }
                        }

                        //5.根据大数据返回的路线工作车辆得到此时路线上车辆的工作类型
                        Query query1 = new Query();
                        query1.addCriteria(Criteria.where("tenantId").is(projectCode));
                        query1.addCriteria(Criteria.where("cvrDeviceCode").in(deviceCodeList));
                        List<BasicVehicleInfoDTO> basicVehicleInfoDTOList1 = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
                        if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(basicVehicleInfoDTOList1) && basicVehicleInfoDTOList1.size() > 0) {
                            for (BasicVehicleInfoDTO basicVehicleInfoDTO : basicVehicleInfoDTOList1) {
                                if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(workareaDeviceCodeMap.get(basicVehicleInfoDTO.getCvrDeviceCode()))) {
                                    String areaId = workareaDeviceCodeMap.get(basicVehicleInfoDTO.getCvrDeviceCode());
                                    if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(vehicleDistanceInfoMap.get(areaId))) {
                                        vehicleDistanceInfoMap.get(areaId).setVehicleId(basicVehicleInfoDTO.getId());
                                        vehicleDistanceInfoMap.get(areaId).setPlateNumber(basicVehicleInfoDTO.getPlateNumber());
                                        vehicleDistanceInfoMap.get(areaId).setVehicleWorktype(ObjectUtil.isNotEmpty(basicVehicleInfoDTO.getVehicleWorkTypeCode())?basicVehicleInfoDTO.getVehicleWorkTypeCode():"1");
                                    }
                                }
                            }
                        }

                        Query query2 = new Query();
                        query2.addCriteria(Criteria.where("tenantId").is(projectCode));
                        query2.addCriteria(Criteria.where("gpsDeviceCode").in(deviceCodeList));
                        List<BasicVehicleInfoDTO> basicVehicleInfoDTOList2 = mongoTemplate.find(query2, BasicVehicleInfoDTO.class);
                        if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(basicVehicleInfoDTOList2) && basicVehicleInfoDTOList2.size() > 0) {
                            for (BasicVehicleInfoDTO basicVehicleInfoDTO : basicVehicleInfoDTOList2) {
                                if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(workareaDeviceCodeMap.get(basicVehicleInfoDTO.getGpsDeviceCode()))) {
                                    String areaId = workareaDeviceCodeMap.get(basicVehicleInfoDTO.getGpsDeviceCode());
                                    if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(vehicleDistanceInfoMap.get(areaId))) {
                                        vehicleDistanceInfoMap.get(areaId).setVehicleId(basicVehicleInfoDTO.getId());
                                        vehicleDistanceInfoMap.get(areaId).setPlateNumber(basicVehicleInfoDTO.getPlateNumber());
                                        vehicleDistanceInfoMap.get(areaId).setVehicleWorktype(ObjectUtil.isNotEmpty(basicVehicleInfoDTO.getVehicleWorkTypeCode())?basicVehicleInfoDTO.getVehicleWorkTypeCode():"1");
                                    }
                                }
                            }
                        }

                        Query query3 = new Query();
                        query3.addCriteria(Criteria.where("tenantId").is(projectCode));
                        query3.addCriteria(Criteria.where("nvrDeviceCode").in(deviceCodeList));
                        List<BasicVehicleInfoDTO> basicVehicleInfoDTOList3 = mongoTemplate.find(query3, BasicVehicleInfoDTO.class);
                        if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(basicVehicleInfoDTOList3) && basicVehicleInfoDTOList3.size() > 0) {
                            for (BasicVehicleInfoDTO basicVehicleInfoDTO : basicVehicleInfoDTOList3) {
                                if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(workareaDeviceCodeMap.get(basicVehicleInfoDTO.getNvrDeviceCode()))) {
                                    String areaId = workareaDeviceCodeMap.get(basicVehicleInfoDTO.getNvrDeviceCode());
                                    if (org.springblade.core.tool.utils.ObjectUtil.isNotEmpty(vehicleDistanceInfoMap.get(areaId))) {
                                        vehicleDistanceInfoMap.get(areaId).setVehicleId(basicVehicleInfoDTO.getId());
                                        vehicleDistanceInfoMap.get(areaId).setPlateNumber(basicVehicleInfoDTO.getPlateNumber());
                                        vehicleDistanceInfoMap.get(areaId).setVehicleWorktype(ObjectUtil.isNotEmpty(basicVehicleInfoDTO.getVehicleWorkTypeCode())?basicVehicleInfoDTO.getVehicleWorkTypeCode():"1");
                                    }
                                }
                            }
                        }
                    }

                    //6.批量保存实际里程数据
                    List<VehicleDistanceInfo> vehicleDistanceInfoList_ = new ArrayList<VehicleDistanceInfo>(vehicleDistanceInfoMap.values());
                    vehicleDistanceInfoService.saveBatch(vehicleDistanceInfoList_);
                    log.info("VehicleDistanceInfo.saveBatch---------------------done");

                    //7.开始异步计算完成率。
                    statVehicleDistanceInfo(projectCode, cal1.getTime(), cal2.getTime());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeVehicleWorkStat(String startTime, String endTime,String statDate, List<String> projectCodeList) {
        Date now = new Date();

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(now);
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.split(":")[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(startTime.split(":")[1]));
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND,0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(now);
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.split(":")[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(endTime.split(":")[1]));
        cal2.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND,0);

        QueryWrapper<VehicleDistanceInfo> wrapper = new QueryWrapper<VehicleDistanceInfo>();
        wrapper.lambda().eq(VehicleDistanceInfo::getBeginTime,TimeUtil.getYYYYMMDDHHMMSS(cal1.getTime()));
        wrapper.lambda().eq(VehicleDistanceInfo::getEndTime,TimeUtil.getYYYYMMDDHHMMSS(cal2.getTime()));
        wrapper.lambda().eq(VehicleDistanceInfo::getStatDate,statDate);
        wrapper.lambda().in(VehicleDistanceInfo::getTenantId,projectCodeList);
        vehicleDistanceInfoService.remove(wrapper);
        log.info("VehicleDistanceInfo.remove---------------------done");

        QueryWrapper<VehicleWorkStatResult> wrapper1 = new QueryWrapper<VehicleWorkStatResult>();
        wrapper1.lambda().eq(VehicleWorkStatResult::getBeginTime,TimeUtil.getYYYYMMDDHHMMSS(cal1.getTime()));
        wrapper1.lambda().eq(VehicleWorkStatResult::getEndTime,TimeUtil.getYYYYMMDDHHMMSS(cal2.getTime()));
        wrapper1.lambda().eq(VehicleWorkStatResult::getStatDate,statDate);
        wrapper1.lambda().in(VehicleWorkStatResult::getTenantId,projectCodeList);
        remove(wrapper1);
        log.info("VehicleWorkStatResult.remove---------------------done");


    }
}
