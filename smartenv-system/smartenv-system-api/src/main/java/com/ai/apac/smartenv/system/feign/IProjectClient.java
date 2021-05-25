package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/12/8 9:59 上午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = IProjectClientFallback.class
)
public interface IProjectClient {

    String API_PREFIX = "/projectClient";
    String GET_ALL_PROJECT = API_PREFIX + "/getAllProject";
    String GET_PROJECT_BY_ID = API_PREFIX + "/getProjectById";
    String GET_PROJECT_BY_COMPANY = API_PREFIX + "/getProjectByCompanyId";
    String GET_PROJECT_BY_ACCOUNT = API_PREFIX + "/getProjectByAccountId";
    String GET_PROJECT_BY_TENANT_ID = API_PREFIX + "/getProjectByTenantId";
    String GET_PROJECT_BY_ADCODE = API_PREFIX + "/getProjectByAdcode";
    String ADD_ACCOUNT_PROJECT_REL = API_PREFIX + "/addAccountProjectRel/{accountId}";
    String GET_PROJECT_BY_OWNER = API_PREFIX + "/getProjectByOwnerId";

    @GetMapping(GET_ALL_PROJECT)
    R<List<Project>> getALLProject();

    /**
     * 根据项目ID查询项目详情
     *
     * @param projectId
     * @return
     */
    @GetMapping(GET_PROJECT_BY_ID)
    R<Project> getProjectById(@RequestParam String projectId);

    /**
     * 根据公司ID查询项目
     *
     * @param companyId
     * @return
     */
    @GetMapping(GET_PROJECT_BY_COMPANY)
    R<List<Project>> getProjectByCompany(@RequestParam Long companyId);

    /**
     * 根据登录帐号查询项目
     *
     * @param accountId
     * @param projectStatus
     * @return
     */
    @GetMapping(GET_PROJECT_BY_ACCOUNT)
    R<List<ProjectVO>> listProjectByAccountId(@RequestParam Long accountId, @RequestParam Integer projectStatus);

    /**
     * 根据租户ID获取项目信息
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_PROJECT_BY_TENANT_ID)
    R<Project> getProjectByTenantId(@RequestParam String tenantId);

    /**
     * 根据国家城市编码查询项目
     *
     * @param adcode
     * @returnC
     */
    @GetMapping(GET_PROJECT_BY_ADCODE)
    R<List<Project>> getProjectByAdcode(@RequestParam Long adcode);

    /**
     * 新增帐号和项目的关联关系
     *
     * @param accountId
     * @param projectCodeList
     * @return
     */
    @PostMapping(ADD_ACCOUNT_PROJECT_REL)
    R addAccountProjectRel(@PathVariable Long accountId, @RequestBody List<String> projectCodeList);

    /**
     * 根据负责人员工ID获取项目信息
     *
     * @param ownerId
     * @return
     */
    @GetMapping(GET_PROJECT_BY_OWNER)
    R<List<Project>> getProjectByOwner(@RequestParam Long ownerId);
}
