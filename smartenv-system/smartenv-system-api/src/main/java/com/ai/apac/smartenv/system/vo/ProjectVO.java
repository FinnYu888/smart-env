package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.common.dto.AreaNode;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.ProjectArea;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author qianlong
 * @description Project视图
 * @Date 2020/11/29 10:29 下午
 **/
@Data
public class ProjectVO extends Project {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("负责人姓名")
    private String ownerName;

    @ApiModelProperty("所属公司名称")
    private String companyName;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("联系手机")
    private String mobile;

    @ApiModelProperty("联系人邮箱")
    private String email;

    @ApiModelProperty("项目类型")
    private String projectTypeName;

    @ApiModelProperty("负责人帐号")
    private String adminAccount;

    @ApiModelProperty("是否是默认项目")
    private Integer isDefault;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("登录帐号ID")
    private Long accountId;

    @ApiModelProperty("项目区域")
    private List<List<AreaNode>> areaNodeList;

    @ApiModelProperty("完整的城市层级关系")
    private List<String> cityFullId;

    @ApiModelProperty("上级公司ID")
    private Long parentCompanyId;

    @ApiModelProperty("上级公司名称")
    private String parentCompanyName;

    @ApiModelProperty("完整的公司层级关系")
    private List<String> companyFullId;
}
