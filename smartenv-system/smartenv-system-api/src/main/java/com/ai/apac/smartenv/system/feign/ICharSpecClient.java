package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.CharSpec;
import com.ai.apac.smartenv.system.entity.CharSpecValue;
import com.ai.apac.smartenv.system.vo.CharSpecVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ICharSpecClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/2/7
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/2/7     zhaidx           v1.0.0               修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = ICharSpecFallbackClient.class
)
public interface ICharSpecClient {
    
    String API_PREFIX = "/client";
    String CHAR_SPEC = API_PREFIX + "/char-spec";
    String CHAR_SPEC_VALUE = API_PREFIX + "/char-spec-value";
    String CHAR_SPECS = API_PREFIX + "/char-specs";

    /**
     * 根据扩展属性主键id获取扩展属性
     *
     * @param id 主键
     * @return Menu
     */
    @GetMapping(CHAR_SPEC)
    R<CharSpec> getCharSpecById(@RequestParam("id")  Long id);

    @GetMapping(CHAR_SPEC_VALUE)
    R<CharSpecValue> getCharSpecValue(@RequestParam("charSpecId") Long charSpecId,@RequestParam("value") String value);

    /**
     * 根据实体类型Id获取扩展属性
     * @param entityCategoryId
     * @return
     */
    @GetMapping(CHAR_SPECS)
    R<List<CharSpecVO>> listCharSpecByEntityCategoryId(@NotEmpty @RequestParam String entityCategoryId);
}
