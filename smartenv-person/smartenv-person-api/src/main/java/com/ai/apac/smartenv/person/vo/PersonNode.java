package com.ai.apac.smartenv.person.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PersonNode
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/15
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/15  16:48    panfeng          v1.0.0             修改原因
 */
@Data
public class PersonNode {


    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String nodeName;
    private String jobNumber;

    private List<PersonNode> subNodes;

    private Boolean showFlag;
    private Boolean isLastNode;
    private Boolean isPerson;
    private Boolean isValid;

    //1在岗 2 离岗 3  休息
    private Integer status;
    private String statusName;

}
