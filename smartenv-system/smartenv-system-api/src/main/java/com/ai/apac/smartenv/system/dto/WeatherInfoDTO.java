package com.ai.apac.smartenv.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qianlong
 * @description  天气信息数据传输对象(https://tianqiapi.com/)
 * @Date 2020/2/9 5:03 下午
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherInfoDTO implements Serializable {

    @ApiModelProperty("城市编码")
    @JsonProperty("cityid")
    private String cityId;

    @ApiModelProperty("城市名称")
    @JsonProperty("city")
    private String cityName;

    @ApiModelProperty("当前星期")
    private String week;

    @ApiModelProperty("气象台更新时间")
    @JsonProperty("update_time")
    private String updateTime;

    @ApiModelProperty("当前日期")
    private String date;

    @ApiModelProperty("天气情况")
    private String wea;

    @ApiModelProperty("天气情况对应图标")
    @JsonProperty("wea_img")
    private String weaImg;

    @ApiModelProperty("天气情况对应图片")
    private String weaImgPath;

    @ApiModelProperty("实时温度")
    @JsonProperty("tem")
    private String realTimeTem;

    @ApiModelProperty("最高温度")
    @JsonProperty("tem1")
    private String maxTem;

    @ApiModelProperty("最低温度")
    @JsonProperty("tem2")
    private String minTem;

    @ApiModelProperty("风向")
    private String win;

    @ApiModelProperty("风力等级")
    @JsonProperty("win_speed")
    private String winSpeed;

    @ApiModelProperty("风速")
    @JsonProperty("win_meter")
    private String winMeter;

    @ApiModelProperty("能见度")
    private String humidity;

    @ApiModelProperty("气压hPa")
    private String pressure;

    @ApiModelProperty("空气质量")
    private String air;

    @ApiModelProperty("空气PM2.5")
    @JsonProperty("air_pm25")
    private String airPm25;

    @ApiModelProperty("空气质量等级")
    @JsonProperty("air_level")
    private String airLevel;

    @ApiModelProperty("空气质量描述")
    @JsonProperty("air_tips")
    private String airTips;
}
