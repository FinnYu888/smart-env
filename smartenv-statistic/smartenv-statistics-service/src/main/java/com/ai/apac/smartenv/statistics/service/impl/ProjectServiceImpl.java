package com.ai.apac.smartenv.statistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.statistics.service.IProjectService;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2021/1/15 11:27 上午
 **/
@Service
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private IProjectClient projectClient;

    /**
     * 根据帐户查询可以访问的项目
     *
     * @param accountId
     * @return
     */
    @Override
    public List<ProjectVO> listProjectByAccountId(Long accountId) {
        R<List<ProjectVO>> result = projectClient.listProjectByAccountId(accountId, 1);
        if (result.isSuccess() && result.getData() != null) {
            List<ProjectVO> projectVOList = result.getData();
            return projectVOList;
        }
        return null;
    }

    /**
     * 根据帐户查询可以访问的项目编码
     *
     * @param accountId
     * @return
     */
    @Override
    public List<String> listProjectCodeByAccountId(Long accountId) {
        List<ProjectVO> projectVOList = this.listProjectByAccountId(accountId);
        if(CollUtil.isNotEmpty(projectVOList)){
            return projectVOList.stream().map(projectVO -> {
                return projectVO.getProjectCode();
            }).collect(Collectors.toList());
        }
        return null;
    }
}
