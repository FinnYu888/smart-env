package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author qianlong
 */
@Data
@TableName("ai_station")
@ApiModel(value = "Station对象", description = "Station对象")
public class Station extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "父主键")
    private Long parentId;

    @ApiModelProperty(value = "岗位级别,从1-4,数字越大,级别越高")
    private Integer stationLevel;

    /**
     * 机构名
     */
    @ApiModelProperty(value = "岗位名称")
    private String stationName;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
