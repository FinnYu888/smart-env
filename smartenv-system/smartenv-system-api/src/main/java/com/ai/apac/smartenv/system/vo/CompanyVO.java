package com.ai.apac.smartenv.system.vo;

import com.ai.apac.smartenv.system.entity.Company;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description CompanyVO
 * @Date 2020/11/27 6:00 下午
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CompanyVO对象", description = "CompanyVO对象")
public class CompanyVO extends Company implements INode {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 状态名称
     */
    @ApiModelProperty("状态名称")
    private String statusName;

    /**
     * 城市名称
     */
    @ApiModelProperty("城市名称")
    private String cityName;

    /**
     * 城市ID列表,如果是省市两级,第一个元素就是省,第二个是市
     */
    @ApiModelProperty("城市ID列表")
    private List<String> cityFullId;

    /**
     * 上级城市名称
     */
    @ApiModelProperty("城市规模名称")
    private String parentCompanyName;

    /**
     * 城市规模名称
     */
    @ApiModelProperty("城市规模名称")
    private String companySizeName;

    @ApiModelProperty("完整的营业执照路径")
    private String fullBusinessLicenseUrl;

    /**
     * 子孙节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<INode> children;

    @Override
    public List<INode> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }
}
