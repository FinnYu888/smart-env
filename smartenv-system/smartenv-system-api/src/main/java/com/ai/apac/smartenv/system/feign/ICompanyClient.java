package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.Company;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/28 1:10 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = ICompanyFallback.class
)
public interface ICompanyClient {

    String API_PREFIX = "/client";
    String GET_COMPANY_BY_ID = API_PREFIX + "/company";
    String GET_ALL_COMPANY = API_PREFIX + "/all-company";

    /**
     * 根据公司ID获取公司信息
     *
     * @param companyId 主键
     * @return Menu
     */
    @GetMapping(GET_COMPANY_BY_ID)
    R<Company> getCompanyById(@RequestParam Long companyId);

    /**
     * 获取所有公司信息
     * @return
     */
    @GetMapping(GET_ALL_COMPANY)
    R<List<Company>> getAllCompany();
}
