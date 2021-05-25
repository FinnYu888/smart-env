package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.common.constant.ProjectConstant;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.ai.smartenv.cache.util.SmartCache;
import com.alibaba.excel.util.StringUtils;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/8 10:16 上午
 **/
public class ProjectCache {

    public static IProjectClient projectClient = null;

    private static BladeRedis bladeRedis = null;

    private static IUserClient userClient = null;

    private static IProjectClient getProjectClient() {
        if (projectClient == null) {
            projectClient = SpringUtil.getBean(IProjectClient.class);
        }
        return projectClient;
    }

    private static IUserClient getUserClient() {
        if (userClient == null) {
            userClient = SpringUtil.getBean(IUserClient.class);
        }
        return userClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 重新加载内存数据
     */
    public static void reload() {
        getBladeRedis().del(PROJECT_MAP, PROJECT_CODE_MAP, ACCOUNT_PROJECT_MAP);
        reloadAccountProject();
        R<List<Project>> allProjectResult = getProjectClient().getALLProject();
        if (allProjectResult.isSuccess() && allProjectResult.getData() != null) {
            allProjectResult.getData().stream().forEach(project -> {
                saveProject(project);
            });
        }
    }

    /**
     * 保存项目信息
     *
     * @param project
     */
    public static void saveProject(Project project) {
        SmartCache.hset(PROJECT_MAP, project.getId(), project);
        SmartCache.hset(PROJECT_CODE_MAP, project.getProjectCode(), project);
    }

    /**
     * 删除项目信息
     *
     * @param projectId
     */
    public static void delProjectById(Long projectId) {
        SmartCache.hdel(PROJECT_MAP, projectId);
    }

    /**
     * 删除项目信息
     *
     * @param projectCode
     */
    public static void delProjectByCode(String projectCode) {
        SmartCache.hdel(PROJECT_CODE_MAP, projectCode);
    }

    /**
     * 获取项目详细信息
     *
     * @param projectId
     */
    public static Project getProjectById(Long projectId) {
        return SmartCache.hget(PROJECT_MAP, projectId, () -> {
            R<Project> result = getProjectClient().getProjectById(projectId.toString());
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
    }

    /**
     * 根据项目CODE获取项目详细信息
     *
     * @param projectCode
     */
    public static Project getProjectByCode(String projectCode) {
        return SmartCache.hget(PROJECT_CODE_MAP, projectCode, () -> {
            R<Project> result = getProjectClient().getProjectByTenantId(projectCode);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
    }

    /**
     * 根据项目编码获取项目名称
     * @param projectCode
     * @return
     */
    public static String getProjectNameByCode(String projectCode) {
        Project project = SmartCache.hget(PROJECT_CODE_MAP, projectCode, () -> {
            R<Project> result = getProjectClient().getProjectByTenantId(projectCode);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
        if (project != null && project.getId() != null) {
            return project.getProjectName();
        }
        return null;
    }

    /**
     * 根据帐号查询状态为正常的项目
     *
     * @param accountId
     * @return
     */
    public static List<ProjectVO> listProjectByAccountId(Long accountId) {
        return SmartCache.hget(ACCOUNT_PROJECT_MAP, accountId, () -> {
            R<List<ProjectVO>> result = getProjectClient().listProjectByAccountId(accountId, ProjectConstant.ProjectStatus.Normal);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        });
    }

    /**
     * 从缓存中删除帐号关联的项目
     *
     * @param accountId
     */
    public static void delAccountProject(Long accountId) {
        SmartCache.hdel(ACCOUNT_PROJECT_MAP, accountId);
    }

    /**
     * 清空缓存中所有帐号关联的项目
     */
    public static void reloadAccountProject() {
        getBladeRedis().del(ACCOUNT_PROJECT_MAP);
        R<List<User>> allUserResult = getUserClient().getAllUser();
        if (allUserResult.isSuccess() && allUserResult.getData() != null) {
            List<User> allUser = allUserResult.getData();
            allUser.stream().forEach(user -> {
                Long accountId = user.getId();
                SmartCache.hget(ACCOUNT_PROJECT_MAP, accountId, () -> {
                    R<List<ProjectVO>> result = getProjectClient().listProjectByAccountId(accountId, ProjectConstant.ProjectStatus.Normal);
                    if (result.isSuccess() && result.getData() != null) {
                        return result.getData();
                    }
                    return null;
                });
            });
        }
    }
}
