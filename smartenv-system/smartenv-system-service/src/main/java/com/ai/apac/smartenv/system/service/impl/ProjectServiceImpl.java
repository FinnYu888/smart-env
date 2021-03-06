package com.ai.apac.smartenv.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.feign.IAlarmRuleInfoClient;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.ProjectConstant;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.common.dto.AreaNode;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.ai.apac.smartenv.system.cache.*;
import com.ai.apac.smartenv.system.dto.ProjectDTO;
import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.mapper.*;
import com.ai.apac.smartenv.system.service.*;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.apac.smartenv.system.vo.ProjectVO;
import com.ai.apac.smartenv.system.wrapper.AdministrativeCityWrapper;
import com.ai.apac.smartenv.system.wrapper.CityWrapper;
import com.ai.apac.smartenv.system.wrapper.ProjectWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.rmi.ServerException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMIN;
import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMINISTRATOR;

/**
 * @author qianlong
 * @description ????????????????????????
 * @Date 2020/11/26 7:50 ??????
 **/
@Service
@Slf4j
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Value("${smartenv.app.baseUrl}")
    private String appBaseUrl;

    private String authRequestUrl = "/smartenv-auth/oauth/token";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private ITenantService tenantService;

    @Autowired
    private IDeptService deptService;

    @Autowired
    private IStationService stationService;

    @Autowired
    private IPersonClient personClient;

    @Autowired
    private IUserClient userClient;

    @Autowired
    private IPersonUserRelClient personUserRelClient;

    @Autowired
    private AccountProjectRelMapper accountProjectRelMapper;

    @Autowired
    private IPushcClient pushcClient;

    @Autowired
    private IAlarmRuleInfoClient alarmRuleInfoClient;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CityBaiduMapper cityBaiduMapper;

    @Autowired
    private ILoginService loginService;

    @Autowired
    private BladeRedis bladeRedis;

//    @Autowired
//    private CityMapper cityMapper;

    @Autowired
    private ICityService cityService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * Project??????????????????
     *
     * @param projectDTO
     */
    private void basicValid(ProjectDTO projectDTO) {
        if (projectDTO.getCompanyId() == null || projectDTO.getCompanyId() <= 0L) {
            throw new ServiceException("???????????????????????????");
        }
        if (projectDTO.getCityId() == null || projectDTO.getCityId() <= 0L) {
            throw new ServiceException("???????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getProjectName())) {
            throw new ServiceException("?????????????????????");
        }
        projectDTO.setProjectName(projectDTO.getProjectName().trim());
        if (StringUtils.isBlank(projectDTO.getShortName())) {
            throw new ServiceException("?????????????????????");
        }
        if (projectDTO.getShortName().trim().length() > 10) {
            throw new ServiceException("??????????????????????????????10?????????");
        }
        if (StringUtils.isBlank(projectDTO.getProjectType())) {
            throw new ServiceException("?????????????????????");
        }
        if (StringUtils.isNotBlank(projectDTO.getAdminAccount())) {
            if (projectDTO.getAdminAccount().length() > 45) {
                throw new ServiceException("?????????????????????????????????45?????????");
            }
        } else {
            throw new ServiceException("?????????????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getOwnerName())) {
            throw new ServiceException("????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getMobile())) {
            throw new ServiceException("????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getEmail())) {
            throw new ServiceException("????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getLng()) || StringUtils.isBlank(projectDTO.getLat())) {
            throw new ServiceException("???????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getMapScale())) {
            throw new ServiceException("?????????????????????????????????");
        }
        if (StringUtils.isBlank(projectDTO.getAddress())) {
            throw new ServiceException("????????????????????????");
        }
    }

    /**
     * ??????????????????
     *
     * @param projectDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean addProject(ProjectDTO projectDTO) {
        //????????????
        basicValid(projectDTO);

        BladeUser currentUser = AuthUtil.getUser();
        //????????????????????????
        Project project = Objects.requireNonNull(BeanUtil.copy(projectDTO, Project.class));
        project.setStatus(1);
        project.setCreateDept(Long.valueOf(currentUser.getDeptId()));
        project.setCreateUser(currentUser.getUserId());
        project.setCreateTime(new Date());
        project.setUpdateUser(currentUser.getUserId());
        project.setUpdateTime(new Date());

        List<Tenant> tenants = tenantMapper.selectList(Wrappers.<Tenant>query().lambda().eq(Tenant::getIsDeleted, BladeConstant.DB_NOT_DELETED));
        tenants.stream().forEach(tenantObj -> {
            if (project.getProjectName().equals(tenantObj.getTenantName())) {
                throw new ServiceException("???????????????????????????????????????");
            }
        });
        List<String> codes = tenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
        String tenantId = tenantService.getTenantId(codes);
        project.setProjectCode(tenantId);

        //???????????????????????????
        Role role = new Role();
        role.setTenantId(tenantId);
        role.setParentId(BladeConstant.TOP_PARENT_ID);
        role.setRoleName("?????????");
        role.setRoleAlias("admin");
        role.setSort(2);
        role.setIsDeleted(0);
        roleService.save(role);
        //????????????????????????????????????
        RoleCache.saveOrUpdateRole(role);

        //??????????????????
        Dept dept = new Dept();
        dept.setTenantId(tenantId);
        dept.setParentId(BladeConstant.TOP_PARENT_ID);
        dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
        dept.setDeptName("?????????");
        dept.setFullName("?????????");
        dept.setDeptCategory(1);
        dept.setSort(2);
        dept.setIsDeleted(0);
        deptService.save(dept);
        //????????????????????????????????????
        DeptCache.saveOrUpdateDept(dept);

        //??????????????????
        Station defaultStation = new Station();
        defaultStation.setParentId(0L);
        defaultStation.setStationName("?????????");
        //??????????????????????????????
        defaultStation.setStationLevel(4);
        defaultStation.setTenantId(tenantId);
        stationService.createStation(defaultStation);

        //????????????????????????
        User adminAccount = null;

        //??????????????????
        Person adminPerson = null;

        //?????????????????????
        boolean createNewAccount = false;

        //?????????????????????
        boolean createNewPerson = false;

        Long accountId = 0L;

        //?????????????????????
        Integer isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;

        Long ownerId = projectDTO.getOwnerId();
        if (ownerId != null && ownerId > 0L) {
            //?????????????????????????????????
            createNewPerson = false;
            adminPerson = this.getPersonById(ownerId);
            //????????????????????????????????????,?????????????????????????????????,???????????????????????????????????????
            adminAccount = this.getUserByPersonId(ownerId);
            if (adminAccount == null) {
                createNewAccount = true;
                adminAccount = new User();
            } else {
                createNewAccount = false;
                accountId = adminAccount.getId();
            }
        } else {
            //?????????????????????,???????????????????????????,????????????????????????
            createNewPerson = true;
            adminPerson = new Person();
            //???????????????????????????????????????????????????????????????,???????????????????????????????????????
            R<User> adminAccountResult = userClient.userByAcct(projectDTO.getAdminAccount());
            if (adminAccountResult != null && adminAccountResult.getData() != null && adminAccountResult.getData().getId() != null) {
                adminAccount = adminAccountResult.getData();
                createNewAccount = false;
            } else {
                adminAccount = new User();
                createNewAccount = true;
            }
        }

        String account = projectDTO.getAdminAccount();
        if (createNewAccount) {
            //???????????????????????????????????????????????????????????????????????????????????????

            //?????????????????????????????????
            adminAccount.setTenantId(tenantId);
            adminAccount.setName(projectDTO.getOwnerName());
            adminAccount.setRealName(projectDTO.getOwnerName());
            adminAccount.setAccount(projectDTO.getAdminAccount());
            adminAccount.setPassword("123456");
            adminAccount.setEmail(projectDTO.getEmail());
            adminAccount.setRoleId(String.valueOf(role.getId()));
            adminAccount.setDeptId(String.valueOf(dept.getId()));
            adminAccount.setBirthday(new Date());
            adminAccount.setSex(1);
            adminAccount.setPhone(projectDTO.getMobile());
            adminAccount.setIsDeleted(0);
            adminAccount.setStatus(SystemConstant.UserStatus.Normal);

            R<Boolean> userResult = null;
            try {
                userResult = userClient.saveUser(adminAccount);
            } catch (Exception ex) {
                if (ex.getMessage().equals("????????????????????????!")) {
                    throw new ServiceException(userResult.getMsg());
                }
            }

            //????????????????????????????????????ID
            adminAccount = this.getUserByIdOrAcct(null, adminAccount.getAccount());
            if (adminAccount == null) {
                throw new ServiceException("?????????????????????????????????");
            }
            accountId = adminAccount.getId();
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        if (createNewPerson) {
            //?????????????????????
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEntryTime(new Date());
            //???????????????????????????6?????????
            adminPerson.setJobNumber(String.valueOf(RandomUtil.randomInt(100000, 999999)));
            adminPerson.setPersonDeptId(dept.getId());
            adminPerson.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            adminPerson.setTenantId(tenantId);
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setPersonPositionId(defaultStation.getId());
            adminPerson.setEntryTime(new Date());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            //??????????????????
            adminPerson.setGender(1);
            R<Person> createPersonResult = personClient.createPerson(adminPerson);
            if (createPersonResult == null || !createPersonResult.isSuccess()) {
                throw new ServiceException("????????????????????????:" + createPersonResult.getMsg());
            } else {
                isDefaultProject = ProjectConstant.ProjectDefault.Default;
                ownerId = createPersonResult.getData().getId();
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
            //??????????????????
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setUpdateTime(new Date());
            adminPerson.setUpdateUser(AuthUtil.getUserId());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            personClient.updatePerson(adminPerson);
        }

        //???????????????????????????????????????
        if (createNewAccount) {
            PersonUserRel personUserRel = new PersonUserRel();
            personUserRel.setPersonId(ownerId);
            personUserRel.setUserId(accountId);
            personUserRel.setTenantId(tenantId);
            R<PersonUserRel> createRelResult = personUserRelClient.createPersonUserRel(personUserRel);
            if (createRelResult == null || !createRelResult.isSuccess()) {
                throw new ServiceException("???????????????????????????:" + createRelResult.getMsg());
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        //???????????????????????????
        this.saveAccountProjectRel(tenantId, adminAccount, isDefaultProject);

        //????????????????????????
        this.saveOrUpdateProjectArea(projectDTO);

        //??????????????????????????????
        Tenant tenant = new Tenant();
        tenant.setTenantName(project.getProjectName());
        tenant.setLinkman(projectDTO.getOwnerName());
        tenant.setAddress(project.getAddress());
        tenant.setCityId(project.getCityId());
        tenant.setAdcode(getAdcodeByCityId(projectDTO.getCityId()));
        tenant.setAdminAccount(projectDTO.getAdminAccount());
        tenant.setContactNumber(projectDTO.getMobile());
        tenant.setEmail(projectDTO.getEmail());
        tenant.setTenantId(tenantId);
        tenant.setStatus(1);
        tenant.setIsDeleted(0);
        tenantMapper.insert(tenant);

        //??????????????????????????????
        project.setOwnerId(ownerId);
        project.setAdcode(tenant.getAdcode());
        project.setIsDeleted(0);
        baseMapper.insert(project);

        //????????????????????????
        this.updateUserAdminRole(ownerId, adminAccount.getId(), project.getProjectCode());

        //????????????????????????
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject("??????????????????");
        emailDTO.setReceiver(tenant.getEmail());
        emailDTO.setContent(StrUtil.format("?????????????????????{},???????????????{}???", projectDTO.getAdminAccount(), "123456"));
        pushcClient.sendEmail(emailDTO);

        ProjectCache.saveProject(project);
        ProjectCache.reloadAccountProject();
        TenantCache.saveOrUpdateTenant(tenant);
        return true;
    }

    /**
     * ??????????????????
     *
     * @param projectDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean updateProject(ProjectDTO projectDTO) {
        Long projectId = projectDTO.getId();
        if (projectId == null || projectId <= 0L) {
            throw new ServiceException("??????????????????????????????");
        }
        //??????????????????
        basicValid(projectDTO);

        //????????????????????????
        User adminAccount = null;

        //??????????????????
        Person adminPerson = null;

        //?????????????????????
        boolean createNewAccount = false;

        //?????????????????????
        boolean createNewPerson = false;

        Long accountId = 0L;
        Long deptId = 0L;
        Long stationId = 0L;
        String roleId = "";
        String roleGroup = "";

        //??????????????????????????????
        Project oldProject = baseMapper.selectById(projectId);

        //?????????????????????????????????
        Person oldAdminPerson = this.getPersonById(oldProject.getOwnerId());
        User oldAdminAccount = this.getUserByPersonId(oldProject.getOwnerId());
        if (oldAdminPerson != null) {
            deptId = oldAdminPerson.getPersonDeptId();
            stationId = oldAdminPerson.getPersonPositionId();
        }
        if (oldAdminAccount != null) {
            roleId = oldAdminAccount.getRoleId();
            roleGroup = oldAdminAccount.getRoleGroup();
        }

        Long ownerId = projectDTO.getOwnerId();
        if (ownerId != null && ownerId > 0L) {
            //???????????????????????????,??????????????????????????????,??????????????????????????????,???????????????????????????????????????????????????,??????????????????????????????????????????
            //????????????????????????????????????
            adminPerson = this.getPersonById(projectDTO.getOwnerId());
            adminAccount = this.getUserByPersonId(projectDTO.getOwnerId());
            if (adminPerson == null && adminAccount == null) {
                //??????????????????
                createNewPerson = true;
                createNewAccount = true;
            } else {
                //??????????????????,??????????????????,??????????????????????????????????????????
                createNewPerson = false;
                ownerId = adminPerson.getId();
                if (adminAccount == null) {
                    createNewAccount = true;
                } else {
                    createNewAccount = false;
                    accountId = adminAccount.getId();
                }
            }
        } else {
            //?????????????????????????????????
            createNewPerson = true;
            createNewAccount = true;
        }

        Integer isDefaultProject = ProjectConstant.ProjectDefault.Default;

        if (createNewAccount) {
            //???????????????????????????????????????????????????????????????????????????????????????

            //?????????????????????????????????
            adminAccount = new User();
            adminAccount.setTenantId(oldProject.getProjectCode());
            adminAccount.setName(projectDTO.getOwnerName());
            adminAccount.setRealName(projectDTO.getOwnerName());
            adminAccount.setAccount(projectDTO.getAdminAccount());
            adminAccount.setPassword("123456");
            adminAccount.setEmail(projectDTO.getEmail());
            adminAccount.setRoleId(roleId);
            adminAccount.setDeptId(String.valueOf(deptId));
            adminAccount.setBirthday(new Date());
            adminAccount.setSex(1);
            adminAccount.setPhone(projectDTO.getMobile());
            adminAccount.setIsDeleted(0);
            adminAccount.setStatus(SystemConstant.UserStatus.Normal);
            adminAccount.setRoleGroup(roleGroup);

            R<Boolean> userResult = null;
            try {
                userResult = userClient.saveUser(adminAccount);
            } catch (Exception ex) {
                if (ex.getMessage().equals("????????????????????????!")) {
                    throw new ServiceException(userResult.getMsg());
                }
            }
            //????????????????????????????????????ID
            adminAccount = this.getUserByIdOrAcct(null, adminAccount.getAccount());
            if (adminAccount == null) {
                throw new ServiceException("?????????????????????????????????");
            }
            accountId = adminAccount.getId();
        } else {
            if (oldAdminAccount != null || oldAdminAccount.getId() != null) {
                isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
                //????????????????????????,??????????????????????????????????????????????????????
                oldAdminAccount.setRoleGroup(SystemConstant.RoleAlias.USER);
                oldAdminAccount.setUpdateTime(new Date());
                oldAdminAccount.setUpdateUser(AuthUtil.getUserId());
                userClient.updateUser(oldAdminAccount);
            }
        }

        if (createNewPerson) {
            //?????????????????????
            adminPerson = new Person();
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEntryTime(new Date());
            //???????????????????????????6?????????
            adminPerson.setJobNumber(String.valueOf(RandomUtil.randomInt(100000, 999999)));
            adminPerson.setPersonDeptId(deptId);
            adminPerson.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            adminPerson.setTenantId(oldProject.getProjectCode());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setPersonPositionId(stationId);
            adminPerson.setEntryTime(new Date());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            //??????????????????
            adminPerson.setGender(1);
            R<Person> createPersonResult = personClient.createPerson(adminPerson);
            if (createPersonResult == null || !createPersonResult.isSuccess()) {
                throw new ServiceException("????????????????????????:" + createPersonResult.getMsg());
            } else {
                isDefaultProject = ProjectConstant.ProjectDefault.Default;
                ownerId = createPersonResult.getData().getId();
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
            //??????????????????
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            adminPerson.setUpdateTime(new Date());
            adminPerson.setUpdateUser(AuthUtil.getUserId());
            personClient.updatePerson(adminPerson);
        }

        //???????????????????????????????????????
        if (createNewAccount) {
            PersonUserRel personUserRel = new PersonUserRel();
            personUserRel.setPersonId(ownerId);
            personUserRel.setUserId(accountId);
            personUserRel.setTenantId(oldProject.getProjectCode());
            R<PersonUserRel> createRelResult = personUserRelClient.createPersonUserRel(personUserRel);
            if (createRelResult == null || !createRelResult.isSuccess()) {
                throw new ServiceException("???????????????????????????:" + createRelResult.getMsg());
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        if (!oldProject.getOwnerId().equals(projectDTO.getAccountId())) {
            //??????????????????,?????????????????????????????????????????????
            List<AccountProjectRel> accountProjectRelList = accountProjectRelMapper.selectList(new LambdaQueryWrapper<AccountProjectRel>()
                    .eq(AccountProjectRel::getAccountId, oldAdminAccount.getId()).eq(AccountProjectRel::getProjectCode, projectDTO.getProjectCode()));
            accountProjectRelList.stream().forEach(accountProjectRel -> {
                accountProjectRelMapper.deleteById(accountProjectRel);
            });
            ProjectCache.delAccountProject(adminAccount.getId());
        }

        //???????????????????????????
        this.saveAccountProjectRel(projectDTO.getProjectCode(), adminAccount, isDefaultProject);

//        User newAdminAccount = UserCache.getUserByAcct(projectDTO.getAdminAccount());
//        String newRoleIds = newAdminAccount.getRoleId();
//        List<Long> newRoleIdList = Func.toLongList(newRoleIds);
//        List<Role> projectAdminRoleList = roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getTenantId, projectDTO.getProjectCode()).eq(Role::getRoleAlias, ADMIN));
//        List<Long> oldRoleIdList = new ArrayList<Long>();
//        if (CollUtil.isNotEmpty(projectAdminRoleList)) {
//            oldRoleIdList = projectAdminRoleList.stream().map(role -> {
//                return role.getId();
//            }).collect(Collectors.toList());
//        }
//        List<Long> finalRoleIdList = new ArrayList<Long>();
//        finalRoleIdList.addAll(newRoleIdList);
//        finalRoleIdList.addAll(oldRoleIdList);
//        finalRoleIdList = finalRoleIdList.stream().distinct().collect(Collectors.toList());
//        newRoleIds = CollUtil.join(finalRoleIdList, ",");
//        newAdminAccount.setRoleId(newRoleIds);
//        newAdminAccount.setRoleGroup(ADMIN);
//        userClient.updateUser(newAdminAccount);

        //????????????????????????
        this.saveOrUpdateProjectArea(projectDTO);

        //??????????????????
        Tenant oldTenant = TenantCache.getTenantById(projectDTO.getProjectCode());
        oldTenant.setTenantName(projectDTO.getProjectName());
        oldTenant.setLinkman(projectDTO.getOwnerName());
        oldTenant.setAddress(projectDTO.getAddress());
        oldTenant.setCityId(projectDTO.getCityId());
        oldTenant.setAdcode(getAdcodeByCityId(projectDTO.getCityId()));
        oldTenant.setAdminAccount(projectDTO.getAdminAccount());
        oldTenant.setContactNumber(projectDTO.getMobile());
        oldTenant.setEmail(projectDTO.getEmail());
        oldTenant.setTenantId(projectDTO.getProjectCode());
        oldTenant.setStatus(1);
        oldTenant.setIsDeleted(0);
        tenantMapper.updateById(oldTenant);

        //??????????????????
        BeanUtils.copyProperties(projectDTO, oldProject);
        oldProject.setOwnerId(ownerId);
        oldProject.setAdcode(oldTenant.getAdcode());
        oldProject.setUpdateUser(AuthUtil.getUser().getUserId());
        oldProject.setUpdateTime(new Date());
        baseMapper.updateById(oldProject);

        //????????????????????????
        this.updateUserAdminRole(ownerId, adminAccount.getId(), oldProject.getProjectCode());

        if (createNewAccount) {
            //????????????????????????
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setSubject("??????????????????");
            emailDTO.setReceiver(projectDTO.getEmail());
            emailDTO.setContent(StrUtil.format("?????????????????????{},???????????????{}???", projectDTO.getAdminAccount(), "123456"));
            pushcClient.sendEmail(emailDTO);
        }

        ProjectCache.saveProject(oldProject);
        ProjectCache.reloadAccountProject();
        TenantCache.saveOrUpdateTenant(oldTenant);
        return true;
    }

    /**
     * ????????????????????????
     *
     * @param ownerId
     */
    private void updateUserAdminRole(Long ownerId, Long accountId, String projectCode) {
        //??????????????????????????????
        User currentUser = UserCache.getUser(accountId);
        String currentRoleIdStr = currentUser.getRoleId();
        List<String> currentRoleIdList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(currentRoleIdStr)) {
            currentRoleIdList = Func.toStrList(currentRoleIdStr);
        }
        //????????????????????????????????????
        List<String> userRoleIdList = currentRoleIdList.stream().map(roleId -> {
            Role role = RoleCache.getRole(roleId);
            if (role.getRoleAlias().equalsIgnoreCase(SystemConstant.RoleAlias.USER)) {
                return role.getId().toString();
            }
            return null;
        }).filter(roleId -> StringUtils.isNotEmpty(roleId)).collect(Collectors.toList());

        //?????????????????????????????????
        List<Project> projectList = baseMapper.selectList(new LambdaQueryWrapper<Project>().eq(Project::getOwnerId, ownerId));
        List<String> projectCodeList = new ArrayList<String>();
        if (CollUtil.isNotEmpty(projectList)) {
            projectCodeList = projectList.stream().map(project -> {
                return project.getProjectCode();
            }).collect(Collectors.toList());
        }
        projectCodeList.add(projectCode);
        List<Role> adminRoleList = roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getRoleAlias, ADMIN).in(Role::getTenantId, projectCodeList));
        List<String> adminRoleIdList = adminRoleList.stream().map(role -> {
            return role.getId().toString();
        }).collect(Collectors.toList());
        adminRoleIdList.addAll(userRoleIdList);
        //??????
        adminRoleIdList = adminRoleIdList.stream().distinct().collect(Collectors.toList());
        currentUser.setRoleId(CollUtil.join(adminRoleIdList, ","));

        //??????????????????
        userClient.updateUser(currentUser);
    }

    /**
     * ????????????????????????????????????
     *
     * @param projectCode
     * @param adminAccount
     * @param isDefault
     */
    private void saveAccountProjectRel(String projectCode, User adminAccount, Integer isDefault) {
        //???????????????????????????
        Integer count = accountProjectRelMapper.selectCount(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getProjectCode, projectCode)
                .eq(AccountProjectRel::getAccountId, adminAccount.getId()));
        if (count <= 0) {
            AccountProjectRel accountProjectRel = new AccountProjectRel();
            accountProjectRel.setProjectCode(projectCode);
            accountProjectRel.setAccount(adminAccount.getAccount());
            accountProjectRel.setAccountId(adminAccount.getId());
            accountProjectRel.setIsDefault(isDefault);
            accountProjectRel.setIsDeleted(0);
            accountProjectRel.setStatus(1);
            accountProjectRel.setProjectCode(projectCode);
            accountProjectRelMapper.insert(accountProjectRel);
            ProjectCache.delAccountProject(adminAccount.getId());
        }
    }

    /**
     * ????????????????????????
     *
     * @param userId
     */
    private void grandAdminRole(Long userId) {
        List<AccountProjectRel> accountProjectRelList = accountProjectRelMapper.selectList(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getAccountId, userId));
    }

    /**
     * ??????????????????
     *
     * @param projectDTO
     */
    private void saveOrUpdateProjectArea(ProjectDTO projectDTO) {
        //????????????????????????
        if (CollUtil.isNotEmpty(projectDTO.getAreaNodeList())) {
            //?????????????????????
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("project_code").is(projectDTO.getProjectCode()));
            mongoTemplate.remove(query, ProjectArea.class);

            ProjectArea projectArea = new ProjectArea();
            projectArea.setAreaId(IdUtil.fastSimpleUUID());
            projectArea.setAreaNodeList(projectDTO.getAreaNodeList());
            projectArea.setProjectCode(projectDTO.getProjectCode());
            mongoTemplate.save(projectArea);
        }
    }

    /**
     * ????????????
     *
     * @param projectVO
     * @param query
     * @return
     */
    @Override
    public IPage<Project> selectPage(ProjectVO projectVO, Query query) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(projectVO.getProjectName())) {
            queryWrapper.like("project_name", projectVO.getProjectName());
        }
        if (projectVO.getCityId() != null && projectVO.getCityId() > 0L) {
            queryWrapper.eq("city_id", projectVO.getCityId());
        }
        if (projectVO.getStatus() != null && projectVO.getStatus() > 0) {
            queryWrapper.eq("status", projectVO.getStatus());
        }
        if (projectVO.getCompanyId() != null && projectVO.getCompanyId() > 0) {
            queryWrapper.eq("company_id", projectVO.getCompanyId());
        }
        queryWrapper.orderByDesc("update_time");
        IPage<Project> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
    }

    /**
     * ???????????????
     *
     * @param projectVO
     * @param userId
     * @return
     */
    @Override
    public List<ProjectVO> listProjectByUser(ProjectVO projectVO, Long userId, Integer findChildCity) {
        List<Long> cityIdList = new ArrayList<Long>();
        cityIdList.add(projectVO.getCityId());
        if (projectVO.getCityId() != null && findChildCity == 1) {
            //???????????????
            List<City> childCity1List = cityService.list(new LambdaQueryWrapper<City>().eq(City::getParentId, projectVO.getCityId()));
            if (CollUtil.isNotEmpty(childCity1List)) {
                childCity1List.stream().forEach(city1 -> {
                    Long cityId1 = city1.getId();
                    cityIdList.add(cityId1);
                    List<City> childCity2List = cityService.list(new LambdaQueryWrapper<City>().eq(City::getParentId, cityId1));
                    if (CollUtil.isNotEmpty(childCity2List)) {
                        childCity2List.stream().forEach(city2 -> {
                            cityIdList.add(city2.getId());
                        });
                    }
                });
            }
            return baseMapper.listProjectByAccountId(userId, projectVO.getStatus(), null, projectVO.getProjectName(), cityIdList);
        } else {
            return baseMapper.listProjectByAccountId(userId, projectVO.getStatus(), projectVO.getCityId(), projectVO.getProjectName(), null);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param projectId
     * @return
     */
    @Override
    public ProjectVO getProjectByDetail(Long projectId) {
        Project project = baseMapper.selectById(projectId);
        ProjectVO projectVO = ProjectWrapper.build().entityDetailVO(project);
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("project_code").is(project.getProjectCode()));
        ProjectArea projectArea = mongoTemplate.findOne(query, ProjectArea.class);
        if (projectArea != null) {
            projectVO.setAreaNodeList(projectArea.getAreaNodeList());
        }
        return projectVO;
    }

    /**
     * ????????????ID?????????????????????????????????????????????????????????
     *
     * @param personId
     * @return
     */
//    private Pair<Person, User> getPersonUser(Long personId) {
//        Person person = this.getPersonById(personId);
//        if (person == null) {
//            return null;
//        }
//        User user = null;
//        user = this.getUserByPersonId(personId);
//        return new Pair<Person, User>(person, user);
//    }
    private Person getPersonById(Long personId) {
        R<Person> personResult = personClient.getPerson(personId);
        if (personResult.isSuccess() && personResult.getData() != null && personResult.getData().getId() != null) {
            return personResult.getData();
        } else {
            return null;
        }
    }

    /**
     * ????????????ID??????????????????
     *
     * @param personId
     * @return
     */
    private User getUserByPersonId(Long personId) {
        R<PersonUserRel> personUserRelResult = personUserRelClient.getRelByPersonId(personId);
        if (personUserRelResult.isSuccess() && personUserRelResult.getData() != null && personUserRelResult.getData().getId() != null) {
            PersonUserRel personUserRel = personUserRelResult.getData();
            Long userId = personUserRel.getUserId();
            R<User> userResult = userClient.userInfoById(userId);
            if (userResult.isSuccess() && userResult.getData() != null && userResult.getData().getId() != null) {
                return userResult.getData();
            }
        }
        return null;
    }

    /**
     * ??????????????????ID????????????????????????
     *
     * @param userId
     * @param account
     * @return
     */
    private User getUserByIdOrAcct(Long userId, String account) {
        R<User> userResult = null;
        if (userId != null && userId > 0L) {
            userResult = userClient.userInfoById(userId);
        } else if (StringUtils.isNotBlank(account)) {
            userResult = userClient.userByAcct(account);
        }
        if (userResult != null && userResult.getData() != null && userResult.getData().getId() != null) {
            return userResult.getData();
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param projectId
     * @param newStatus
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean changeProjectStatus(Long projectId, Integer newStatus) {
        Project projectTmp = baseMapper.selectById(projectId);
        if (projectTmp == null) {
            throw new ServiceException("??????????????????????????????");
        }
        projectTmp.setStatus(newStatus);
        projectTmp.setUpdateTime(new Date());
        projectTmp.setUpdateUser(AuthUtil.getUserId());
        baseMapper.updateById(projectTmp);
        ProjectCache.delProjectById(projectId);
        ProjectCache.delProjectByCode(projectTmp.getProjectCode());

        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantId, projectTmp.getProjectCode()));
        if (tenant != null) {
            tenant.setStatus(newStatus);
            tenant.setUpdateTime(new Date());
            tenant.setUpdateUser(AuthUtil.getUserId());
            tenantMapper.updateById(tenant);
            TenantCache.deleteTenant(tenant.getTenantId());
        }
        return true;
    }

    /**
     * ??????????????????ID??????????????????????????????????????????
     *
     * @param accountId
     * @param projectStatus
     * @return
     */
    @Override
    public List<ProjectVO> listProjectByAccountId(Long accountId, Integer projectStatus) {
        return baseMapper.listProjectByAccountId(accountId, projectStatus, null, null, null);
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param accountId
     * @return
     */
    @Override
    public List<CityVO> listProjectCity(Long accountId) {
        List<ProjectVO> projectList = listProjectByAccountId(accountId, ProjectConstant.ProjectStatus.Normal);
        List<Long> projectCityIdList = null;
        HashMap<Long, Integer> cityProjectCountMap = new HashMap<Long, Integer>();
        if (CollUtil.isNotEmpty(projectList)) {
            projectCityIdList = projectList.stream().map(project -> {
                return project.getCityId();
            }).collect(Collectors.toList());
            for (Long cityId : projectCityIdList) {
                Integer projectNum = cityProjectCountMap.get(cityId);
                if (projectNum != null) {
                    cityProjectCountMap.put(cityId, projectNum + 1);
                } else {
                    cityProjectCountMap.put(cityId, 1);
                }
            }
        }
        if (CollUtil.isNotEmpty(projectCityIdList)) {
            projectCityIdList = projectCityIdList.stream().distinct().collect(Collectors.toList());
            List<CityVO> cityVOList = new ArrayList<CityVO>();
            for (Long cityId : projectCityIdList) {
                City city = cityService.getCityById(cityId);
                CityVO cityVO = null;
                if (city != null) {
                    cityVO = CityWrapper.build().entityVO(city);
                    cityVO.setProjectNum(this.getProjectNum(cityProjectCountMap, cityId));
                    cityVOList.add(cityVO);
                } else {
                    continue;
                }
                if (city.getParentId() != 0L) {
                    City parentCity1 = cityService.getCityById(city.getParentId());
                    CityVO parentCity1VO = null;
                    if (parentCity1 != null) {
                        parentCity1VO = CityWrapper.build().entityVO(parentCity1);
                        if (cityProjectCountMap.get(parentCity1.getId()) == null) {
                            parentCity1VO.setProjectNum(cityVO.getProjectNum());
                        } else {
                            parentCity1VO.setProjectNum(this.getProjectNum(cityProjectCountMap, parentCity1.getId()) + this.getProjectNum(cityProjectCountMap, cityId));
                        }
                        cityVOList.add(parentCity1VO);
                    }
                    if (parentCity1.getParentId() != 0L) {
                        City parentCity2 = cityService.getCityById(parentCity1.getParentId());
                        if (parentCity2 != null) {
                            CityVO parentCity2VO = CityWrapper.build().entityVO(parentCity2);
                            if (cityProjectCountMap.get(parentCity2.getId()) == null && cityProjectCountMap.get(parentCity1.getId()) == null) {
                                parentCity2VO.setProjectNum(parentCity1VO.getProjectNum());
                            } else if (cityProjectCountMap.get(parentCity2.getId()) != null && cityProjectCountMap.get(parentCity1.getId()) == null) {
                                parentCity2VO.setProjectNum(this.getProjectNum(cityProjectCountMap, parentCity2.getId()) + this.getProjectNum(cityProjectCountMap, cityId));
                            } else {
                                parentCity2VO.setProjectNum(this.getProjectNum(cityProjectCountMap, parentCity2.getId()) + this.getProjectNum(cityProjectCountMap, parentCity1.getId()) + this.getProjectNum(cityProjectCountMap, cityId));
                            }
                            cityVOList.add(parentCity2VO);
                        }
                    }
                }
            }
            cityVOList = cityVOList.stream().distinct().collect(Collectors.toList());
            List<CityVO> cityTree = ForestNodeMerger.merge(cityVOList);
            return cityTree;
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param cityProjectCountMap
     * @param cityId
     * @return
     */
    private Integer getProjectNum(HashMap<Long, Integer> cityProjectCountMap, Long cityId) {
        return cityProjectCountMap.get(cityId) == null ? 0 : cityProjectCountMap.get(cityId);
    }

    /**
     * ???????????????????????????????????????????????????(??????????????????)
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean convertTenantToProject() {
        List<Tenant> allTenant = tenantMapper.selectList(new QueryWrapper<Tenant>());
        allTenant.stream().forEach(tenant -> {
            Project project = baseMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, tenant.getTenantId()));
            if (project == null || project.getId() == null) {

                List<Company> companyList = companyMapper.selectList(new LambdaQueryWrapper<Company>().eq(Company::getFullName, tenant.getTenantName()));
                Long companyId = 0L;
                if (companyList != null && companyList.size() > 0) {
                    companyId = companyList.get(0).getId();
                } else {
                    //???????????????????????????
                    Company company = new Company();
                    company.setFullName(tenant.getTenantName());
                    company.setShortName("");
                    company.setAddress(tenant.getAddress());
                    company.setCompanySize(2);
                    company.setEmail(tenant.getEmail());
                    company.setOwnerName(tenant.getLinkman());
                    company.setParentId(0L);
                    company.setStatus(1);
                    company.setIsDeleted(0);
                    company.setMobile(tenant.getContactNumber());
                    companyMapper.insert(company);
                    companyId = company.getId();
                }

                //???????????????????????????
                project = new Project();
                BeanUtils.copyProperties(tenant, project);
                project.setProjectCode(tenant.getTenantId());
                project.setProjectName(tenant.getTenantName());
                if (tenant.getTenantName().length() >= 20) {
                    project.setShortName(tenant.getTenantName().substring(0, 19));
                } else {
                    project.setShortName(tenant.getTenantName());
                }
                project.setAddress(tenant.getAddress());
                project.setCompanyId(companyId);
                project.setCityId(tenant.getCityId());
                project.setProjectType("1");
                project.setDeviceNum(1000);
                project.setPersonNum(1000);
                project.setVehicleNum(1000);
                Long ownerId = 0L;
                String adminCount = tenant.getAdminAccount();
                R<User> userResult = userClient.userByAcct(adminCount);
                if (userResult.isSuccess() && userResult.getData() != null) {
                    User user = userResult.getData();
                    R<PersonUserRel> personUserRelResult = personUserRelClient.getRelByUserId(user.getId());
                    if (personUserRelResult.isSuccess() && personUserRelResult.getData() != null) {
                        PersonUserRel personUserRel = personUserRelResult.getData();
                        ownerId = personUserRel.getPersonId();
                        Integer count = accountProjectRelMapper.selectCount(new LambdaQueryWrapper<AccountProjectRel>()
                                .eq(AccountProjectRel::getAccountId, user.getId())
                                .eq(AccountProjectRel::getProjectCode, tenant.getTenantId()));
                        if (count == 0) {
                            AccountProjectRel accountProjectRel = new AccountProjectRel();
                            accountProjectRel.setIsDefault(ProjectConstant.ProjectDefault.Default);
                            accountProjectRel.setStatus(ProjectConstant.ProjectStatus.Normal);
                            accountProjectRel.setIsDeleted(0);
                            accountProjectRel.setProjectCode(tenant.getTenantId());
                            accountProjectRel.setAccountId(user.getId());
                            accountProjectRel.setAccount(user.getAccount());
                            accountProjectRelMapper.insert(accountProjectRel);
                        }
                    }
                }
                ownerId = ownerId == null ? 0L : ownerId;
                log.info("The tenant[{}] ownerId:{}", tenant.getTenantId(), ownerId);
                project.setOwnerId(ownerId);
                baseMapper.insert(project);
            }
        });
        ProjectCache.reload();
        return true;
    }


    /**
     * ??????????????????????????????adcode????????????(??????????????????)
     */
    @Override
    public void linkProjectCityToAdcode() {
        List<Project> result = baseMapper.selectList(new LambdaQueryWrapper<Project>().groupBy(Project::getCityId));
        List<Long> citIdList = result.stream().map(project -> {
            return project.getCityId();
        }).collect(Collectors.toList());
        citIdList.stream().forEach(cityId -> {
            Long adcode = getAdcodeByCityId(cityId);
            if (adcode != null) {
                Project project = new Project();
                project.setAdcode(adcode);
                baseMapper.update(project, new LambdaQueryWrapper<Project>().eq(Project::getCityId, cityId));
                Tenant tenant = new Tenant();
                tenant.setAdcode(adcode);
                tenantMapper.update(tenant, new LambdaQueryWrapper<Tenant>().eq(Tenant::getCityId, cityId));
            }
        });
        ProjectCache.reload();
    }

    /**
     * ????????????ID?????????????????????????????????
     *
     * @param cityId
     * @return
     */
    private Long getAdcodeByCityId(Long cityId) {
        String cityName = cityService.getCityNameById(cityId);
        if (StringUtils.isNotBlank(cityName)) {
            List<AdministrativeCity> cityList = cityBaiduMapper.selectList(new LambdaQueryWrapper<AdministrativeCity>().like(AdministrativeCity::getCityName, cityName));
            if (CollUtil.isNotEmpty(cityList)) {
                Long adcode = cityList.get(0).getId();
                return adcode;
            }
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param newProjectCode
     * @return R<JSONObject>
     */
    @Override
    public R<JSONObject> switchProject(String newProjectCode) {
        BladeUser currentUser = AuthUtil.getUser();
        log.info("??????????????????:{}", currentUser.getTenantId());
//        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantId,newProjectCode));
//        if (tenant == null || tenant.getId() == null) {
//            throw new ServiceException("???????????????????????????");
//        }
        Project project = baseMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, newProjectCode));
        if (project == null || project.getId() == null) {
            throw new ServiceException("???????????????????????????");
        }
        //???????????????????????????????????????????????????
        Integer count = accountProjectRelMapper.selectCount(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getProjectCode, newProjectCode).eq(AccountProjectRel::getAccountId, currentUser.getUserId()));
        if (count <= 0) {
            throw new ServiceException("???????????????????????????,??????????????????");
        }
        String account = currentUser.getAccount();
        //???????????????base64??????
        String loginPassword = bladeRedis.get("smartenv:account:" + StringPool.COLON + account);
        if (StringUtils.isBlank(loginPassword)) {
            throw new ServiceException("????????????,???????????????");
        }

        //??????blade_user?????????tenant_id??????????????????projectCode
        User user = UserCache.getUser(currentUser.getUserId());
        user.setTenantId(newProjectCode);
        userClient.updateUser(user);

        //??????HTTP????????????auth??????
        Map<String, Object> bodyParams = new HashMap<String, Object>();
        bodyParams.put("grant_type", "password");
        bodyParams.put("scope", "all");
        bodyParams.put("username", account);
        bodyParams.put("password", loginPassword);
        bodyParams.put("switchProject", newProjectCode);
        HttpResponse httpResponse = HttpUtil.createRequest(Method.POST, appBaseUrl + authRequestUrl)
                .contentType(ContentType.FORM_URLENCODED.toString())
                .header("Authorization", "Basic d2VjaGF0OmFzaWFpbmZvMTIz")
                .form(bodyParams)
                .execute();
        log.info("HTTP Status is:{}", httpResponse.getStatus());
        String body = httpResponse.body();
        log.info("AUTH Response Body={}", body);
        int httpStatus = httpResponse.getStatus();
        JSONObject result = JSON.parseObject(body);
        if (httpStatus == 200) {
            return R.data(result);
        } else {
            String errorMsg = result.getString("error_description");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(errorMsg)) {
                throw new ServiceException(errorMsg);
            } else {
                throw new ServiceException(result.toJSONString());
            }
        }
    }
}
