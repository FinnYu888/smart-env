package com.ai.apac.smartenv.statistics.service;

import com.ai.apac.smartenv.system.vo.ProjectVO;

import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2021/1/15 11:21 上午
 **/
public interface IProjectService {

    /**
     * 根据帐户查询可以访问的项目
     * @param accountId
     * @return
     */
    List<ProjectVO> listProjectByAccountId(Long accountId);

    /**
     * 根据帐户查询可以访问的项目编码
     * @param accountId
     * @return
     */
    List<String> listProjectCodeByAccountId(Long accountId);
}
