package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.MappingVO;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IArrangeClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020年11月30日 下午5:05:39
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年11月30日     zhanglei25           v1.0.0               修改原因
 */
@FeignClient(value = ApplicationConstant.APPLICATION_OMNIC_NAME, fallback = IRealTimeStatusClientFallback.class)
public interface IMappingClient {
    String client = "/client";
    String GET_SSCP_CODE_BY_THIRD_CODE = client + "/get-sscpcode-by-thirdcode";

    String SAVE_MAPPING_CODE = client + "/save-mapping-code";

    String DEL_MAPPING_CODE = client + "/del-mapping-code";


    @PostMapping(GET_SSCP_CODE_BY_THIRD_CODE)
    R<AiMapping> getSscpCodeByThirdCode(@RequestBody AiMapping mapping);

    @DeleteMapping(DEL_MAPPING_CODE)
    R<Boolean> delMapping(@RequestParam String sscpCode,@RequestParam Integer codeType);

    @PostMapping(SAVE_MAPPING_CODE)
    R<Boolean> saveMappingCode(@RequestBody AiMapping mapping);


}
