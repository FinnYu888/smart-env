package com.ai.apac.smartenv.facility.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ToiletQueryDTO
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
public class ToiletQueryDTO implements Serializable {
    private static final long serialVersionUID = -1624415449740004019L;

    private List<Long> statuses;

    private List<Long> levels;

    private List<Long> regionIds;
    
    private String tenantId;
    
    private String toiletName;
}
