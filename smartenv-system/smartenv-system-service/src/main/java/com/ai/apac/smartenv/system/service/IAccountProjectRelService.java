package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.AccountProjectRel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/11/26 7:49 下午
 **/
public interface IAccountProjectRelService extends IService<AccountProjectRel> {

    /**
     * 将帐户和多个项目关联
     * @param accountId
     * @param projectCodeList
     */
    void batchCreateRel(Long accountId, List<String> projectCodeList);

    /**
     * 将帐户和多个项目关联
     * @param accountId
     * @param projectCodes
     */
    void batchCreateRel(Long accountId, String projectCodes);
}
