package com.ai.apac.smartenv.common.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.LauncherConstant;
import okhttp3.*;
import okio.BufferedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BigDataHttpClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/6
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/6  10:37    panfeng          v1.0.0             修改原因
 */
@Component
public class BigDataHttpClient {

    private static Logger log = LoggerFactory.getLogger(BigDataHttpClient.class);

    public static String getPersonCarRealTime = "smartenv-api/device/position/search";
    /**
     * 同步告警规则给大数据
     */
    public static String postAlarmRule = "smartenv-api/sync/alarm";
    /**
     * 同步告警关联实体类型给大数据
     */
    public static String postAlarmRuleRelateEntity = "smartenv-api/sync/categoryAlarmsRel";
    /**
     * 通过UUID取经纬度
     */
    public static String searchByUUID = "smartenv-api/device/track/searchByUUID";

    public static String syncDevicePersonRel = "smartenv-api/sync/devicePersonRel";

    public static String syncDeviceVehicleRel = "smartenv-api/sync/deviceVehicleRel";


    public static String track = "/smartenv-api/device/track/search";

    public static String trackOilAvg = "/smartenv-api/device/track/getOilAvg";

    public static String getDeviceStatus = "/smartenv-api/device/status/search";

    public static String syncDeviceCode = "smartenv-api/device/updateDeviceCode";
    public static String getDeviceRecords = "smartenv-api/device/queryLatestRecords";

    //获取中转站臭味级别
    public static String getOdlyLevelURL = "/smartenv-api/wts/odour/search";

    public static String syncArrangeToBigData = "/smartenv-api/sync/updateSchedule";
    public static String syncCommuterTimeAndMileage = "/smartenv-api/sync/syncCommuterTimeAndMileage";

    public static String bigDataTimeFormat = "yyyyMMddHHmmss";

    private static OkHttpClient okHttpClient;

    @Value("${bigdata.config.addr}")
    private String BIG_DATA_ADDR;

    private static Long TIME_OUT = 2 * 60 * 1000L;

    static {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.MICROSECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    public static String getBigdataAddr() {
        BigDataHttpClient bean = SpringUtil.getBean(BigDataHttpClient.class);
        return bean.BIG_DATA_ADDR;
    }

    /**
     * 返回报文格式
     */
    public interface RESPONSE {
        final static String CODE = "code";
        final static String DATA = "DATA";
    }

    /**
     * 获取臭味基本关键字
     */
    public interface Odoy_Request {
        final static String WTS_IDS = "wtsIds";
        final static String WTS_ODOURS = "wtsOdours";
        final static String WTS_ID = "wtsId";
        final static String WTS_odour = "odour";
    }

    /**
     * 同步操作类型
     */
    public interface OptFlag {
        String ADD = "1"; // 新增
        String EDIT = "2"; // 修改
        String REMOVE = "3"; // 删除
    }

    //从大数据获取消息
    public static String getBigDataBody(String path, Map<String, Object> params) throws IOException {
        log.debug(path + "--" + params);
        String paramStr = HttpUtil.toParams(params, "utf-8");
        final Request request = new Request.Builder()
                .get()
                .url(getBigdataAddr().concat(path).concat("?").concat(paramStr))
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        return execute.body().string();
    }

    public static <T> T getBigDataBodyToObjcet(String path, Map<String, Object> params, Class<T> objcetType) throws IOException {
        String bigDataBody = getBigDataBody(path, params);
        return JSONUtil.toBean(bigDataBody, objcetType);
    }


    //从大数据获取消息
    public static String postDataToBigData(String path, String bodyString) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);
        final Request request = new Request.Builder()
                .get()
                .url(getBigdataAddr().concat(path))
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        String string = execute.body().string();
        log.info("response:" + string);

        return string;
    }

}





