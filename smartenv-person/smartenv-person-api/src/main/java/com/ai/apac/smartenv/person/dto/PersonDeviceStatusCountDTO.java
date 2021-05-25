package com.ai.apac.smartenv.person.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 人员定位设备状态数据统计
 * @Date 2020/11/9 11:13 上午
 **/
@Data
@ApiModel("人员定位设备状态数据统计")
public class PersonDeviceStatusCountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("人员总数")
    Integer personCount;

    @ApiModelProperty("在线数量")
    Integer onPersonCount;

    @ApiModelProperty("离线数量")
    Integer offPersonCount;

    @ApiModelProperty("未绑定设备数量")
    Integer nodPersonCount;

    @ApiModelProperty("项目编码")
    private String projectCode;

    public PersonDeviceStatusCountDTO() {
        this.personCount = 0;
        this.onPersonCount = 0;
        this.offPersonCount = 0;
        this.nodPersonCount = 0;
    }

    public PersonDeviceStatusCountDTO(Integer personCount, Integer onPersonCount, Integer offPersonCount, Integer nodPersonCount, String projectCode) {
        this.personCount = personCount;
        this.onPersonCount = onPersonCount;
        this.offPersonCount = offPersonCount;
        this.nodPersonCount = nodPersonCount;
        this.projectCode = projectCode;
    }
}
