package com.ai.apac.smartenv.system.feign;

import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.service.IAccountProjectRelService;
import com.ai.apac.smartenv.system.service.IProjectService;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/8 10:12 上午
 **/
@ApiIgnore
@RestController
@AllArgsConstructor
public class ProjectClient implements IProjectClient {

    private IProjectService projectService;

    private IAccountProjectRelService accountProjectRelService;

    @Override
    @GetMapping(GET_ALL_PROJECT)
    public R<List<Project>> getALLProject() {
        return R.data(projectService.list());
    }

    @Override
    @GetMapping(GET_PROJECT_BY_ID)
    public R<Project> getProjectById(@RequestParam String projectId) {
        Project project = projectService.getById(projectId);
        if (ObjectUtil.isEmpty(project) || ObjectUtil.isEmpty(project.getId())) {
            project = projectService.getOne(new QueryWrapper<Project>().lambda().eq(Project::getProjectCode, projectId));
        }
        return R.data(project);
    }

    @Override
    @GetMapping(GET_PROJECT_BY_COMPANY)
    public R<List<Project>> getProjectByCompany(Long companyId) {
        return R.data(projectService.list(new LambdaQueryWrapper<Project>().eq(Project::getCompanyId, companyId)));
    }

    /**
     * 根据登录帐号查询项目
     *
     * @param accountId
     * @param projectStatus
     * @return
     */
    @Override
    @GetMapping(GET_PROJECT_BY_ACCOUNT)
    public R<List<ProjectVO>> listProjectByAccountId(@RequestParam Long accountId, @RequestParam Integer projectStatus) {
        return R.data(projectService.listProjectByAccountId(accountId, projectStatus));
    }


    @Override
    @GetMapping(GET_PROJECT_BY_TENANT_ID)
    public R<Project> getProjectByTenantId(@RequestParam String tenantId) {
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, tenantId));
        return R.data(project);
    }

    /**
     * 根据国家城市编码查询项目
     *
     * @param adcode
     * @returnC
     */
    @Override
    @GetMapping(GET_PROJECT_BY_ADCODE)
    public R<List<Project>> getProjectByAdcode(@RequestParam Long adcode) {
        return R.data(projectService.list(new LambdaQueryWrapper<Project>().eq(Project::getAdcode, adcode)));
    }

    /**
     * 新增帐号和项目的关联关系
     *
     * @param accountId
     * @param projectCodeList
     * @return
     */
    @Override
    @PostMapping(ADD_ACCOUNT_PROJECT_REL)
    public R addAccountProjectRel(@PathVariable Long accountId, @RequestBody List<String> projectCodeList) {
        accountProjectRelService.batchCreateRel(accountId, projectCodeList);
        return R.status(true);
    }

    /**
     * 根据负责人员工ID获取项目信息
     *
     * @param ownerId@return
     */
    @Override
    @GetMapping(GET_PROJECT_BY_OWNER)
    public R<List<Project>> getProjectByOwner(@RequestParam Long ownerId) {
        List<Project> projectList = projectService.list(new LambdaQueryWrapper<Project>().eq(Project::getOwnerId, ownerId));
        return R.data(projectList);
    }
}
