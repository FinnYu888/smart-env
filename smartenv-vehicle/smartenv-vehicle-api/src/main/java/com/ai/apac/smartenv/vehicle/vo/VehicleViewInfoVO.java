package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;
import java.util.List;

/**
 * 车辆基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleInfoVO对象", description = "车辆360基本信息对象")
public class VehicleViewInfoVO extends TenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 车牌号
     */
    private String plateNumber;


    /**
     * 车头照
     */
    private String mediaURI;

    /**
     * 工作状态
     */
    private Long workStatusId;

    private String workStatus;

    /**
     * 车辆类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long vehicleKindCode;

    private String vehicleKindCodeName;

    private String scheduleBeginTime;

    private String scheduleEndTime;

    private String breaksBeginTime;

    private String breaksEndTime;

    private String workArea;
    
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long vehicleCategoryId;

    /**
     * 车辆类型名称
     */
    private String vehicleCategoryName;

    /**
     * ACC状态
     */
    private Long accStatusId;

    private String accStatus;

    /**
     * 手表状态
     */
    private Long watchStatusId;

    private String watchStatus;

    /**
     * 所属名称ID
     */
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 车辆服役开始时间
     */
    private String entryTime;

    private String quitTime;


    List<VehicleDriverVO> vehicleDriverVOList;


	private String currentFuel;// 当前油量

    private String avgOil;// 百公里油耗


}
