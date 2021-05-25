package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.PublicEventInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PublicEventInfoVO
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/23
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/23  16:00    panfeng          v1.0.0             修改原因
 */
@Data
public class PublicEventInfoVO  extends PublicEventInfo {

    /**
     * 整改前照片
     */
    @ApiModelProperty(value = "整改前照片")
    private List<EventMediumVO> preEventMediumList;

    /**
     * 整改后照片
     */
    @ApiModelProperty(value = "整改后照片")
    private List<EventMediumVO> afterEventMediumList;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventTypeName;

    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件处理状态名称")
    private String eventStatusName;


}
