package com.ai.apac.smartenv.omnic.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: StatusCount
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/12  23:35    panfeng          v1.0.0             修改原因
 */
@Data
public class StatusCount implements Serializable {

    //工作中
    Long working;
    //休息
    Long sitBack;
    //离岗(静值)
    Long departure;
    //告警
    Long alarm;
    //加水
    Long waterCnt;
    //加油
    Long oilCnt;
    //休假
    Long vacationCnt;
    //维修中
    Long maintainCnt;
    //未排班
    Long unArrangeCnt;
}
