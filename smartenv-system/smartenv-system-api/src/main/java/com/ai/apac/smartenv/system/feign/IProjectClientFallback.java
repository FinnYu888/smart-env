package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import org.springblade.core.tool.api.R;

import java.util.List;


public class IProjectClientFallback implements IProjectClient {

    @Override
    public R<List<Project>> getALLProject() {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Project> getProjectById(String projectId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<Project>> getProjectByCompany(Long companyId) {
        return R.fail("接收数据失败");
    }

    /**
     * 根据登录帐号查询项目
     * @param accountId
     * @param projectStatus
     * @return
     */
    @Override
    public R<List<ProjectVO>> listProjectByAccountId(Long accountId, Integer projectStatus) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Project> getProjectByTenantId(String tenantId) {
        return R.fail("接收数据失败");
    }

    /**
     * 根据国家城市编码查询项目
     *
     * @param adcode
     * @returnC
     */
    @Override
    public R<List<Project>> getProjectByAdcode(Long adcode) {
        return R.fail("接收数据失败");
    }

    /**
     * 新增帐号和项目的关联关系
     *
     * @param accountId
     * @param projectCodeList
     * @return
     */
    @Override
    public R addAccountProjectRel(Long accountId, List<String> projectCodeList) {
        return R.fail("新增帐号和项目的关联关系失败");
    }

    /**
     * 根据负责人员工ID获取项目信息
     *
     * @param ownerId@return
     */
    @Override
    public R<List<Project>> getProjectByOwner(Long ownerId) {
        return R.fail("接收数据失败");
    }
}
