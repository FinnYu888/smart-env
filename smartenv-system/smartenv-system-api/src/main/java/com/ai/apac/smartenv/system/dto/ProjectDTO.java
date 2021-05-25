package com.ai.apac.smartenv.system.dto;

import com.ai.apac.smartenv.common.dto.AreaNode;
import com.ai.apac.smartenv.system.entity.Project;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qianlong
 * @description ProjectDTO对象
 * @Date 2020/11/30 11:42 上午
 **/
@Data
@ApiModel("ProjectDTO对象")
public class ProjectDTO extends Project {

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    private String ownerName;
    /**
     * 负责人手机
     */
    @ApiModelProperty(value = "负责人手机")
    private String mobile;
    /**
     * 办公室电话
     */
    @ApiModelProperty(value = "办公室电话")
    private String officePhone;
    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty("项目负责人帐号")
    private String adminAccount;

    @ApiModelProperty("项目区域")
    private List<List<AreaNode>> areaNodeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("登录帐号ID")
    private Long accountId;
}
