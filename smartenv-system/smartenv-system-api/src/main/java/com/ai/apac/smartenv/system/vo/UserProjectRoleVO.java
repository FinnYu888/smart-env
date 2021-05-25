package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Role;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author qianlong
 * @description 当前用户关联的项目及角色
 * @Date 2020/12/27 8:40 下午
 **/
@Data
@ApiModel("当前用户关联的项目及角色VO")
public class UserProjectRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

//    @ApiModelProperty("用户目前关联的项目角色")
//    private LinkedHashMap<Project,List<Role>> userProjectRoleMap;

    @ApiModelProperty("用户目前关联的项目角色")
    private List<ProjectRoleVO> userProjectRoleList;

    @ApiModelProperty("系统中所有项目角色")
    private List<Project> allProjectList;
}
