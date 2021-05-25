package com.ai.apac.smartenv.vehicle.vo;

import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springblade.core.tool.node.INode;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleNode
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/11
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/11  16:35    panfeng          v1.0.0             修改原因
 */
@Data
public class VehicleNode {

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String nodeName;

    private List<VehicleNode> subNodes;

    private Boolean showFlag;
    private Boolean isLastNode;
    private Boolean isValid;// 是否有效，失效置灰
    private Boolean isVehicle;

    //1在岗 2 离岗 3  休息
    private Integer status;
    private String statusName;



    private String vehicleType;


}
