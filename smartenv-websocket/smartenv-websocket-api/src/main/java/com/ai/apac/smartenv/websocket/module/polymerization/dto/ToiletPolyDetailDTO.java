package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ToiletPolyDetailDTO
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/18     zhaidx           v1.0.0               修改原因
 */
@Data
public class ToiletPolyDetailDTO extends BasicPolymerizationDetailDTO {
    private static final long serialVersionUID = 4205872837618671644L;

    @ApiModelProperty(value = "公厕名称")
    private String toiletName;
    @ApiModelProperty(value = "公厕编码")
    private String toiletCode;
    @ApiModelProperty(value = "公厕级别")
    private String toiletLevel;
    @ApiModelProperty(value = "公厕头像")
    private String toiletImage;
    @ApiModelProperty(value = "公厕联系号码")
    private String phoneNumber;
    @ApiModelProperty(value = "公厕PM值")
    private String toiletPm;
    @ApiModelProperty(value = "公厕清洁分")
    private String clearPoint;
    @ApiModelProperty(value = "公厕清洁描述")
    private String clearDesc;
    @ApiModelProperty(value = "公厕负责人")
    private Long chargePersonId;
    @ApiModelProperty(value = "公厕是否支持安装终端")
    private String supportDevice;
    @ApiModelProperty(value = "所属单位")
    private String companyCode;
    @ApiModelProperty(value = "所属部门")
    private Long deptId;
    @ApiModelProperty(value = "所属路线/区域")
    private Long workareaId;
    @ApiModelProperty(value = "所属片区")
    private Long regionId;
    @ApiModelProperty(value = "经度")
    private String lng;
    @ApiModelProperty(value = "纬度")
    private String lat;
    @ApiModelProperty(value = "公厕地址")
    private String location;
    @ApiModelProperty(value = "公厕详细地址")
    private String detailLocation;
    @ApiModelProperty(value = "公厕工作状态：正常，关闭，临时关闭")
    private String workStatus;
    @ApiModelProperty(value = "公厕二维码")
    private String toiletQrCode;
    @ApiModelProperty(value = "公厕负责人")
    private String chargePersonName;
    @ApiModelProperty(value = "公厕级别名称")
    private String toiletLevelName;
    @ApiModelProperty(value = "公厕状态名称")
    private String workStatusName;
    @ApiModelProperty(value = "所属单位名称")
    private String companyName;
    @ApiModelProperty(value = "所属部门名称")
    private String deptName;
    @ApiModelProperty(value = "所属业务区域名称")
    private String regionName;
    @ApiModelProperty(value = "男厕便池数")
    private int manAQuotaCount;
    @ApiModelProperty(value = "男厕坑位数")
    private int manBQuotaCount;
    @ApiModelProperty(value = "女厕坑位数")
    private int womanBQuotaCount;
    @ApiModelProperty(value = "母婴厕位数")
    private int momQuotaCount;
    @ApiModelProperty(value = "无障碍厕位")
    private int barrierFreeQuotaCount;
    @ApiModelProperty(value = "总厕位")
    private int totalQuotaCount;
}
