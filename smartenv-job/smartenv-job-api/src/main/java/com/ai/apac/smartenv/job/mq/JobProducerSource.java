package com.ai.apac.smartenv.job.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IJobProducerSource
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/6
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/6  14:49    panfeng          v1.0.0             修改原因
 */

public interface JobProducerSource {

    String ARRANGE_BEGIN_OUTPUT="arrange-begin-output";
    String ARRANGE_END_OUTPUT="arrange-end-output";

    @Output(ARRANGE_BEGIN_OUTPUT)
    MessageChannel arrangeBeginOutput();

    @Output(ARRANGE_END_OUTPUT)
    MessageChannel arrangeEndOutput();

}
