package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.dto.WeighingSiteRecordDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteRocordView
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/8
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/8  19:53    panfeng          v1.0.0             修改原因
 */
@Data
public class WeighingSiteRocordVO  extends WeighingSiteRecordDTO {
    public Date startTime;
    public Date endTime;


}
