package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.common.constant.CompanyConstant;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.system.cache.CompanyCache;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.mapper.CompanyMapper;
import com.ai.apac.smartenv.system.service.ICompanyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author qianlong
 * @description 公司相关服务
 * @Date 2020/11/26 7:50 下午
 **/
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements ICompanyService {


    /**
     * 基本校验
     *
     * @param company
     */
    private void basicValid(Company company) {
        if (StringUtils.isEmpty(company.getFullName())) {
            throw new ServiceException("公司名称不能为空");
        }
        company.setFullName(company.getFullName().trim());
        if(company.getFullName().length() > 20){
            throw new ServiceException("公司名称不能超过20个字符");
        }
        if (company.getCityId() == null) {
            throw new ServiceException("所在城市不能为空");
        }
        if (StringUtils.isEmpty(company.getOwnerName())) {
            throw new ServiceException("负责人姓名不能为空");
        }
        if (StringUtils.isEmpty(company.getMobile())) {
            throw new ServiceException("负责人电话不能为空");
        }
    }

    /**
     * 新增公司信息
     *
     * @param company
     * @return
     */
    @Override
    public boolean saveCompany(Company company) {
        //基本校验
        this.basicValid(company);
        //校验公司名称是否重复
        Integer companyCount = baseMapper.selectCount(new LambdaQueryWrapper<Company>().eq(Company::getFullName, company.getFullName())
                .eq(Company::getStatus, CompanyConstant.CompanyStatus.Normal));
        if (companyCount > 0) {
            throw new ServiceException("公司名称不能为重复");
        }
        company.setCreateTime(new Date());
        company.setUpdateTime(new Date());
        company.setStatus(CompanyConstant.CompanyStatus.Normal);
        company.setIsDeleted(0);
        baseMapper.insert(company);
        CompanyCache.saveCompany(company);
        return true;
    }

    /**
     * 变更公司信息
     *
     * @param company
     * @return
     */
    @Override
    public boolean updateCompany(Company company) {
        //基本校验
        this.basicValid(company);
        //校验公司名称是否重复
        Integer companyCount = baseMapper.selectCount(new LambdaQueryWrapper<Company>().eq(Company::getFullName, company.getFullName())
                .eq(Company::getStatus, CompanyConstant.CompanyStatus.Normal).ne(Company::getId, company.getId()));
        if (companyCount > 0) {
            throw new ServiceException("公司名称不能为重复");
        }
        company.setUpdateTime(new Date());
        company.setIsDeleted(0);
        baseMapper.updateById(company);
        CompanyCache.delCompany(company.getId());
        return true;
    }

    /**
     * 删除公司信息
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean delCompany(Long companyId) {
        return false;
    }

    /**
     * 变更公司状态
     *
     * @param company
     * @param newStatus
     * @return
     */
    @Override
    public boolean changeCompanyStatus(Company company, Integer newStatus) {
        if (newStatus.intValue() < CompanyConstant.CompanyStatus.Normal.intValue() || newStatus.intValue() > CompanyConstant.CompanyStatus.Lock.intValue()) {
            throw new ServiceException("要更新的状态不正确");
        }
        Company companyTmp = baseMapper.selectById(company.getId());
        if (companyTmp == null) {
            throw new ServiceException("公司不存在");
        }
        companyTmp.setStatus(newStatus);
        companyTmp.setUpdateTime(new Date());
        companyTmp.setUpdateUser(company.getUpdateUser());
        //TODO 公司状态变更,需要通知公司现有负责人

        //TODO 公司状态变更为锁定,则所有项目状态也跟着变成锁定
        if (newStatus.equals(CompanyConstant.CompanyStatus.Lock)) {

        }
        baseMapper.updateById(companyTmp);
        CompanyCache.delCompany(companyTmp.getId());
        return true;
    }
}
