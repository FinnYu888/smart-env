package com.ai.apac.smartenv.omnic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xubr
 * @Description:
 * @Company: AsiaInfo International LTD.
 * @Date: Created at 2020/7/28 15:38.
 * <p>Modification History:
 * <p>Date          Author        Version        Description
 * <p>---------------------------------------------------------
 * <p>2020/7/28      xubr           1.0          first version
 */
@Data
@ApiModel(value = "大屏统一查询条件对象", description = "大屏统一查询条件对象")
public class AllQuery4BSVO implements Serializable {
    private static final long serialVersionUID = 2144179115971058647L;
    @ApiModelProperty("区域列表")
    List<BasicInfo4BSVO> regionList;
    @ApiModelProperty("车辆类型")
    List<BasicInfo4BSVO> vehicleType;
    @ApiModelProperty("车辆状态")
    List<BasicInfo4BSVO> vehicleState;
    @ApiModelProperty("车辆在线/离线")
    List<BasicInfo4BSVO> vehicleAccStatus;
    @ApiModelProperty("人员岗位")
    List<BasicInfo4BSVO> personPosition;
    @ApiModelProperty("人员状态")
    List<BasicInfo4BSVO> personState;
    @ApiModelProperty("人员手表状态")
    List<BasicInfo4BSVO> personWatchStatus;
    @ApiModelProperty("中转站规模")
    List<BasicInfo4BSVO> facilityType;
    @ApiModelProperty("中转站状态")
    List<BasicInfo4BSVO> facilityState;
    @ApiModelProperty("垃圾桶种类")
    List<BasicInfo4BSVO> ashcanType;
    @ApiModelProperty("垃圾桶工作状态")
    List<BasicInfo4BSVO> ashcanWorkState;
    @ApiModelProperty("垃圾桶状态")
    List<BasicInfo4BSVO> ashcanState;
    @ApiModelProperty("事件状态")
    List<BasicInfo4BSVO> eventState;
    @ApiModelProperty("事件等级")
    List<BasicInfo4BSVO> eventLevel;
    @ApiModelProperty("公厕等级")
    List<BasicInfo4BSVO> wcLevel;
    @ApiModelProperty("公厕状态")
    List<BasicInfo4BSVO> wcState;
}
