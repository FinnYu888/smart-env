package com.ai.apac.smartenv.omnic.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description 实时位置对象
 * @Date 2020/3/6 3:13 下午
 **/
@Data
public class RealTimePositionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Position> positions;

    @Data
    public static class Position {
        @Longitude
        private String lng;
        @Latitude
        private String lat;
        private String speed;
        private String time;
        private String deviceId;
    }
}
