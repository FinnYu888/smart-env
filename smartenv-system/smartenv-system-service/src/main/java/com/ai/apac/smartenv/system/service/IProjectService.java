package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.dto.ProjectDTO;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * @author qianlong
 * @Description 项目管理Service
 * @Date 2020/11/26 7:49 下午
 **/
public interface IProjectService extends IService<Project> {

    /**
     * 新增项目信息
     *
     * @param projectDTO
     * @return
     */
    boolean addProject(ProjectDTO projectDTO);

    /**
     * 修改项目信息
     *
     * @param projectDTO
     * @return
     */
    boolean updateProject(ProjectDTO projectDTO);

    /**
     * 分页查询
     *
     * @param projectVO
     * @param query
     * @return
     */
    IPage<Project> selectPage(ProjectVO projectVO, Query query);

    /**
     * 不分页查询
     *
     * @param projectVO
     * @param userId
     * @param findChildCity 是否查询子城市的项目 0-不查询 1-查询
     * @return
     */
    List<ProjectVO> listProjectByUser(ProjectVO projectVO, Long userId, Integer findChildCity);

    /**
     * 根据项目编号获取项目详情
     *
     * @param projectId
     * @return
     */
    ProjectVO getProjectByDetail(Long projectId);

    /**
     * 修改项目状态
     *
     * @param projectId
     * @param newStatus
     * @return
     */
    boolean changeProjectStatus(Long projectId, Integer newStatus);

    /**
     * 根据登录帐号ID、项目状态查询关联的项目信息
     *
     * @param accountId
     * @param projectStatus
     * @return
     */
    List<ProjectVO> listProjectByAccountId(Long accountId, Integer projectStatus);

    /**
     * 查询当前登录帐户关联项目的所在城市树
     *
     * @param accountId
     * @return
     */
    List<CityVO> listProjectCity(Long accountId);

    /**
     * 将目前租户信息都转换成项目信息入库(仅供运维使用)
     *
     * @return
     */
    boolean convertTenantToProject();

    /**
     * 将项目所在城市编码与adcode关联起来(仅供运维使用)
     */
    void linkProjectCityToAdcode();

    /**
     * 用户切换项目
     *
     * @param newProjectCode
     * @return R<JSONObject>
     */
    R<JSONObject> switchProject(String newProjectCode);
}
