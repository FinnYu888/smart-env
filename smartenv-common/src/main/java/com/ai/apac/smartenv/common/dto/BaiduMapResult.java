package com.ai.apac.smartenv.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaiduMapResult
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/21  11:06    panfeng          v1.0.0             修改原因
 */
@Data
public abstract class BaiduMapResult implements Serializable {

    private Integer status;

    private String message;




}
