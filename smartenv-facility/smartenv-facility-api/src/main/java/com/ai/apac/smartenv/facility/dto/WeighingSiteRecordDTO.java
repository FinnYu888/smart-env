package com.ai.apac.smartenv.facility.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteRecordDTO
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/3  16:04    panfeng          v1.0.0             修改原因
 */
@Data
@Document(collection = "WeighingSiteRecord")
public class WeighingSiteRecordDTO {
    private Long weighingSiteId;// 称重站ID
    private String weighingRecordId; // 称重单号
    private String freightName; // 货物名称
    private String freightSpec; // 称重规格
    private Date   grossWeightTime; // 毛重时间 YYYY-MM-DD HH:mm:ss
    private Double grossWeight; // 毛重
    private String   tareTime; // 皮重时间
    private Double tare; // 皮重
    private Double actualWeight; // 实重
    private Date   weighingTime; // 称重时间
    private Double deduction; // 扣除
    private Double netWeight; // 净重
    private String radioFrequencyCardNo; // 射频卡号
    private String shipper; // 发货单位
    private String receivingUnit; // 收货单位
    private String transportUnit; // 运输单位
    private String plateNumber; // 车牌号
    private String drivingLicenseNumber; // 行驶证号
    private String driver; // 司机
    private String remark; // 称重单号
    private String weighman; // 司磅员
    private String weighingSiteNo; // 称重站号

    private String companyId;// 公司ID

}
