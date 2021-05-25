package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: IArrangeClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月22日 下午5:05:39 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月22日     zhaoaj           v1.0.0               修改原因
 */
@FeignClient(value = ApplicationConstant.APPLICATION_OMNIC_NAME, fallback = IRealTimeStatusClientFallback.class)
public interface IArrangeClient {
    String client = "/client";
    String LIST_ARRANGE = client + "/list-arrange";
    String COUNT_ARRANGE = client + "/count-arrange";

    @GetMapping(LIST_ARRANGE)
	R<List<QScheduleObject>> listArrange(QScheduleObjectVO qScheduleObject);

    @GetMapping(COUNT_ARRANGE)
    R<Integer> countArrange(QScheduleObjectVO qScheduleObject);

}
