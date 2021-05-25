package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.service.ICompanyService;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/28 1:20 下午
 **/
@ApiIgnore
@RestController
@AllArgsConstructor
public class CompanyClient implements ICompanyClient{

    private ICompanyService companyService;

    /**
     * 根据公司ID获取公司信息
     *
     * @param companyId 主键
     * @return Menu
     */
    @Override
    @GetMapping(GET_COMPANY_BY_ID)
    public R<Company> getCompanyById(@RequestParam Long companyId) {
        return R.data(companyService.getById(companyId));
    }

    @Override
    public R<List<Company>> getAllCompany() {
        return R.data(companyService.list());
    }
}
