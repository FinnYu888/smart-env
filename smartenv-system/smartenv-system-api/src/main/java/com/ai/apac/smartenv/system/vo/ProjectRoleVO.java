package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Role;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/29 2:46 下午
 **/
@Data
public class ProjectRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("项目ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;

    @ApiModelProperty("项目编码")
    private String projectCode;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("关联的角色列表")
    private List<Role> roleList;
}
