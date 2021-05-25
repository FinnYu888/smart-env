package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.system.entity.AccountProjectRel;
import com.ai.apac.smartenv.system.mapper.AccountProjectRelMapper;
import com.ai.apac.smartenv.system.service.IAccountProjectRelService;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author qianlong
 * @description 帐号与项目关联关系相关服务
 * @Date 2020/11/26 7:50 下午
 **/
@Service
public class AccountProjectRelService extends ServiceImpl<AccountProjectRelMapper, AccountProjectRel> implements IAccountProjectRelService {

    /**
     * 将帐户和多个项目关联
     *
     * @param accountId
     * @param projectCodeList
     */
    @Override
    public void batchCreateRel(Long accountId, List<String> projectCodeList) {
        if (accountId == null || accountId < 0) {
            throw new ServiceException("帐户ID不能为空");
        }
        User user = UserCache.getUser(accountId);
        if (user == null || user.getId() == null) {
            throw new ServiceException("帐户不存在");
        }
        if (CollUtil.isEmpty(projectCodeList)) {
            return;
        }
        projectCodeList.stream().forEach(projectCode -> {
            int count = baseMapper.selectCount(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getAccountId, accountId).eq(AccountProjectRel::getProjectCode, projectCode));
            if (count <= 0 && org.apache.commons.lang3.StringUtils.isNotEmpty(projectCode)) {
                AccountProjectRel accountProjectRel = new AccountProjectRel();
                accountProjectRel.setAccount(user.getAccount());
                accountProjectRel.setAccountId(accountId);
                accountProjectRel.setProjectCode(projectCode);
                accountProjectRel.setIsDeleted(0);
                accountProjectRel.setStatus(1);
                accountProjectRel.setCreateTime(new Date());
                accountProjectRel.setUpdateTime(new Date());
                baseMapper.insert(accountProjectRel);
            }
        });
    }

    /**
     * 将帐户和多个项目关联
     *
     * @param accountId
     * @param projectCodes
     */
    @Override
    public void batchCreateRel(Long accountId, String projectCodes) {
        if (StringUtils.isEmpty(projectCodes)) {
            return;
        }
        List<String> projectCodeList = Func.toStrList(projectCodes);
        this.batchCreateRel(accountId, projectCodeList);
    }
}
