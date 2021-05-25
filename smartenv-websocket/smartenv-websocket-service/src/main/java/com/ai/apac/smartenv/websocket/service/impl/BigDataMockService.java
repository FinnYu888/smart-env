package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackDTO;
import com.ai.apac.smartenv.websocket.module.mock.dto.PutTrackTaskDTO;
import com.ai.apac.smartenv.websocket.service.IBigDataMockService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.ResultCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/10 5:56 下午
 **/
@Service
@Slf4j
public class BigDataMockService implements IBigDataMockService {

    public static final String PUT_TRACK = "http://10.21.35.111:18066/smartenv-api/etl/track/put";

    private static OkHttpClient okHttpClient = null;

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    /**
     * 模拟上报轨迹数据
     *
     * @param trackPutDTO
     */
    @Override
    public String putTrackTask(PutTrackTaskDTO trackPutDTO) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Integer interval = trackPutDTO.getInterval();
                    Integer duration = trackPutDTO.getDuration();
                    BigDecimal beginLat = new BigDecimal(trackPutDTO.getLat());
                    BigDecimal beginLng = new BigDecimal(trackPutDTO.getLng());
                    String deviceCode = trackPutDTO.getDeviceCode();
                    //计算需要循环几次
                    Integer times = (duration * 60) / interval;
                    for (int i = 0; i < times; i++) {
                        //BigDecimal lat = beginLat.add(RandomUtil.randomBigDecimal(new BigDecimal(0), new BigDecimal(10)));
                        beginLng = beginLng.add(new BigDecimal(0.0001));
                        PutTrackDTO putTrackDTO = new PutTrackDTO();
                        putTrackDTO.setDeviceCode(deviceCode);
                        //putTrackDTO.setLat(NumberUtil.round(lat, 4).toString());
                        putTrackDTO.setLat(NumberUtil.round(beginLat, 8).toString());// lat不变，模拟直线
                        putTrackDTO.setLng(NumberUtil.round(beginLng, 8).toString());
                        putTrackDTO.setSpeed(String.valueOf(RandomUtil.randomInt(20, 100)));
                        putTrackDTO.setAccStatus(trackPutDTO.getAccStatus());
                        putTrackMock(putTrackDTO);
                        Thread.sleep(interval * 1000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Thread.interrupted();
                }
            }
        }).start();
        return "创建Mock任务成功";
    }

    /**
     * 单次上报轨迹
     *
     * @param putTrackDTO
     * @return
     */
    @Override
    public String putTrackSingle(PutTrackDTO putTrackDTO) {
        putTrackDTO.setSpeed(String.valueOf(RandomUtil.randomInt(20,100)));
        String result = putTrackMock(putTrackDTO);
        return result;
    }


    private String putTrackMock(PutTrackDTO putTrackDTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", putTrackDTO.getDeviceCode());
        params.put("time", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        params.put("accStatus", putTrackDTO.getAccStatus());
        params.put("lng", putTrackDTO.getLng());
        params.put("lat", putTrackDTO.getLat());
        params.put("speed", putTrackDTO.getSpeed());
        log.info("发送轨迹参数:{}" + JSON.toJSONString(params));
        try {
            String body = HttpUtil.createRequest(Method.POST, PUT_TRACK)
                    .contentType(ContentType.FORM_URLENCODED.toString())
                    .form(params)
                    .execute().body();
            return body;
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
    }
}
