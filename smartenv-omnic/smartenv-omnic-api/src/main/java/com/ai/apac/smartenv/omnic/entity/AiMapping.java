package com.ai.apac.smartenv.omnic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 组信息表实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@TableName("ai_mapping")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Mapping对象", description = "编码映射信息表")
public class AiMapping extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;

    /**
     * 亚信智慧环卫云平台业务表CODE
     */
    @ApiModelProperty(value = "亚信智慧环卫云平台业务表CODE")
    private String sscpCode;

    /**
     * 第三方平台业务表CODE
     */
    @ApiModelProperty(value = "第三方平台业务表CODE")
    private String thirdCode;

    /**
     * 编码类型
     */
    @ApiModelProperty(value = "编码类型")
    private Integer codeType;


}
