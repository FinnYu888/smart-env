package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_FACILITY_NAME,
        fallback = IToiletClientFallback.class
)
public interface IToiletClient {

    String API_PREFIX = "/client";
    String TOILET_VO = API_PREFIX + "/toilet_vo";

    String COUNT_ALL_TOILET = API_PREFIX + "/count_all_toilet";
    String TOILET_VO_BY_CONDITION = API_PREFIX + "/toilet_vo_by_condition";
	String TOILET_ALL_LIST = API_PREFIX + "/toilet_all_list";
    
    @GetMapping(TOILET_VO)
    R<ToiletInfoVO> getToilet(@RequestParam("id") Long id);

    @GetMapping(COUNT_ALL_TOILET)
    R<Integer> countAllToilet(@RequestParam("tenantId") String tenantId);

    @PostMapping(TOILET_VO_BY_CONDITION)
    R<List<ToiletInfoVO>> listToiletVOByCondition(@RequestBody ToiletQueryDTO queryDTO);

    @GetMapping(TOILET_ALL_LIST)
	R<List<ToiletInfoVO>> listToiletAll();
}