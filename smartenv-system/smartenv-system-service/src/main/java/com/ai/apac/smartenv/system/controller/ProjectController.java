package com.ai.apac.smartenv.system.controller;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.dto.ProjectDTO;
import com.ai.apac.smartenv.system.dto.SimpleProjectDTO;
import com.ai.apac.smartenv.system.dto.SwitchProjectDTO;
import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.service.IProjectService;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.ai.apac.smartenv.system.vo.TenantVO;
import com.ai.apac.smartenv.system.wrapper.ProjectWrapper;
import com.ai.apac.smartenv.system.wrapper.TenantWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/29 10:25 ??????
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/project")
@Api(value = "????????????", tags = "????????????")
@Slf4j
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "????????????", notes = "????????????")
    public R addProject(@RequestBody ProjectDTO projectDTO) {
        return R.status(projectService.addProject(projectDTO));
    }

    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "??????ID", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "projectName", value = "????????????", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "status", value = "????????????", paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "cityId", value = "????????????", paramType = "query", dataType = "long")
    })
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<IPage<ProjectVO>> page(@RequestParam(required = false) String projectName, @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) Long cityId, @RequestParam(required = false) Long companyId, Query query) {
        ProjectVO projectVO = new ProjectVO();
        projectVO.setProjectName(projectName);
        projectVO.setCityId(cityId);
        projectVO.setStatus(status);
        projectVO.setCompanyId(companyId);
        IPage<Project> pages = projectService.selectPage(projectVO, query);
        return R.data(ProjectWrapper.build().pageVO(pages));
    }

    @GetMapping("/myProjectList")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "??????ID", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "projectName", value = "????????????", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "status", value = "????????????", paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "cityId", value = "????????????", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "findChildCity", value = "???????????????????????????(0-?????????,1??????)", paramType = "query", dataType = "integer")
    })
    @ApiOperation(value = "??????????????????????????????????????????", notes = "??????????????????????????????????????????")
    public R<List<ProjectVO>> listMyProject(@RequestParam(required = false) String projectName, @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) Long cityId, @RequestParam(required = false) Long companyId,
                                            @RequestParam(required = false, defaultValue = "1") Integer findChildCity, BladeUser bladeUser) {
        ProjectVO projectVO = new ProjectVO();
        projectVO.setProjectName(projectName);
        projectVO.setCityId(cityId);
        projectVO.setStatus(status);
        projectVO.setCompanyId(companyId);
        List<ProjectVO> projectList = projectService.listProjectByUser(projectVO, bladeUser.getUserId(), findChildCity);
        return R.data(projectList);
    }

    @GetMapping("/{projectId}")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "??????ID", paramType = "path", dataType = "long")
    })
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<ProjectVO> getProjectDetail(@PathVariable Long projectId) {
        return R.data(projectService.getProjectByDetail(projectId));
    }

    @PutMapping("/{projectId}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "????????????", notes = "????????????")
    public R updateProject(@PathVariable Long projectId, @RequestBody ProjectDTO projectDTO) {
        projectDTO.setId(projectId);
        return R.status(projectService.updateProject(projectDTO));
    }

    @PutMapping("/{projectId}/status")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "??????????????????", notes = "??????????????????(1-?????? 2-??????)")
    public R changeProjectStatus(@PathVariable Long projectId, Integer newStatus) {
        return R.status(projectService.changeProjectStatus(projectId, newStatus));
    }

    @GetMapping("/{accountId}/projectCityTree")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "???????????????????????????????????????", notes = "???????????????????????????????????????")
    public R<List<CityVO>> getProjectCityTree(@PathVariable Long accountId) {
        return R.data(projectService.listProjectCity(accountId));
    }

    @PostMapping("/tenant2Project")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    public R listAccountProject() {
        return R.status(projectService.convertTenantToProject());
    }

    @PostMapping("/linkProjectCityToAdcode")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "??????????????????????????????adcode????????????", notes = "??????????????????????????????adcode????????????")
    public R linkProjectCityToAdcode() {
        projectService.linkProjectCityToAdcode();
        return R.status(true);
    }

    @PostMapping("/switchProject")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "????????????", notes = "????????????")
    public R switchProject(@RequestBody SwitchProjectDTO switchProjectDTO) {
        return projectService.switchProject(switchProjectDTO.getProjectCode());
    }

    @GetMapping("/simpleProjectList")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<List<SimpleProjectDTO>> listSimpleProject(@RequestParam(required = false) String projectCode, @RequestParam(value = "bladeAuth", required = false) String bladeAuth, BladeUser bladeUser) {
        List<String> projectCodeList = null;
        List<SimpleProjectDTO> simpleProjectList = null;
        List<ProjectVO> projectVOList = null;
        Long userId = null;
        String roleName = null;
        if (StringUtil.isNotEmpty(projectCode)) {
            projectCodeList = Func.toStrList(projectCode);
            simpleProjectList = new ArrayList<SimpleProjectDTO>();
            for (String projectCodeStr : projectCodeList) {
                Project project = ProjectCache.getProjectByCode(projectCodeStr);
                if (project != null && project.getId() != null) {
                    SimpleProjectDTO simpleProjectDTO = new SimpleProjectDTO();
                    simpleProjectDTO.setProjectCode(project.getProjectCode());
                    simpleProjectDTO.setProjectName(project.getProjectName());
                    simpleProjectDTO.setShortName(project.getShortName());
                    simpleProjectDTO.setId(null);
                    simpleProjectDTO.setAdcode(null);
                    simpleProjectList.add(simpleProjectDTO);
                }
            }
            return R.data(simpleProjectList);
        } else if (StringUtils.isNotEmpty(bladeAuth)) {
            Claims claims = JwtUtil.parseJWT(bladeAuth);
            if (claims == null) {
                throw new ServiceException("???????????????");
            } else {
                String userIdStr = claims.get("user_id", String.class);
                userId = Long.valueOf(userIdStr);
                roleName = claims.get("role_name", String.class);
            }
        } else if (bladeUser != null) {
            roleName = bladeUser.getRoleName();
            userId = bladeUser.getUserId();
        }
        if (StringUtils.isNotEmpty(roleName) && roleName.indexOf(SystemConstant.RoleAlias.ADMINISTRATOR) >= 0) {
            List<Project> allProject = projectService.list();
            if (CollUtil.isNotEmpty(allProject)) {
                simpleProjectList = allProject.stream().map(project -> {
                    SimpleProjectDTO simpleProjectDTO = new SimpleProjectDTO();
                    simpleProjectDTO.setProjectCode(project.getProjectCode());
                    simpleProjectDTO.setProjectName(project.getProjectName());
                    simpleProjectDTO.setShortName(project.getShortName());
                    simpleProjectDTO.setId(null);
                    simpleProjectDTO.setAdcode(null);
                    return simpleProjectDTO;
                }).collect(Collectors.toList());
            }
        } else if(userId != null){
            projectVOList = projectService.listProjectByAccountId(Long.valueOf(userId), 1);
            if (CollUtil.isNotEmpty(projectVOList)) {
                simpleProjectList = projectVOList.stream().map(projectVO -> {
                    SimpleProjectDTO simpleProjectDTO = new SimpleProjectDTO();
                    simpleProjectDTO.setProjectCode(projectVO.getProjectCode());
                    simpleProjectDTO.setProjectName(projectVO.getProjectName());
                    simpleProjectDTO.setShortName(projectVO.getShortName());
                    simpleProjectDTO.setId(null);
                    simpleProjectDTO.setAdcode(null);
                    return simpleProjectDTO;
                }).collect(Collectors.toList());
            }
        }
        return R.data(simpleProjectList);
    }
}
