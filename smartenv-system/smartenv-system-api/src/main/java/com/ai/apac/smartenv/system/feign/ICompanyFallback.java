package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author qianlong
 */
@Component
public class ICompanyFallback implements ICompanyClient {

    /**
     * 根据公司ID获取公司信息
     *
     * @param companyId 主键
     * @return Menu
     */
    @Override
    public R<Company> getCompanyById(Long companyId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Company>> getAllCompany() {
        return R.fail("获取数据失败");
    }
}
