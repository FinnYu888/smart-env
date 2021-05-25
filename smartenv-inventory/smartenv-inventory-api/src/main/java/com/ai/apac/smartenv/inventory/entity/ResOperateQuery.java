package com.ai.apac.smartenv.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.sql.Timestamp;
import java.util.Date;

@Data
@ApiModel(value = "查询资源操作对象", description = "查询资源操作对象")
public class ResOperateQuery extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resType;
    private String serialNumber;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long batchId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resSpec;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryId;
    private String reservationRecipientType;
    private String reservationRecipient;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relOrdId;
    private Timestamp reservedExpireDate;
    private Integer amount;
    private Timestamp usedTime;
    private String operateType;
    private String operateState;
    private String unitPrice;
    private String purchasingAgent;
    private Timestamp purchasingDate;
    private String manageStateReasonDesc;
    private String remark;
    private String typeName;
    private String  specName;
    private String unit;
    private String inventoryName;
    private String operateTypeName;
    private String resourceSource;
    private String resourceSourceName;
}
