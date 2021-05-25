package com.ai.apac.smartenv.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/9 1:58 下午
 **/
@Data
public class DeviceStatusCountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("所有状态数量")
    Integer allStatusCount;

    @ApiModelProperty("正常状态数量")
    Integer normalCount;

    @ApiModelProperty("正常关闭数量")
    Integer offCount;

    @ApiModelProperty("异常关闭数量")
    Integer errorOffCount;

    @ApiModelProperty("无设备信息数量")
    Integer noSingleCount;

    @ApiModelProperty("未绑定设备数量")
    Integer unBindCount;

    public DeviceStatusCountDTO() {
        this.allStatusCount = 0;
        this.normalCount = 0;
        this.offCount = 0;
        this.errorOffCount = 0;
        this.noSingleCount = 0;
        this.unBindCount = 0;
    }

    public DeviceStatusCountDTO(Integer allStatusCount, Integer normalCount, Integer offCount, Integer errorOffCount, Integer noSingleCount, Integer unBindCount) {
        this.normalCount = normalCount;
        this.offCount = offCount;
        this.errorOffCount = errorOffCount;
        this.noSingleCount = noSingleCount;
        this.unBindCount = unBindCount;
    }

    public Integer getAllStatusCount() {
        return this.normalCount + this.offCount + this.errorOffCount + this.noSingleCount + this.unBindCount;
    }
}
