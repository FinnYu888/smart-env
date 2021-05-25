package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.ai.apac.smartenv.ops.service.IStatService;
import com.ai.apac.smartenv.ops.util.HttpServiceUtil;
import com.ai.apac.smartenv.ops.util.TokenUtil;
import com.ai.apac.smartenv.statistics.entity.AreaAlarmCountInfo;
import com.ai.apac.smartenv.statistics.entity.AreaIllegalBehaviorInfo;
import com.ai.apac.smartenv.statistics.entity.AreaTrashInfo;
import com.ai.apac.smartenv.statistics.entity.AreaWorkInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/20 9:59 上午
 **/
@Slf4j
public class StatServiceImpl implements IStatService {

    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    //    static final String SERVER_URL = "http://localhost/";
    static final String SERVER_URL = "http://www.asiainfo.tech:31203/";

    static final String IMPORT_TRASH_URL = SERVER_URL + "smartenv-statistic/easyv/area/batchAreaTrashInfo";

    static final String IMPORT_ILLEGAL_BEHAVIOR_URL = SERVER_URL + "/smartenv-statistic/easyv/area/batchAreaIllegalBehaviorInfo";

    static final String IMPORT_ALARM_COUNT_URL = SERVER_URL + "/smartenv-statistic/easyv/area/batchAreaAlarmCount";

    static final String IMPORT_WORK_INFO_URL = SERVER_URL + "/smartenv-statistic/easyv/area/areaWorkInfo";

    @Override
    public void importTrashData(Integer min, Integer max, String areaCode) {
        String token = TokenUtil.getAuthToken();
        List<AreaTrashInfo> list = new ArrayList<>();
        //获取当月每一天
        List<String> dayList = getDayListOfMonth();
        dayList.stream().forEach(day -> {
            AreaTrashInfo areaTrashInfo = new AreaTrashInfo();
            areaTrashInfo.setStatDate(day);
            areaTrashInfo.setWeight(RandomUtil.randomDouble(min, max, 2, RoundingMode.CEILING));
            areaTrashInfo.setItemType("all");
            areaTrashInfo.setAreaCode(areaCode);
            list.add(areaTrashInfo);
        });

        String res = HttpServiceUtil.postRestful(IMPORT_TRASH_URL, list, token);
        log.info("批量保存垃圾收运数据结果:{}", res);
    }

    /**
     * 获取当月所有天
     *
     * @return
     */
    public static List<String> getDayListOfMonth() {
        List<String> list = new ArrayList<String>();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int year = aCalendar.get(Calendar.YEAR);
        int month = aCalendar.get(Calendar.MONTH) + 1;
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        String monthStr = String.valueOf(month).length() < 2 ? "0" + month : String.valueOf(month);
        for (int i = 1; i <= day; i++) {
            String dayStr = String.valueOf(i).length() < 2 ? "0" + i : String.valueOf(i);
            String aDate = String.valueOf(year) + "-" + monthStr + "-" + dayStr;
            list.add(aDate);
        }
        return list;
    }

    /**
     * 生成违规告警数据
     *
     * @param min
     * @param max
     * @param total
     * @param areaCode
     */
    @Override
    public void importIllegalBehavior(Integer min, Integer max, Integer total, String areaCode) {
        if (min == 0 || max == 0 || total == 0) {
            return;
        }
        String token = TokenUtil.getAuthToken();
        List<AreaIllegalBehaviorInfo> list = new ArrayList<>();
        //获取当月每一天
        List<String> dayList = getDayListOfMonth();
        dayList.stream().forEach(day -> {
            List<Integer> countList = getRandomCount(min, max, total);
            list.add(new AreaIllegalBehaviorInfo(null, areaCode, day, "滞留", "1", "vehicle", RandomUtil.randomInt(30), day));
            list.add(new AreaIllegalBehaviorInfo(null, areaCode, day, "越界", "2", "vehicle", RandomUtil.randomInt(50), day));
            list.add(new AreaIllegalBehaviorInfo(null, areaCode, day, "超速", "3", "vehicle", RandomUtil.randomInt(30), day));
            list.add(new AreaIllegalBehaviorInfo(null, areaCode, day, "滞留", "1", "person", RandomUtil.randomInt(50), day));
            list.add(new AreaIllegalBehaviorInfo(null, areaCode, day, "越界", "2", "person", RandomUtil.randomInt(20), day));
        });
        String res = HttpServiceUtil.postRestful(IMPORT_ILLEGAL_BEHAVIOR_URL, list, token);
        log.info("批量保存告警分析数据结果:{}", res);
    }

    /**
     * 生成区域告警数量统计
     *
     * @param min
     * @param max
     * @param areaCode
     */
    @Override
    public void importAlarmCount(Integer min, Integer max, String areaCode) {
        if (min == 0 || max == 0) {
            return;
        }
        String token = TokenUtil.getAuthToken();
        List<AreaAlarmCountInfo> list = new ArrayList<>();
        //获取当月每一天
        List<String> dayList = getDayListOfMonth();
        dayList.stream().forEach(day -> {
            AreaAlarmCountInfo alarmCountInfo = new AreaAlarmCountInfo();
            alarmCountInfo.setCount(RandomUtil.randomLong(min, max));
            alarmCountInfo.setStatDate(day);
            alarmCountInfo.setAreaCode(areaCode);
            alarmCountInfo.setCreateDate(new Date());
            list.add(alarmCountInfo);
        });
        String res = HttpServiceUtil.postRestful(IMPORT_ALARM_COUNT_URL, list, token);
        log.info("批量保存告警数量数据结果:{}", res);
    }

    /**
     * 生成区域违规行为分析
     *
     * @param min
     * @param max
     * @param areaCode
     */
    @Override
    public void importKpi(Integer min, Integer max, String areaCode) {

    }

    /**
     * 生成区域工作完成率
     *
     * @param vehicleMax
     * @param personMax
     * @param areaCode
     */
    @Override
    public void importAreaWorkInfo(Integer vehicleMax, Integer personMax, String areaCode) {
        String token = TokenUtil.getAuthToken();
        //获取当月每一天
        List<String> dayList = getDayListOfMonth();
        dayList.stream().forEach(day -> {
            Double vehicleCompleted = Double.valueOf(DECIMAL_FORMAT.format(vehicleMax * RandomUtil.randomDouble(0.4, 0.8, 2, RoundingMode.CEILING)));
            Double personCompleted = Double.valueOf(DECIMAL_FORMAT.format(personMax * RandomUtil.randomDouble(0.4, 0.8, 2, RoundingMode.CEILING)));
            if (areaCode.startsWith("130")) {
                vehicleCompleted = 0.0;
                personCompleted = 0.0;
            }
            AreaWorkInfo areaWorkInfo = new AreaWorkInfo();
            areaWorkInfo.setStatDate(day);
            areaWorkInfo.setAreaCode(areaCode);
            areaWorkInfo.setTotalWorkAreaForVehicle(Double.valueOf(vehicleMax));
            areaWorkInfo.setCompletedWorkAreaForVehicle(vehicleCompleted);
            areaWorkInfo.setTotalWorkAreaForPerson(Double.valueOf(personMax));
            areaWorkInfo.setCompletedWorkAreaForPerson(personCompleted);
            String res = HttpServiceUtil.postRestful(IMPORT_WORK_INFO_URL, areaWorkInfo, token);
            if (res.indexOf("200") <= 0) {
                log.error("批量保存工作完成情况数据结果:{}", res);
            }
        });
        log.info("批量保存工作完成情况数据结束");
    }

    public List<Integer> getRandomCount(Integer min, Integer max, Integer total) {
        List<Integer> dataList = new ArrayList<Integer>();
        Integer one = RandomUtil.randomInt(max);
        Integer two = RandomUtil.randomInt(max);
        Integer three = RandomUtil.randomInt(max);
        Integer four = RandomUtil.randomInt(max);
        Integer five = total - one - two - three - four;
        if (five <= 0) {
            getRandomCount(min, max, total);
        }
        dataList.add(one);
        dataList.add(two);
        dataList.add(three);
        dataList.add(four);
        dataList.add(five);
        return dataList;
    }

    public static void run(String configFile) {
        //读取配置文件
        ExcelReader reader = ExcelUtil.getReader(configFile);
        List<Map<String, Object>> readAll = reader.readAll();
//        System.out.println(readAll);
        readAll.stream().forEach(configData -> {
            log.info("configData:{}", configData.toString());
            String areaCode = String.valueOf(configData.get("城市编码"));
            if (StringUtils.isEmpty(areaCode)) {
                return;
            }
            String minTrash = String.valueOf(configData.get("垃圾重量(最小)"));
            String maxTrash = String.valueOf(configData.get("垃圾重量(最大)"));
            String minAlarmCount = String.valueOf(configData.get("告警总数(最小)"));
            String maxAlarmCount = String.valueOf(configData.get("告警总数(最大)"));
            String totalAlarmCount = String.valueOf(configData.get("告警总数(合计)"));
            String areaSize = String.valueOf(configData.get("作业面积"));
            String vehicleDistance = String.valueOf(configData.get("车辆里程"));
            vehicleDistance = vehicleDistance.substring(0, vehicleDistance.lastIndexOf("."));
//            log.info("areaCode|||minTrash|||maxTrash|||minAlarmCount|||maxAlarmCount|||totalAlarmCount|||areaSize:{} ||| {} ||| {} ||| {}", areaCode, minTrash, maxTrash, minAlarmCount, maxAlarmCount, totalAlarmCount, areaSize);
            StatServiceImpl statService = new StatServiceImpl();
            statService.importTrashData(Integer.valueOf(minTrash), Integer.valueOf(maxTrash), areaCode);
            statService.importIllegalBehavior(Integer.valueOf(minAlarmCount), Integer.valueOf(maxAlarmCount), Integer.valueOf(totalAlarmCount), areaCode);
            statService.importAlarmCount(Integer.valueOf(totalAlarmCount) / 2, Integer.valueOf(totalAlarmCount), areaCode);
            statService.importAreaWorkInfo(Integer.valueOf(vehicleDistance), Integer.valueOf(areaSize), areaCode);
        });
    }

//    public static void main(String[] args) {
//        run("/Users/qianlong/SVN/smartenv/DOC/14自动脚本/全国作业数据配置.xlsx");
//
//        StatServiceImpl statService = new StatServiceImpl();
//        statService.importTrashData(800,1100,"100000");
//        statService.importIllegalBehavior(200, 500, 1000, "100000");
//        statService.importAlarmCount(600, 1000, "100000");
//        statService.importAreaWorkInfo(780, 2336, "100000");
//        System.out.println(getDayListOfMonth());
//        List<Integer> dataList = new StatServiceImpl().getRandomCount(480, 800, 1600);
//        List<String> dayList = getDayListOfMonth();
//        System.out.println(dayList);
//        System.out.println(DateUtil.today());
//
//    }
}
