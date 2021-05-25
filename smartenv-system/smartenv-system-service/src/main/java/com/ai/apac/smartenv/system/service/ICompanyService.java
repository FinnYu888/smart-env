package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.Company;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/11/26 7:49 下午
 **/
public interface ICompanyService extends IService<Company> {

    /**
     * 新增公司信息
     *
     * @param company
     * @return
     */
    boolean saveCompany(Company company);

    /**
     * 变更公司信息
     *
     * @param company
     * @return
     */
    boolean updateCompany(Company company);

    /**
     * 删除公司信息
     *
     * @param companyId
     * @return
     */
    boolean delCompany(Long companyId);

    /**
     * 变更公司状态
     *
     * @param company
     * @param newStatus
     * @return
     */
    boolean changeCompanyStatus(Company company, Integer newStatus);
}
