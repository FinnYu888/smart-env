package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: TransferStationPolyDetailDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/15
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/15     zhaidx           v1.0.0               修改原因
 */
@Data
public class TransferStationPolyDetailDTO extends BasicPolymerizationDetailDTO{
    private static final long serialVersionUID = -2838469618541600840L;

    @ApiModelProperty(value = "中转站名称")
    private String facilityName;

    @ApiModelProperty(value = "中转站规模")
    private String ext1;

    @ApiModelProperty(value = "中转站规模名称")
    private String tranStationModel;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "状态名称")
    private Integer statusName;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "所属单位")
    private String companyCode;

    @ApiModelProperty(value = "垃圾收运量")
    private String garbageWeight;

    @ApiModelProperty(value = "臭味级别")
    private String odorLevel;
    
}
