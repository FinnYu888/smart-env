package com.ai.apac.smartenv.statistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author qianlong
 * @description 区域工作完成率汇总数据
 * @Date 2020/12/8 7:15 下午
 **/
@Data
@ApiModel("区域告警数量统计")
@Document(collection = "area_alarm_count")
public class AreaAlarmCountInfo {

    @Id
    @Field("id")
    private String id;

    @Field("area_code")
    @ApiModelProperty("区域编码")
    private String areaCode;

    @Field("stat_date")
    @ApiModelProperty("统计日期")
    private String statDate;

    @Field("count")
    @ApiModelProperty("数量")
    private Long count;

    @Field("create_date")
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;
}
