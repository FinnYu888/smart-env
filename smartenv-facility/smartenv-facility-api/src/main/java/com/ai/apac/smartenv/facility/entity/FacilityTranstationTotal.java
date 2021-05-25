package com.ai.apac.smartenv.facility.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

@Data
@TableName("ai_facility_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityTranstationDetail对象", description = "FacilityTranstationDetail对象")
public class FacilityTranstationTotal extends TenantEntity {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @TableField("FACILITY_NAME")
    private String facilityName;
    @TableField("FACILITY_TYPE")
    private String facilityType;
    @TableField("LNG")
    private String lng;
    @TableField("LAT")
    private String lat;
    @TableField("LOCATION")
    private String location;
    @TableField("PHONE")
    private String phone;
    @TableField("COMPANY_CODE")
    private String companyCode;
    @TableField("DIRECTOR")
    private String director;
    @TableField("EXT1")
    private String ext1;
    @TableField("EXT2")
    private String ext2;
    @TableField("EXT3")
    private String ext3;
    @TableField("FACILTY_VOLUME")
    private String faciltyVolume;
    @TableField("FACILTY_AREA")
    private String faciltyArea;
    @TableField("FACILTY_USE_DATE")
    private String faciltyUseDate;
    @TableField("FACILTY_GPB")
    private String faciltyGpb;
    @TableField("PROJECT_NO")
    private String projectNo;
    @TableField("CREATE_TIME")
    private Timestamp createTime;
    @TableField("DONE_DATE")
    private Timestamp doneDate;
    @TableField("DONE_CODE")
    private String doneCode;
    @TableField("OP_ID")
    private String opId;
    @TableField("ORG_ID")
    private String orgId;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("STATUS")
    private Integer status;
    @TableField("GARBAGE_WEIGHT")
    private String garbageWeight;
    @TableField("TREANSFER_TIMES")
    private String transferTimes;
}
