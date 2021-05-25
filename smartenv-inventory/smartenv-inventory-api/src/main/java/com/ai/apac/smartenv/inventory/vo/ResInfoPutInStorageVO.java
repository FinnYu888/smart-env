package com.ai.apac.smartenv.inventory.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "资源入库请求对象", description = "资源入库请求对象")
public class ResInfoPutInStorageVO extends TenantEntity implements Serializable {
    @ApiModelProperty(value = "采购人")
    private String purchasingAgent;
    @ApiModelProperty(value = "采购时间")
    private Date purchasingDate;
    @ApiModelProperty(value = "采购人")
    private String purchasingAgentId;
    @ApiModelProperty(value = "仓库id")
    private String storageId;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "物资来源")
    private String resourceSource;
    @ApiModelProperty(value = "物资列表")
    List<ResInfoVO> resInfoList;
}
