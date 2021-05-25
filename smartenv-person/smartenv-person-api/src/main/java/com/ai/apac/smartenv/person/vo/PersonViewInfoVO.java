package com.ai.apac.smartenv.person.vo;

import com.ai.apac.smartenv.person.entity.Person;
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
@ApiModel(value = "PersonInfoVO对象", description = "人员360基本信息对象")
public class PersonViewInfoVO extends TenantEntity {
    private static final long serialVersionUID = 1L;

    private String jobNumber;

    private String personName;


    /**
     * 照片
     */
    private String mediaURI;

    /**
     * 工作状态
     */
    private Long workStatusId;


    private String workStatus;

    /**
     * 手表状态
     */
    private Long watchStatusId;

    private String watchStatus;

    /**
     * 所属岗位名称
     */
    private String positionName;

    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personDeptId;

    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long personPositionId;

    private String mobileNumber;

    private String entryTime;


    /**
     * 所属部门名称
     */
    private String deptName;
    private String workArea;
    private String scheduleBeginTime;
    private String scheduleEndTime;
    private String breaksBeginTime;
    private String breaksEndTime;

}
