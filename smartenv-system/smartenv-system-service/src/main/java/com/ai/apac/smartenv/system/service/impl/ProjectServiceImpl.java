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
 * @description 公司项目相关服务
 * @Date 2020/11/26 7:50 下午
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
     * Project信息基本校验
     *
     * @param projectDTO
     */
    private void basicValid(ProjectDTO projectDTO) {
        if (projectDTO.getCompanyId() == null || projectDTO.getCompanyId() <= 0L) {
            throw new ServiceException("请选择项目归属公司");
        }
        if (projectDTO.getCityId() == null || projectDTO.getCityId() <= 0L) {
            throw new ServiceException("请选择项目所在城市");
        }
        if (StringUtils.isBlank(projectDTO.getProjectName())) {
            throw new ServiceException("请输入项目名称");
        }
        projectDTO.setProjectName(projectDTO.getProjectName().trim());
        if (StringUtils.isBlank(projectDTO.getShortName())) {
            throw new ServiceException("请输入平台名称");
        }
        if (projectDTO.getShortName().trim().length() > 10) {
            throw new ServiceException("平台名称长度不能超过10个字符");
        }
        if (StringUtils.isBlank(projectDTO.getProjectType())) {
            throw new ServiceException("请选择项目类型");
        }
        if (StringUtils.isNotBlank(projectDTO.getAdminAccount())) {
            if (projectDTO.getAdminAccount().length() > 45) {
                throw new ServiceException("管理员帐号长度不能超过45个字符");
            }
        } else {
            throw new ServiceException("管理员帐号长度不能为空");
        }
        if (StringUtils.isBlank(projectDTO.getOwnerName())) {
            throw new ServiceException("请输入负责人姓名");
        }
        if (StringUtils.isBlank(projectDTO.getMobile())) {
            throw new ServiceException("请输入负责人手机");
        }
        if (StringUtils.isBlank(projectDTO.getEmail())) {
            throw new ServiceException("请输入负责人邮箱");
        }
        if (StringUtils.isBlank(projectDTO.getLng()) || StringUtils.isBlank(projectDTO.getLat())) {
            throw new ServiceException("请选择项目中心地点");
        }
        if (StringUtils.isBlank(projectDTO.getMapScale())) {
            throw new ServiceException("请选择项目地图缩放级别");
        }
        if (StringUtils.isBlank(projectDTO.getAddress())) {
            throw new ServiceException("请选输入项目地址");
        }
    }

    /**
     * 新增项目信息
     *
     * @param projectDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean addProject(ProjectDTO projectDTO) {
        //基本校验
        basicValid(projectDTO);

        BladeUser currentUser = AuthUtil.getUser();
        //准备数据实体对象
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
                throw new ServiceException("该项目名称在系统中已存在！");
            }
        });
        List<String> codes = tenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
        String tenantId = tenantService.getTenantId(codes);
        project.setProjectCode(tenantId);

        //准备默认管理员角色
        Role role = new Role();
        role.setTenantId(tenantId);
        role.setParentId(BladeConstant.TOP_PARENT_ID);
        role.setRoleName("管理员");
        role.setRoleAlias("admin");
        role.setSort(2);
        role.setIsDeleted(0);
        roleService.save(role);
        //刷新新角色数据到缓存中去
        RoleCache.saveOrUpdateRole(role);

        //准备默认部门
        Dept dept = new Dept();
        dept.setTenantId(tenantId);
        dept.setParentId(BladeConstant.TOP_PARENT_ID);
        dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
        dept.setDeptName("管理部");
        dept.setFullName("管理部");
        dept.setDeptCategory(1);
        dept.setSort(2);
        dept.setIsDeleted(0);
        deptService.save(dept);
        //刷新新部门数据到缓存中去
        DeptCache.saveOrUpdateDept(dept);

        //创建默认岗位
        Station defaultStation = new Station();
        defaultStation.setParentId(0L);
        defaultStation.setStationName("管理员");
        //岗位级别默认为管理级
        defaultStation.setStationLevel(4);
        defaultStation.setTenantId(tenantId);
        stationService.createStation(defaultStation);

        //登录帐号信息对象
        User adminAccount = null;

        //员工信息对象
        Person adminPerson = null;

        //是否创建新帐号
        boolean createNewAccount = false;

        //是否创建新员工
        boolean createNewPerson = false;

        Long accountId = 0L;

        //是否是默认项目
        Integer isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;

        Long ownerId = projectDTO.getOwnerId();
        if (ownerId != null && ownerId > 0L) {
            //负责人是系统员原有员工
            createNewPerson = false;
            adminPerson = this.getPersonById(ownerId);
            //查询该员工是否已经有帐号,如果没有帐号则需要创建,如果已经存在则更新数据即可
            adminAccount = this.getUserByPersonId(ownerId);
            if (adminAccount == null) {
                createNewAccount = true;
                adminAccount = new User();
            } else {
                createNewAccount = false;
                accountId = adminAccount.getId();
            }
        } else {
            //负责人是新员工,则需要创建登录帐号,并且与该员工关联
            createNewPerson = true;
            adminPerson = new Person();
            //先根据传入的帐号查询系统中是否有这个帐号了,如果已经有了就更新数据即可
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
            //新的负责人需要同时创建一个用户并与角色、部门、帐号关联起来

            //设置默认管理帐号的信息
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
                if (ex.getMessage().equals("登录帐户不能重复!")) {
                    throw new ServiceException(userResult.getMsg());
                }
            }

            //查询创建结果获取登录帐号ID
            adminAccount = this.getUserByIdOrAcct(null, adminAccount.getAccount());
            if (adminAccount == null) {
                throw new ServiceException("管理员帐号创建没有成功");
            }
            accountId = adminAccount.getId();
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        if (createNewPerson) {
            //需要创建新员工
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEntryTime(new Date());
            //租户管理员工号随机6位数字
            adminPerson.setJobNumber(String.valueOf(RandomUtil.randomInt(100000, 999999)));
            adminPerson.setPersonDeptId(dept.getId());
            adminPerson.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            adminPerson.setTenantId(tenantId);
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setPersonPositionId(defaultStation.getId());
            adminPerson.setEntryTime(new Date());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            //性别默认为男
            adminPerson.setGender(1);
            R<Person> createPersonResult = personClient.createPerson(adminPerson);
            if (createPersonResult == null || !createPersonResult.isSuccess()) {
                throw new ServiceException("创建默认员工失败:" + createPersonResult.getMsg());
            } else {
                isDefaultProject = ProjectConstant.ProjectDefault.Default;
                ownerId = createPersonResult.getData().getId();
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
            //更新员工信息
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setUpdateTime(new Date());
            adminPerson.setUpdateUser(AuthUtil.getUserId());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            personClient.updatePerson(adminPerson);
        }

        //新帐号都需要和员工关联起来
        if (createNewAccount) {
            PersonUserRel personUserRel = new PersonUserRel();
            personUserRel.setPersonId(ownerId);
            personUserRel.setUserId(accountId);
            personUserRel.setTenantId(tenantId);
            R<PersonUserRel> createRelResult = personUserRelClient.createPersonUserRel(personUserRel);
            if (createRelResult == null || !createRelResult.isSuccess()) {
                throw new ServiceException("绑定员工和帐号失败:" + createRelResult.getMsg());
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        //帐号和项目关联关系
        this.saveAccountProjectRel(tenantId, adminAccount, isDefaultProject);

        //保存项目所属区域
        this.saveOrUpdateProjectArea(projectDTO);

        //保存租户数据到租户表
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

        //保存项目数据到项目表
        project.setOwnerId(ownerId);
        project.setAdcode(tenant.getAdcode());
        project.setIsDeleted(0);
        baseMapper.insert(project);

        //更新操作员的角色
        this.updateUserAdminRole(ownerId, adminAccount.getId(), project.getProjectCode());

        //发送开通租户邮件
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject("您的登录帐号");
        emailDTO.setReceiver(tenant.getEmail());
        emailDTO.setContent(StrUtil.format("您的登录帐号是{},登录密码是{}。", projectDTO.getAdminAccount(), "123456"));
        pushcClient.sendEmail(emailDTO);

        ProjectCache.saveProject(project);
        ProjectCache.reloadAccountProject();
        TenantCache.saveOrUpdateTenant(tenant);
        return true;
    }

    /**
     * 修改项目信息
     *
     * @param projectDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean updateProject(ProjectDTO projectDTO) {
        Long projectId = projectDTO.getId();
        if (projectId == null || projectId <= 0L) {
            throw new ServiceException("请选择需要修改的项目");
        }
        //基本信息校验
        basicValid(projectDTO);

        //登录帐号信息对象
        User adminAccount = null;

        //员工信息对象
        Person adminPerson = null;

        //是否创建新帐号
        boolean createNewAccount = false;

        //是否创建新员工
        boolean createNewPerson = false;

        Long accountId = 0L;
        Long deptId = 0L;
        Long stationId = 0L;
        String roleId = "";
        String roleGroup = "";

        //获取原有项目信息对象
        Project oldProject = baseMapper.selectById(projectId);

        //获取项目原负责人的信息
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
            //负责人是系统内员工,获取原有项目信息对象,比较负责人是否修改了,如果修改了则需要去除原来的关联关系,新增新负责人与项目的关联关系
            //获取目前项目负责人的信息
            adminPerson = this.getPersonById(projectDTO.getOwnerId());
            adminAccount = this.getUserByPersonId(projectDTO.getOwnerId());
            if (adminPerson == null && adminAccount == null) {
                //新员工新帐号
                createNewPerson = true;
                createNewAccount = true;
            } else {
                //系统内老员工,则不需要创建,只需要修改负责人其他基本信息
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
            //系统内无员工则需要新建
            createNewPerson = true;
            createNewAccount = true;
        }

        Integer isDefaultProject = ProjectConstant.ProjectDefault.Default;

        if (createNewAccount) {
            //新的负责人需要同时创建一个用户并与角色、部门、帐号关联起来

            //设置默认管理帐号的信息
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
                if (ex.getMessage().equals("登录帐户不能重复!")) {
                    throw new ServiceException(userResult.getMsg());
                }
            }
            //查询创建结果获取登录帐号ID
            adminAccount = this.getUserByIdOrAcct(null, adminAccount.getAccount());
            if (adminAccount == null) {
                throw new ServiceException("管理员帐号创建没有成功");
            }
            accountId = adminAccount.getId();
        } else {
            if (oldAdminAccount != null || oldAdminAccount.getId() != null) {
                isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
                //如果负责人更换了,则原负责人角色组更换成普通用户角色组
                oldAdminAccount.setRoleGroup(SystemConstant.RoleAlias.USER);
                oldAdminAccount.setUpdateTime(new Date());
                oldAdminAccount.setUpdateUser(AuthUtil.getUserId());
                userClient.updateUser(oldAdminAccount);
            }
        }

        if (createNewPerson) {
            //需要创建新员工
            adminPerson = new Person();
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEntryTime(new Date());
            //租户管理员工号随机6位数字
            adminPerson.setJobNumber(String.valueOf(RandomUtil.randomInt(100000, 999999)));
            adminPerson.setPersonDeptId(deptId);
            adminPerson.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            adminPerson.setTenantId(oldProject.getProjectCode());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setPersonPositionId(stationId);
            adminPerson.setEntryTime(new Date());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            //性别默认为男
            adminPerson.setGender(1);
            R<Person> createPersonResult = personClient.createPerson(adminPerson);
            if (createPersonResult == null || !createPersonResult.isSuccess()) {
                throw new ServiceException("创建默认员工失败:" + createPersonResult.getMsg());
            } else {
                isDefaultProject = ProjectConstant.ProjectDefault.Default;
                ownerId = createPersonResult.getData().getId();
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
            //更新员工信息
            adminPerson.setPersonName(projectDTO.getOwnerName());
            adminPerson.setEmail(projectDTO.getEmail());
            adminPerson.setMobileNumber(projectDTO.getMobile());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            adminPerson.setUpdateTime(new Date());
            adminPerson.setUpdateUser(AuthUtil.getUserId());
            personClient.updatePerson(adminPerson);
        }

        //新帐号都需要和员工关联起来
        if (createNewAccount) {
            PersonUserRel personUserRel = new PersonUserRel();
            personUserRel.setPersonId(ownerId);
            personUserRel.setUserId(accountId);
            personUserRel.setTenantId(oldProject.getProjectCode());
            R<PersonUserRel> createRelResult = personUserRelClient.createPersonUserRel(personUserRel);
            if (createRelResult == null || !createRelResult.isSuccess()) {
                throw new ServiceException("绑定员工和帐号失败:" + createRelResult.getMsg());
            }
        } else {
            isDefaultProject = ProjectConstant.ProjectDefault.NotDefault;
        }

        if (!oldProject.getOwnerId().equals(projectDTO.getAccountId())) {
            //负责人更换了,则删除原有的帐号与项目关联关系
            List<AccountProjectRel> accountProjectRelList = accountProjectRelMapper.selectList(new LambdaQueryWrapper<AccountProjectRel>()
                    .eq(AccountProjectRel::getAccountId, oldAdminAccount.getId()).eq(AccountProjectRel::getProjectCode, projectDTO.getProjectCode()));
            accountProjectRelList.stream().forEach(accountProjectRel -> {
                accountProjectRelMapper.deleteById(accountProjectRel);
            });
            ProjectCache.delAccountProject(adminAccount.getId());
        }

        //帐号和项目关联关系
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

        //保存项目所属区域
        this.saveOrUpdateProjectArea(projectDTO);

        //更新租户信息
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

        //更新项目信息
        BeanUtils.copyProperties(projectDTO, oldProject);
        oldProject.setOwnerId(ownerId);
        oldProject.setAdcode(oldTenant.getAdcode());
        oldProject.setUpdateUser(AuthUtil.getUser().getUserId());
        oldProject.setUpdateTime(new Date());
        baseMapper.updateById(oldProject);

        //更新操作员的角色
        this.updateUserAdminRole(ownerId, adminAccount.getId(), oldProject.getProjectCode());

        if (createNewAccount) {
            //发送开通租户邮件
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setSubject("您的登录帐号");
            emailDTO.setReceiver(projectDTO.getEmail());
            emailDTO.setContent(StrUtil.format("您的登录帐号是{},登录密码是{}。", projectDTO.getAdminAccount(), "123456"));
            pushcClient.sendEmail(emailDTO);
        }

        ProjectCache.saveProject(oldProject);
        ProjectCache.reloadAccountProject();
        TenantCache.saveOrUpdateTenant(oldTenant);
        return true;
    }

    /**
     * 更新负责人的角色
     *
     * @param ownerId
     */
    private void updateUserAdminRole(Long ownerId, Long accountId, String projectCode) {
        //查询该员工目前的角色
        User currentUser = UserCache.getUser(accountId);
        String currentRoleIdStr = currentUser.getRoleId();
        List<String> currentRoleIdList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(currentRoleIdStr)) {
            currentRoleIdList = Func.toStrList(currentRoleIdStr);
        }
        //将普通用户的角色过滤出来
        List<String> userRoleIdList = currentRoleIdList.stream().map(roleId -> {
            Role role = RoleCache.getRole(roleId);
            if (role.getRoleAlias().equalsIgnoreCase(SystemConstant.RoleAlias.USER)) {
                return role.getId().toString();
            }
            return null;
        }).filter(roleId -> StringUtils.isNotEmpty(roleId)).collect(Collectors.toList());

        //查询该员工所负责的项目
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
        //去重
        adminRoleIdList = adminRoleIdList.stream().distinct().collect(Collectors.toList());
        currentUser.setRoleId(CollUtil.join(adminRoleIdList, ","));

        //更新用户角色
        userClient.updateUser(currentUser);
    }

    /**
     * 保存帐号与项目的关联关系
     *
     * @param projectCode
     * @param adminAccount
     * @param isDefault
     */
    private void saveAccountProjectRel(String projectCode, User adminAccount, Integer isDefault) {
        //帐号和项目关联关系
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
     * 给用户管理员角色
     *
     * @param userId
     */
    private void grandAdminRole(Long userId) {
        List<AccountProjectRel> accountProjectRelList = accountProjectRelMapper.selectList(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getAccountId, userId));
    }

    /**
     * 保存项目区域
     *
     * @param projectDTO
     */
    private void saveOrUpdateProjectArea(ProjectDTO projectDTO) {
        //保存项目所属区域
        if (CollUtil.isNotEmpty(projectDTO.getAreaNodeList())) {
            //先删除原有区域
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
     * 分页查询
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
     * 不分页查询
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
            //查询子城市
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
     * 根据项目编号获取项目详情
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
     * 根据员工ID获取员工信息对象和对应的操作员帐号信息
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
     * 根据帐号ID获取帐号信息
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
     * 根据登录帐号ID获取登录帐号信息
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
     * 修改项目状态
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
            throw new ServiceException("请选择需要修改的项目");
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
     * 根据登录帐号ID、项目状态查询关联的项目信息
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
     * 查询当前登录帐户关联项目的所在城市树
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
     * 获取项目数量
     *
     * @param cityProjectCountMap
     * @param cityId
     * @return
     */
    private Integer getProjectNum(HashMap<Long, Integer> cityProjectCountMap, Long cityId) {
        return cityProjectCountMap.get(cityId) == null ? 0 : cityProjectCountMap.get(cityId);
    }

    /**
     * 将目前租户信息都转换成项目信息入库(仅供运维使用)
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
                    //用租户信息创建公司
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

                //创建项目并关联公司
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
     * 将项目所在城市编码与adcode关联起来(仅供运维使用)
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
     * 根据城市ID获取国家规范的区域编码
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
     * 用户切换项目
     *
     * @param newProjectCode
     * @return R<JSONObject>
     */
    @Override
    public R<JSONObject> switchProject(String newProjectCode) {
        BladeUser currentUser = AuthUtil.getUser();
        log.info("当前项目编码:{}", currentUser.getTenantId());
//        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantId,newProjectCode));
//        if (tenant == null || tenant.getId() == null) {
//            throw new ServiceException("要访问的项目不存在");
//        }
        Project project = baseMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, newProjectCode));
        if (project == null || project.getId() == null) {
            throw new ServiceException("要访问的项目不存在");
        }
        //需要校验当前用户是否可以切换该项目
        Integer count = accountProjectRelMapper.selectCount(new LambdaQueryWrapper<AccountProjectRel>().eq(AccountProjectRel::getProjectCode, newProjectCode).eq(AccountProjectRel::getAccountId, currentUser.getUserId()));
        if (count <= 0) {
            throw new ServiceException("您无权限访问该项目,请联系管理员");
        }
        String account = currentUser.getAccount();
        //对密码进行base64解密
        String loginPassword = bladeRedis.get("smartenv:account:" + StringPool.COLON + account);
        if (StringUtils.isBlank(loginPassword)) {
            throw new ServiceException("登录超时,请重新登录");
        }

        //变更blade_user表中的tenant_id为当前最新的projectCode
        User user = UserCache.getUser(currentUser.getUserId());
        user.setTenantId(newProjectCode);
        userClient.updateUser(user);

        //通过HTTP方式请求auth服务
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
