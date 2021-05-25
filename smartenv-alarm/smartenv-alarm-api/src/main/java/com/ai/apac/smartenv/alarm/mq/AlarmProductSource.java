package com.ai.apac.smartenv.alarm.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmProductSource
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/21  09:32    panfeng          v1.0.0             修改原因
 */
public interface AlarmProductSource {

    String POLYMERIZATION_VEHICLE_CHANGE_OUTPUT = "polymerization_vehicle_change_output";

    String POLYMERIZATION_PERSON_CHANGE_OUTPUT = "polymerization_person_change_output";


    @Output(POLYMERIZATION_VEHICLE_CHANGE_OUTPUT)
    MessageChannel polymerizationVehicleChangeOutput();

    @Output(POLYMERIZATION_PERSON_CHANGE_OUTPUT)
    MessageChannel polymerizationPersonChangeOutput();


}
