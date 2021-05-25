package com.ai.apac.smartenv.vehicle.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * 驾驶员视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "驾驶员VO对象", description = "驾驶员VO对象")
public class VehicleDriverVO extends TenantEntity {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long relId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 驾驶员名称（如果有多个驾驶员 取第一个）
     */
    private String personName;

    private String jobNumber;

    private Long driverNumber;


    /**
     * 驾驶员ID（如果有多个驾驶员 取第一个）
     */
    private String mobileNumber;

    private Long personDeptId;

    private String personDeptName;

    private String personPositionName;

}
