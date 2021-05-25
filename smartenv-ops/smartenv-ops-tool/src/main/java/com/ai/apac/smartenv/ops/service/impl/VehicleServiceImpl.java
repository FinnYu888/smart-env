package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.ai.apac.smartenv.ops.service.IVehicleService;
import com.ai.apac.smartenv.ops.util.HttpServiceUtil;
import com.ai.apac.smartenv.ops.util.TokenUtil;
import com.ai.apac.smartenv.vehicle.dto.SimpleVehicleTrackInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.api.R;

import java.util.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/16 11:36 下午
 **/
@Slf4j
public class VehicleServiceImpl implements IVehicleService {

//    static final String SERVER_URL = "http://localhost/";
    static final String SERVER_URL = "http://www.asiainfo.tech:31203/";;

    static final String LIST_VEHICLE_TRACK = SERVER_URL + "smartenv-vehicle/vehicleinfo/trackInfo";

    static final String STAT_VEHICLE_TRACK = SERVER_URL + "smartenv-vehicle/vehicleinfo/statTrackInfo";

    static final String UPDATE_DEVICE_STATUS = SERVER_URL + "smartenv-omnic/polymerization/workStatusChange";

    /**
     * 根据项目编码查询车辆轨迹数据并生成报表
     *
     * @param projectCode
     */
    @Override
    public void saveVehicleTrackInfoReport(List<String> dateList, String projectCode) {
        String token = TokenUtil.getAuthToken();
//        Date endDate = DateUtil.minusDays(new Date(), 1);
//        Date startDate = DateUtil.minusDays(new Date(), 7);
        List<SimpleVehicleTrackInfoDTO> simpleVehicleTrackInfoDTOList = new ArrayList<>();
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        String projectName = "";
        for (String statDate : dateList) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("statDate", statDate);
            params.put("projectCode", projectCode);
            String res = HttpServiceUtil.getForm(LIST_VEHICLE_TRACK, params, token);
            log.info("查询车辆轨迹数据:{}", res);
            R<List<SimpleVehicleTrackInfoDTO>> result = JSONUtil.toBean(res, new R<List<SimpleVehicleTrackInfoDTO>>().getClass());
            String dataListStr = JSONUtil.toJsonStr(result.getData());
            JSONArray jsonArray = JSONUtil.parseArray(result.getData());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("车牌号", obj.getStr("plateNumber"));
                row.put("车辆定位设备号", obj.getStr("gpsDeviceCode"));
                row.put("日期", obj.getStr("statDate"));
                String totalDistance = obj.getStr("totalDistance");
                if (StringUtils.isNotEmpty(totalDistance)) {
                    totalDistance = NumberUtil.roundStr(Double.valueOf(totalDistance) / 1000.00, 2);
                } else {
                    totalDistance = "0.0";
                }
                row.put("行驶里程(公里)", totalDistance);
                String totalWorkDistance = obj.getStr("totalWorkDistance");
                if (StringUtils.isNotEmpty(totalWorkDistance)) {
                    totalWorkDistance = NumberUtil.roundStr(Double.valueOf(totalWorkDistance) / 1000.00, 2);
                } else {
                    totalWorkDistance = "0.0";
                }
                row.put("作业里程(公里)", totalWorkDistance);
                row.put("公司", obj.getStr("projectName"));
                rows.add(row);

                projectName = obj.getStr("projectName");
            }
        }

//        for (SimpleVehicleTrackInfoDTO simpleVehicleTrackInfoDTO : simpleVehicleTrackInfoDTOList) {
//            Map<String, Object> row = new LinkedHashMap<>();
//            row.put("车牌号", simpleVehicleTrackInfoDTO.getPlateNumber());
//            row.put("车辆定位设备号", simpleVehicleTrackInfoDTO.getGpsDeviceCode());
//            row.put("日期", simpleVehicleTrackInfoDTO.getStatDate());
//            row.put("行驶里程", simpleVehicleTrackInfoDTO.getTotalDistance());
//            row.put("作业里程", simpleVehicleTrackInfoDTO.getTotalWorkDistance());
//            rows.add(row);
//        }

        String statDateStr = "(" + dateList.get(dateList.size() - 1) + "-" + dateList.get(0) + ")";
        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("/Users/qianlong/Git/aii-apac/apac-smartenv/smartenv-app-platform/excel/" + projectName + "_" + projectCode + statDateStr + ".xlsx");
//        //自定义标题别名
//        writer.addHeaderAlias("plateNumber", "车牌号");
//        writer.addHeaderAlias("gpsDeviceCode", "设备号");
//        writer.addHeaderAlias("statDate", "日期");
//        writer.addHeaderAlias("beginTime", "开始时间");
//        writer.addHeaderAlias("endTime", "结束时间");
//        writer.addHeaderAlias("totalDistance", "行驶里程");
//        writer.addHeaderAlias("totalWorkDistance", "作业里程");
//        writer.addHeaderAlias("avgSpeed", "平均速度");
//        writer.addHeaderAlias("maxSpeed", "最高速度");
//        writer.addHeaderAlias("totalCount", "总上报次数");
//        writer.addHeaderAlias("projectCode", "项目编码");
//        writer.addHeaderAlias("projectName", "项目名称");

        // 合并单元格后的标题行，使用默认标题样式
//        writer.merge(rows.size() - 1, "龙马公司统计");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(rows, true);
        // 关闭writer，释放内存
        writer.close();
    }

    public void statVehicleTrack(String date, String projectCode) {
        String token = TokenUtil.getAuthToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put("statDate", date);
        params.put("projectCode", projectCode);
        String res = HttpServiceUtil.postForm(STAT_VEHICLE_TRACK, params, token);
        log.info("统计车辆轨迹数据:{}", res);
    }

    public void updateDeviceStatus(String filePath){
        String token = TokenUtil.getAuthToken("zhonghunajie","123456");
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath), "Sheet1");
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.stream().forEach(workAreaInfo -> {
            String deviceCode = (String) workAreaInfo.get("deviceCode");
            HashMap<String, Object> params = new HashMap<>();
            params.put("deviceCode", deviceCode);
            params.put("workAreaType", 1);
            String res = HttpServiceUtil.getForm(UPDATE_DEVICE_STATUS, params, token);
            log.info("同步设备状态结果:{}", res);
        });


    }

//    public static void main(String[] args) throws Exception {
//        String file = " /Users/qianlong/SVN/smartenv/DOC/16产品设计/01需求收集/沧州台账数据/待同步设备Code.xlsx";
//        VehicleServiceImpl vehicleService = new VehicleServiceImpl();
//        vehicleService.updateDeviceStatus(file);
//        Integer statDate = 20210110;
//        Integer days = 7;
//        List<String> statDateList = new ArrayList<String>();
//        for (int i = 0; i < days; i++) {
////            vehicleService.statVehicleTrack(String.valueOf(statDate + i), "752224");
//            statDateList.add(String.valueOf(statDate + i));
//        }
//        vehicleService.saveVehicleTrackInfoReport(statDateList, "201546");
//        vehicleService.saveVehicleTrackInfoReport(statDateList, "752224");
//        vehicleService.saveVehicleTrackInfoReport(statDateList, "991859");
//    }
}
