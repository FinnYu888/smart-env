package com.ai.apac.smartenv.person.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 部门员工数量统计
 * @Date 2020/5/18 9:01 下午
 **/
@Data
public class DeptStaffCountDTO implements Serializable {

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("员工数量")
    private Integer count;
}
