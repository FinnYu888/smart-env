/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.person.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.http.HttpUtil;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.dto.DevicePersonInfoDto;
import com.ai.apac.smartenv.device.dto.DeviceStatusCountDTO;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.dto.*;
import com.ai.apac.smartenv.person.entity.GroupMember;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.person.mapper.PersonMapper;
import com.ai.apac.smartenv.person.service.*;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.person.wrapper.PersonWrapper;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.ai.apac.smartenv.wechat.feign.IWechatUserClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 人员信息表 服务实现类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Service
@Slf4j
@AllArgsConstructor
public class PersonServiceImpl extends BaseServiceImpl<PersonMapper, Person> implements IPersonService {

    private IPersonExtService personExtService;
    private IPersonUserRelService personUserRelService;
    private IPersonVehicleRelService personVehicleRelService;
    private IScheduleClient scheduleClient;
    private IUserClient userClient;
    private ISysClient sysClient;
    private IDeviceClient deviceClient;
    private IWorkareaRelClient workareaRelClient;
    private IPersonVehicleRelClient personVehicleRelClient;
    private IDeviceRelClient deviceRelClient;
    private IPersonJobNumberService personJobNumberService;
    private IWechatUserClient wechatUserClient;
    private MongoTemplate mongoTemplate;
    private IPolymerizationClient polymerizationClient;
    private IGroupMemberService groupMemberService;
    private IDataChangeEventClient dataChangeEventClient;
    private IProjectClient projectClient;

    @Override
    public IPage<PersonVO> selectPersonPage(IPage<PersonVO> page, PersonVO person) {
        return page.setRecords(baseMapper.selectPersonPage(page, person));
    }

    @Override
    public Integer updatePersonInfoById(PersonVO person) {
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            person.setUpdateUser(user.getUserId());
        }
        person.setUpdateTime(DateUtil.now());

        Integer update = baseMapper.updatePersonInfoById(person);
//        List<OmnicPersonInfo> personList=new ArrayList<>();
//        personList.add(BeanUtil.copy(person,OmnicPersonInfo.class));
//        List<Long> personIdList = new ArrayList<>();
//        personIdList.add(person.getId());
//        polymerizationClient.reloadPersonInfo(personIdList);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), person.getId()));

        return update;
    }

    @Override
    public Integer updatePersonWatchStateById(Long state, Long personId) {
        Person person = baseMapper.selectById(personId);
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            person.setUpdateUser(user.getUserId());
        }
        person.setUpdateTime(DateUtil.now());
        person.setWatchDeviceStatus(state);

        Integer result = baseMapper.updateById(person);

        //触发数据库变更事件
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.PERSON_WATCH_STATUS_EVENT, person.getTenantId(), person.getId()));
        return result;
    }

    @Override
    public IPage<Person> page(PersonVO person, Query query, String deviceStatus, String isBindTerminal) {
        QueryWrapper<Person> queryWrapper = generateQueryWrapper(person);
        if (ObjectUtil.isNotEmpty(deviceStatus)) {
            queryWrapper.eq("watch_device_status", deviceStatus);
//            List<Long> entityIdList = deviceRelClient.getEntityRelsByCategory(AuthUtil.getTenantId(), DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString(), deviceStatus).getData();
//            if (ObjectUtil.isNotEmpty(entityIdList) && entityIdList.size() > 0) {
//                if (DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus)) {
//                    queryWrapper.lambda().notIn(Person::getId, entityIdList);
//                } else {
//                    queryWrapper.lambda().in(Person::getId, entityIdList);
//                }
//            } else {
//                if (!DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus)) {
//                    IPage<Person> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
//                    return emptyPage;
//                }
//            }
        }
        if (ObjectUtil.isNotEmpty(isBindTerminal)) {
            if (String.valueOf(VehicleConstant.VehicleRelBind.FALSE).equals(isBindTerminal)) {
                queryWrapper.eq("watch_device_status", -1);
            } else {
                queryWrapper.ne("watch_device_status", -1);
            }
//			List<Long> entityIdList = deviceRelClient.getEntityRelsByCategory(AuthUtil.getTenantId(), "", DeviceConstant.DeviceStatus.NO_DEV).getData();
//            if (ObjectUtil.isNotEmpty(entityIdList) && entityIdList.size() > 0) {
//                if (String.valueOf(PersonConstant.PersonRelBind.FALSE).equals(isBindTerminal)) {
//                	queryWrapper.lambda().notIn(Person::getId, entityIdList);
//                } else {
//                	queryWrapper.lambda().in(Person::getId, entityIdList);
//                }
//            } else {
//                if (String.valueOf(PersonConstant.PersonRelBind.TRUE).equals(isBindTerminal)) {
//                    IPage<Person> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
//                    return emptyPage;
//                }
//            }
        }
        IPage<Person> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
    }

    private QueryWrapper<Person> generateQueryWrapper(PersonVO person) {
        QueryWrapper<Person> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(person.getPersonName())) {
            queryWrapper.like("person_name", person.getPersonName());
        }
        if (person.getPersonPositionId() != null) {
            queryWrapper.eq("person_position_id", person.getPersonPositionId());
        }
        if (person.getPersonDeptId() != null) {
            List<Long> deptIdList = sysClient.getAllChildDepts(person.getPersonDeptId()).getData();
            if (deptIdList != null && !deptIdList.isEmpty()) {
                queryWrapper.in("person_dept_id", deptIdList);
            }
        }
        if (person.getIsIncumbency() != null) {
            queryWrapper.eq("is_incumbency", person.getIsIncumbency());
        }
        if (StringUtil.isNotBlank(person.getIsIncumbencys())) {
            queryWrapper.in("is_incumbency", Func.toStrList(person.getIsIncumbencys()));
        }
        if (StringUtils.isNotBlank(person.getTenantId())) {
            queryWrapper.eq("tenant_id", person.getTenantId());
        } else {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                queryWrapper.eq("tenant_id", user.getTenantId());
            }
        }
        queryWrapper.orderByAsc("is_incumbency").orderByAsc("job_number");
        return queryWrapper;
    }

    /*
     * 工号是否重复
     */
    private boolean checkJobNumber(Person person) {
        QueryWrapper<Person> queryWrapper = new QueryWrapper<>();
        if (person.getId() != null) {
            queryWrapper.notIn("id", person.getId());
        }
        queryWrapper.eq("job_number", person.getJobNumber());
        BladeUser user = AuthUtil.getUser();
        if (user != null && StringUtils.isNotBlank(user.getTenantId())) {
            queryWrapper.eq("tenant_id", user.getTenantId());
        } else if (StringUtils.isNotBlank(person.getTenantId())) {
            queryWrapper.eq("tenant_id", person.getTenantId());
        }
        List<Person> list = list(queryWrapper);
        if (list != null && list.size() > 0) {
//			return false;
            throw new ServiceException("该工号已存在");
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean savePersonInfo(PersonVO personVO) {
        // 验证参数
        validatePerson(personVO);
        // 工号非必填，若为null，自动生成
        if (StringUtil.isBlank(personVO.getJobNumber())) {
            String jobNumber = getNextJobNumber(personVO, personVO.getTenantId());
            personVO.setJobNumber(jobNumber);
        }
        checkJobNumber(personVO);
        // 删除缓存
        PersonCache.delPerson(null, personVO.getId());
        boolean save = save(personVO);
        if (save) {
            Long personId = personVO.getId();
            // 保存照片
            if (StringUtil.isNotBlank(personVO.getDriverLicenseFirstName())) {
                personExtService.savePicture(personId, PersonConstant.PersonExtAttr.DRIVER_LICENSE_FIRST_ATTR_ID,
                        PersonConstant.PersonExtAttr.DRIVER_LICENSE_FIRST_ATTR_NAME, personVO.getDriverLicenseFirstName());
            }
            if (StringUtil.isNotBlank(personVO.getDriverLicenseSecondName())) {
                personExtService.savePicture(personId, PersonConstant.PersonExtAttr.DRIVER_LICENSE_SECOND_ATTR_ID,
                        PersonConstant.PersonExtAttr.DRIVER_LICENSE_SECOND_ATTR_NAME, personVO.getDriverLicenseSecondName());
            }
            // 创建操作员
            if (StringUtil.isNotBlank(personVO.getAccount())) {
                User user = new User();
                user.setAccount(personVO.getAccount());
                user.setDeptId(String.valueOf(personVO.getPersonDeptId()));
                user.setName(personVO.getPersonName());
                user.setRealName(personVO.getPersonName());
                user.setPhone(String.valueOf(personVO.getMobileNumber()));
                user.setRoleId(personVO.getRoleId());
                user.setEmail(personVO.getEmail());
                user.setTenantId(personVO.getTenantId());
                Boolean saveUser = false;
                try {
                    saveUser = userClient.saveUser(user).getData();
                } catch (Exception e) {
                    throw new ServiceException("创建操作员失败!");
                }
                if (saveUser) {
                    user = UserCache.getUserByAcct(user.getAccount());
                    PersonUserRel personUserRel = new PersonUserRel();
                    personUserRel.setPersonId(personId);
                    personUserRel.setUserId(user.getId());
                    personUserRelService.savePersonUserRel(personUserRel);
                }
            }
        }

//        List<Long> personIdList = new ArrayList<>();
//        personIdList.add(personVO.getId());
//        polymerizationClient.reloadPersonInfo(personIdList);

        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), personVO.getId()));


        return save;
    }

    private String getNextJobNumber(PersonVO personVO, String tenantId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        String jobNumber = personJobNumberService.getNextNumber(tenantId);
        personVO.setJobNumber(jobNumber);
        try {
            checkJobNumber(personVO);
        } catch (Exception e) {
            if ("该工号已存在".equals(e.getMessage())) {
                // 工号重复
                jobNumber = getNextJobNumber(personVO, tenantId);
            } else {
                throw e;
            }
        }
        return jobNumber;
    }

    /*
     * 验证入参
     */
    private void validatePerson(@Valid PersonVO person) {
        Set<ConstraintViolation<@Valid Person>> validateSet = Validation.buildDefaultValidatorFactory().getValidator()
                .validate(person, new Class[0]);
        if (validateSet != null && !validateSet.isEmpty()) {
            String messages = validateSet.stream().map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
            throw new ServiceException(messages);
        }
        if (person.getId() != null) {
            if (StringUtil.isBlank(person.getJobNumber())) {
                throw new ServiceException("需要输入员工工号");
            }
        }
        // 非导入，校验岗位
        if (person.getIsImport() == null || !person.getIsImport()) {
            if (person.getPersonPositionId() == null) {
                throw new ServiceException("需要输入职位");
            }
        }
        // 校验手机号
		/*String regex = "^1[3|4|5|6|7|8|9]\\d{9}$";// ^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(String.valueOf(person.getMobileNumber()));
		if (!matcher.matches()) {
		    throw new ServiceException("手机号码格式不正确");
		}*/
        // 校验身份证号
        if (person.getIdCardType() != null && StringUtil.isNotBlank(person.getIdCard()) && person.getIdCardType() == 1) {
//            regex = "^[1-9]\\d{5}[1-9]\\d{3}((0[1-9])|(1[0-2]))(([0|1|2][1-9])|3[0-1])((\\d{4})|\\d{3}X)$";
            String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(person.getIdCard());
            if (!matcher.matches()) {
                throw new ServiceException("身份证号格式不正确");
            }
        }
        // 校验操作员
        if (person.getIsUser() != null && person.getIsUser() == PersonConstant.IsUser.YES) {
            Long userId = person.getUserId();
            if (userId == null || userId == 0) {
                if (StringUtil.isBlank(person.getAccount())) {
                    throw new ServiceException("需要输入账户名");
                }
                if (StringUtil.isBlank(person.getRoleId())) {
                    throw new ServiceException("需要选择操作员角色");
                }
                // 新建操作员账号不能重复
                User user = UserCache.getUserByAcct(person.getAccount());
                if (user != null && user.getId() != null && user.getId() > 0) {
                    throw new ServiceException("账户名不能重复");
                }
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public Integer updatePersonInfo(PersonVO person) {
        // 验证参数
        validatePerson(person);
        // 验证该员工是否是项目管理员,如果是则提示先更换项目管理员
        if (person.getIsIncumbency() != null && person.getIsIncumbency().equals(PersonConstant.IncumbencyStatus.UN)) {
            R<List<Project>> result = projectClient.getProjectByOwner(person.getId());
            if (result.isSuccess() && CollUtil.isNotEmpty(result.getData())) {
                throw new ServiceException("该员工是项目负责人,请先变更项目负责人为其他员工后再设置离职");
            }
        }

        // 验证操作员
        Integer isUser = person.getIsUser() == null ? 0 : person.getIsUser();
        if (isUser == PersonConstant.IsUser.YES) {
            Long userId = person.getUserId();
            if (userId == null) {
                throw new ServiceException("请选择操作员");
            }
            User user = UserCache.getUser(userId);
            if (user == null || user.getId() == null) {
                throw new ServiceException("操作员不存在");
            }
        }
        // 先退出解绑
        if (person.getIsIncumbency() != null && person.getIsIncumbency() == PersonConstant.IncumbencyStatus.UN) {
            removePersonAllRel(person);
            person.setWatchDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO_DEV));
            // 没有离职日期，默认当天
            if (person.getLeaveTime() == null) {
                person.setLeaveTime(DateUtil.now());
            }
        }
        checkJobNumber(person);
        Integer update = updatePersonInfoById(person);
        if (update == 1) {
            // 修改照片
            // 驾驶证正页
            personExtService.updatePicture(person.getId(), person.getDriverLicenseFirstName(),
                    PersonConstant.PersonExtAttr.DRIVER_LICENSE_FIRST_ATTR_ID, PersonConstant.PersonExtAttr.DRIVER_LICENSE_FIRST_ATTR_NAME);
            // 驾驶证副页
            personExtService.updatePicture(person.getId(), person.getDriverLicenseSecondName(),
                    PersonConstant.PersonExtAttr.DRIVER_LICENSE_SECOND_ATTR_ID, PersonConstant.PersonExtAttr.DRIVER_LICENSE_SECOND_ATTR_NAME);

            if (isUser == PersonConstant.IsUser.NO) {
                // 不可登陆
                PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(person.getId());
                if (personUserRel != null && personUserRel.getId() != null) {
                    personUserRelService.deletePersonUserRel(Arrays.asList(personUserRel.getId()));
                }
            } else {
                // 可登陆
                Long userId = person.getUserId();
                Long personId = person.getId();
                PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(personId);
                if (personUserRel == null || personUserRel.getId() == null) {
                    // 新绑定操作员
                    personUserRel = new PersonUserRel();
                    personUserRel.setPersonId(personId);
                    personUserRel.setUserId(userId);
                    personUserRelService.savePersonUserRel(personUserRel);
                } else {
                    Long currentUserId = personUserRel.getUserId();
                    if (!userId.equals(currentUserId)) {
                        // 更新绑定操作员
                        personUserRel.setUserId(userId);
                        PersonUserRelCache.delRel(personUserRel);
                        personUserRelService.updateById(personUserRel);
                    }
                }
            }
        }

//        List<Long> personIdList = new ArrayList<>();
//        personIdList.add(person.getId());
//        polymerizationClient.reloadPersonInfo(personIdList);
        if (PersonConstant.IncumbencyStatus.IN.equals(person.getIsIncumbency())) {
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), person.getId()));
        } else if (PersonConstant.IncumbencyStatus.UN.equals(person.getIsIncumbency())) {
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.REMOVE_PERSON_EVENT, AuthUtil.getTenantId(), person.getId()));

        }
        return update;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    protected void removePersonAllRel(PersonVO person) {
        // 解绑终端
        deviceClient.unbindDevice(person.getId(), CommonConstant.ENTITY_TYPE.PERSON);
        // 解绑路线
        workareaRelClient.unbindWorkarea(person.getId(), PersonConstant.WORKAREA_REL_PERSON);
        // 解绑考勤
        scheduleClient.unbindSchedule(person.getId(), ArrangeConstant.ScheduleObjectEntityType.PERSON);
        // 解绑驾驶员
        personVehicleRelClient.unbindVehicle(person.getId());
        // 解绑操作员
        personUserRelService.unbindUser(person.getId());


//        List<Long> personIdList = new ArrayList<>();
//        personIdList.add(person.getId());
//        polymerizationClient.reloadPersonInfo(personIdList);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), person.getId()));
    }

    private List<PersonNode> getChildNodeByDept(String nodeName, Long parentId, List<Dept> allDepts, String tenantId, Person person, List<Long> invalidEntityIdList) {
        List<PersonNode> nodeList = new ArrayList<>();
        for (Dept dept : allDepts) {
            if (parentId.equals(dept.getParentId())) {
                PersonNode node = new PersonNode();
                node.setId(dept.getId());
                node.setIsPerson(false);
                node.setNodeName(dept.getFullName());
                node.setIsValid(true);
                List<PersonNode> subNodes = getChildNodeByDept(nodeName, dept.getId(), allDepts, tenantId, person, invalidEntityIdList);
                // 查询人员
                List<Person> list = getPersonByDeptId(dept.getId());
                List<PersonVO> listVO = PersonWrapper.build().listVO(list);
                for (PersonVO obj : listVO) {
                    // 离职
                    if (obj.getIsIncumbency() == null || obj.getIsIncumbency() == PersonConstant.IncumbencyStatus.UN) {
                        continue;
                    }
                    // 根据岗位过滤
                    if (person != null && person.getPersonPositionId() != null && (obj.getPersonPositionId() == null
                            || !obj.getPersonPositionId().equals(person.getPersonPositionId()))) {
                        continue;
                    }
                    PersonNode personNode = new PersonNode();
                    personNode.setId(obj.getId());
                    personNode.setIsPerson(true);
                    personNode.setNodeName(obj.getPersonName() + "(" + obj.getJobNumber() + ")");
                    personNode.setIsLastNode(true);
                    personNode.setIsValid(true);
                    if (invalidEntityIdList != null && invalidEntityIdList.contains(obj.getId())) {
                        personNode.setIsValid(false);
                    }
                    if (StringUtil.isBlank(nodeName) || (StringUtil.isNotBlank(nodeName) && personNode.getNodeName().contains(nodeName))) {
                        subNodes.add(personNode);
                    }
                }
                if (StringUtil.isNotBlank(nodeName) && subNodes.isEmpty()) {
                    continue;
                }
                if (subNodes.isEmpty()) {
                    node.setIsLastNode(true);
                }
                node.setSubNodes(subNodes);
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    @Override
    public List<PersonNode> treeByDept(String nodeName, String tenantId, Person person, List<Long> invalidEntityIdList) {
        List<Dept> allDepts = sysClient.getAllDept().getData();
        Iterator<Dept> it = allDepts.iterator();
        while (it.hasNext()) {
            Dept dept = it.next();
            if (dept.getId().equals(0L) || dept.getId().equals(dept.getParentId())
                    || (StringUtils.isNotBlank(tenantId) && !tenantId.equals(dept.getTenantId()))
                    || dept.getIsDeleted() == null || dept.getIsDeleted() == 1) {
                it.remove();
            }
        }
        List<PersonNode> nodeList = getChildNodeByDept(nodeName, 0L, allDepts, tenantId, person, invalidEntityIdList);
        return nodeList;
    }

    @Override
    public List<Person> listAll(PersonVO person) {
        QueryWrapper<Person> queryWrapper = generateQueryWrapper(person);
        List<Person> list = list(queryWrapper);
        return list;
    }

    /**
     * 根据查询条件查询用户
     *
     * @param queryCond
     * @return
     */
    @Override
    public List<Person> listPersonByCond(Person queryCond) {
        QueryWrapper<Person> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", queryCond.getTenantId());
        if (StringUtils.isNotBlank(queryCond.getPersonName())) {
            queryWrapper.like("person_name", queryCond.getPersonName());
        }
        if (queryCond.getPersonDeptId() != null) {
            queryWrapper.eq("person_dept_id", queryCond.getPersonDeptId());
        }
        if (StringUtils.isNotEmpty(queryCond.getMobileNumber())) {
            queryWrapper.eq("mobile_number", queryCond.getMobileNumber());
        }
        if (StringUtils.isNotEmpty(queryCond.getIdCard())) {
            queryWrapper.eq("id_card", queryCond.getIdCard());
        }
        queryWrapper.eq("is_incumbency", queryCond.getIsIncumbency());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public int countAll(PersonVO person) {
        QueryWrapper<Person> queryWrapper = generateQueryWrapper(person);
        return count(queryWrapper);
    }

    @Override
    public IPage<Person> pageForVehicle(PersonVO person, Query query, Long vehicleId) {
        // 查询已绑定的该车辆的人员
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelService.getPersonByVehicle(vehicleId);
        List<Long> filterPersonIdList = new ArrayList<>();
        if (personVehicleRelList != null) {
            personVehicleRelList.forEach(rel -> {
                filterPersonIdList.add(rel.getPersonId());
            });
        }
        person.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        QueryWrapper<Person> queryWrapper = generateQueryWrapper(person);
        if (!filterPersonIdList.isEmpty()) {
            queryWrapper.notIn("id", filterPersonIdList);
        }
        IPage<Person> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
    }

    @Override
    public IPage<Person> pageForGroup(PersonVO person, Query query, Long groupId) {
        List<GroupMember> groupMemberList = groupMemberService.list(new QueryWrapper<GroupMember>().lambda().eq(GroupMember::getGroupId, groupId));
        QueryWrapper<Person> personQueryWrapper = new QueryWrapper<Person>();
        personQueryWrapper.lambda().ne(Person::getWatchDeviceStatus, -1);//过滤没绑定设备的
        if (ObjectUtil.isNotEmpty(groupMemberList) && groupMemberList.size() > 0) {
            List<Long> memberIds = new ArrayList<Long>();
            groupMemberList.forEach(groupMember -> {
                memberIds.add(groupMember.getMemberId());
            });
            personQueryWrapper.lambda().notIn(Person::getId, memberIds);//过滤已添加组的
        }

        if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getPersonName())) {
            personQueryWrapper.lambda().like(Person::getPersonName, person.getPersonName());
        }

        if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getPersonPositionId())) {
            personQueryWrapper.lambda().eq(Person::getPersonPositionId, person.getPersonPositionId());
        }


        if (ObjectUtil.isNotEmpty(person) && ObjectUtil.isNotEmpty(person.getPersonDeptId())) {
            personQueryWrapper.lambda().eq(Person::getPersonDeptId, person.getPersonDeptId());
        }


        IPage<Person> pages = page(Condition.getPage(query), personQueryWrapper);
        return pages;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean removePerson(List<Long> idList) {
        idList.forEach(id -> {
            Person person = PersonCache.getPersonById(null, id);
            if (person == null) {
                throw new ServiceException("人员不存在");
            }
            String personName = person.getPersonName();
            // 校验是否绑定终端
            List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(id, CommonConstant.ENTITY_TYPE.PERSON).getData();
            if (deviceRelList != null && !deviceRelList.isEmpty()) {
                throw new ServiceException(personName + ", " + "已绑定终端，不允许删除");
            }
            // 校验是否绑定区域/路线
            List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(id, PersonConstant.WORKAREA_REL_PERSON).getData();
            if (workareaRelList != null && !workareaRelList.isEmpty()) {
                throw new ServiceException(personName + ", " + "已绑定路线或区域，不允许删除");
            }
            // 校验是否绑定考勤
            List<ScheduleObject> scheduleObjectList = scheduleClient.listUnfinishScheduleByEntity(id, ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
            if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
                throw new ServiceException(personName + ", " + "已绑定考勤，不允许删除");
            }
            // 校验是否绑定驾驶员
            List<PersonVehicleRel> vehicleList = personVehicleRelClient.getVehicleByPersonId(id).getData();
            if (vehicleList != null && !vehicleList.isEmpty()) {
                throw new ServiceException(personName + ", " + "已绑定车辆，不允许删除");
            }
            // 校验是否绑定操作员
            PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(id);
            if (personUserRel != null && personUserRel.getId() != null) {
                throw new ServiceException(personName + ", " + "已绑定操作员，不允许删除");
            }
            // 删除缓存
            PersonCache.delPerson(null, id);
            // 逻辑删除
            deleteLogic(Arrays.asList(id));
            // 照片

            personExtService.removePersonAttr(id);

//            List<Long> personIdList = new ArrayList<>();
//            personIdList.add(person.getId());
//            polymerizationClient.removePersonList(personIdList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.REMOVE_PERSON_EVENT, AuthUtil.getTenantId(), person.getId()));

        });
        return true;
    }

    @Override
    public List<Person> getPersonByDeptId(Long deptId) {
        return list(new QueryWrapper<Person>().eq("person_dept_id", deptId));
    }

    /**
     * 获取人员树
     *
     * @param treeType 1-按职位分组，2-按部门分组
     * @param tenantId 租户ID
     * @return
     */
    @Override
    public List<PersonNode> getPersonTree(Integer treeType, String tenantId) {
        List<PersonNode> personNodeList = null;

        //获取今日人员排班列表
//        PersonStatusStatDTO personStatusStat = getPersonStatusStatToday(tenantId);
//        List<BasicPersonDTO> allPerson = new ArrayList<BasicPersonDTO>();
//        if (null != personStatusStat.getWorkingList()) {
////            allPerson.addAll(personStatusStat.getWorkingList());
////        }
////        if (null != personStatusStat.getDepartureList()) {
////            allPerson.addAll(personStatusStat.getDepartureList());
////        }
////        if (null != personStatusStat.getSitBackList()) {
////            allPerson.addAll(personStatusStat.getSitBackList());
////        }
        org.springframework.data.mongodb.core.query.Query query = org.springframework.data.mongodb.core.query.Query.query(Criteria.where("tenantId").is(tenantId)
        );
        query.addCriteria(Criteria.where("watchDeviceCode").ne(null));
        List<BasicPersonDTO> allPerson = mongoTemplate.find(query, BasicPersonDTO.class);

        log.info("当前租户所有人员数量:{}", allPerson.size());
        List<PersonNode> personTree = new ArrayList<PersonNode>();
        if (treeType == 1) {//按职位分组
            Map<Long, List<BasicPersonDTO>> personMap = allPerson.stream()
                    .collect(Collectors.groupingBy(BasicPersonDTO::getPersonPositionId));
            log.info("按职位分组结果:{}", JSON.toJSONString(personMap));
            personNodeList = personMap.keySet().stream().map(positionId -> {
                PersonNode personNode = new PersonNode();
                personNode.setId(positionId);
                personNode.setNodeName(String.valueOf(positionId));
                personNode.setIsLastNode(false);
                List<BasicPersonDTO> personDTOList = personMap.get(positionId);
                List<PersonNode> subTree = this.buildSubTree(personDTOList);
                personNode.setSubNodes(subTree);
                personTree.add(personNode);
                return personNode;
            }).collect(Collectors.toList());
        } else {
            Map<Long, List<BasicPersonDTO>> personMap = allPerson.stream().filter(basicPersonDTO -> basicPersonDTO.getPersonDeptId() != null)
                    .collect(Collectors.groupingByConcurrent(BasicPersonDTO::getPersonDeptId));
            log.info("按部门分组结果:{}", JSON.toJSONString(personMap));
            personNodeList = personMap.keySet().stream().map(deptId -> {
                PersonNode personNode = new PersonNode();
                personNode.setId(deptId);
                String deptName = DeptCache.getDeptName(String.valueOf(deptId));
                personNode.setNodeName(deptName);
                personNode.setIsLastNode(false);
                List<BasicPersonDTO> personDTOList = personMap.get(deptId);
                List<PersonNode> subTree = this.buildSubTree(personDTOList);
                personNode.setSubNodes(subTree);
                personTree.add(personNode);
                return personNode;
            }).collect(Collectors.toList());
        }

        return personNodeList;
    }

    private List<PersonNode> buildSubTree(List<BasicPersonDTO> personDTOList) {
        List<PersonNode> subTree = personDTOList.stream().map(personDTO -> {
            PersonNode subNode = new PersonNode();
            subNode.setId(personDTO.getId());
            subNode.setNodeName(personDTO.getPersonName());
            subNode.setJobNumber(personDTO.getJobNumber());
            subNode.setSubNodes(null);
            subNode.setShowFlag(false);
            subNode.setIsLastNode(true);
            subNode.setStatus(personDTO.getWorkStatus());
            subNode.setStatusName(personDTO.getWorkStatusName());
            return subNode;
        }).collect(Collectors.toList());
        return subTree;
    }

    /**
     * 获取人员当天出勤状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public PersonStatusStatDTO getPersonStatusStatToday(String tenantId) {
        //工作中人员
        List<BasicPersonDTO> workingList = new ArrayList<BasicPersonDTO>();
        //脱岗人员
        List<BasicPersonDTO> departureList = new ArrayList<BasicPersonDTO>();
        //休息中人员
        List<BasicPersonDTO> sitBackList = new ArrayList<BasicPersonDTO>();
        //查询当天排班表,应该有哪些人出勤
        R<List<ScheduleObject>> scheduleResult = scheduleClient.listPersonForNow(tenantId);
        List<Long> scheduleVehicleIdList = new ArrayList<Long>();
        HashMap<Long, Person> scheduleVehicleMap = new HashMap<Long, Person>();
        if (scheduleResult != null && scheduleResult.getData() != null) {
            List<ScheduleObject> scheduleList = scheduleResult.getData();
            if (scheduleList != null && scheduleList.size() > 0) {
                log.info("今日应出勤人员:{}", JSON.toJSONString(scheduleList));
                scheduleList.stream().forEach(scheduleObject -> {
                    Long personId = scheduleObject.getEntityId();
                    Person person = PersonCache.getPersonById(tenantId, personId);
                    if (person != null && person.getIsIncumbency() != PersonConstant.IncumbencyStatus.UN) {
                        scheduleVehicleIdList.add(personId);
                        scheduleVehicleMap.put(personId, person);
                    }
                });
            }
        }
        List<Person> personList = getActivePerson(tenantId);
        for (Person person : personList) {
            if (scheduleVehicleMap.get(person.getId()) == null) {
                BasicPersonDTO basicPersonDTO = BeanUtil.copy(person, BasicPersonDTO.class);
                basicPersonDTO.setWorkStatus(PersonConstant.PersonStatus.OFF_ONLINE);
                basicPersonDTO.setWorkStatusName(PersonStatusEnum.getDescByValue(basicPersonDTO.getWorkStatus()));
                sitBackList.add(basicPersonDTO);
            }
        }

        PersonStatusStatDTO statusStatDTO = getworkperson(scheduleVehicleIdList, scheduleVehicleMap);

        statusStatDTO.setSitBackList(sitBackList);

        log.info("今日人员出勤统计:{}", JSON.toJSONString(statusStatDTO));
        return statusStatDTO;
    }

    private PersonStatusStatDTO getworkperson(List<Long> scheduleVehicleIdList, Map<Long, Person> scheduleVehicleMap) {
        PersonStatusStatDTO statusStatDTO = new PersonStatusStatDTO();
        if (null == scheduleVehicleIdList || 0 == scheduleVehicleIdList.size()) {
            return statusStatDTO;
        }
        //工作中人员
        List<BasicPersonDTO> workingList = new ArrayList<BasicPersonDTO>();
        //脱岗人员
        List<BasicPersonDTO> departureList = new ArrayList<BasicPersonDTO>();

        Map<Long, DevicePersonInfoDto> deviceInfoMap = deviceClient.getByEntityAndCategoryMap(scheduleVehicleIdList, DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE).getData();
        Map<Long, Boolean> isNeedWorkResultMap = scheduleClient.checkTodayNeedWorkMap(scheduleVehicleIdList, ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
        for (Long scheduleVehicledId : scheduleVehicleIdList) {
            Boolean isNeedWorkResult = isNeedWorkResultMap.get(scheduleVehicledId);
            if (null != isNeedWorkResult && isNeedWorkResult) {
                Person person = scheduleVehicleMap.get(scheduleVehicledId);
                BasicPersonDTO basicPersonDTO = BeanUtil.copy(person, BasicPersonDTO.class);
                DevicePersonInfoDto deviceInfo = deviceInfoMap.get(scheduleVehicledId);
                if (ObjectUtil.isNotEmpty(deviceInfo) && ObjectUtil.isNotEmpty(deviceInfo.getDeviceStatus()) && deviceInfo.getDeviceStatus().equals(0L)) {
                    //需要出勤且手表开启
                    basicPersonDTO.setWorkStatus(PersonConstant.PersonStatus.ONLINE);
                    basicPersonDTO.setWorkStatusName(PersonStatusEnum.getDescByValue(basicPersonDTO.getWorkStatus()));
                    workingList.add(basicPersonDTO);
                } else {
                    //没有绑定手表、手表关闭、手表异常关闭都视为离岗
                    basicPersonDTO.setWorkStatus(PersonConstant.PersonStatus.OFF_ONLINE_ALARM);
                    basicPersonDTO.setWorkStatusName(PersonStatusEnum.getDescByValue(basicPersonDTO.getWorkStatus()));
                    departureList.add(basicPersonDTO);
                }
            }
        }
        if (workingList.size() > 0) statusStatDTO.setWorkingList(workingList);
        if (departureList.size() > 0) statusStatDTO.setDepartureList(departureList);
        return statusStatDTO;


    }

    @Override
    public List<Person> getActivePerson(String tenantId) {
        PersonVO person = new PersonVO();
        person.setTenantId(tenantId);
        person.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        return listAll(person);
    }

    @Override
    public int getActivePersonCount(String tenantId) {
        PersonVO person = new PersonVO();
        person.setTenantId(tenantId);
        person.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        return countAll(person);
    }

    /**
     * 按租户统计查询部门在职员工数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<DeptStaffCountDTO> getDeptStaffCount(String tenantId) {
        return baseMapper.getDeptStaffCount(tenantId);
    }

    /**
     * 根据人员ID、月份获取该月的人体节律数据
     *
     * @param personId
     * @param month
     * @return
     */
    @Override
    public List<BodyBiologicalDTO> getBodyBiologicalInfo(Long personId, String month) {
        if (personId == null) {
            throw new ServiceException("请选择人员");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(month)) {
            throw new ServiceException("请选择要查询的月份");
        }

        Person person = baseMapper.selectById(personId);
        if (person == null) {
            throw new ServiceException("待查询的人员信息不存在");
        }

        Date birthday = person.getBirthday();
        if (birthday == null) {
            throw new ServiceException("人员生日为空,无法查询人体节律数据");
        }
        String birthdayStr = cn.hutool.core.date.DateUtil.format(birthday, TimeUtil.YYYY_MM_DD);
        String[] birthDayInfo = birthdayStr.split("-");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("year", birthDayInfo[0]);
        params.put("month", birthDayInfo[1]);
        params.put("day", birthDayInfo[2]);
        params.put("endyear", month.substring(0, 4));
        params.put("endmonth", month.substring(4, month.length()));
        String body = HttpUtil.get(PersonConstant.BODY_BIOLOGICAL_URL, params);
        if (StringUtil.isBlank(body)) {
            throw new ServiceException("计算人体生物节律异常,请稍候再试");
        }
        if (body.indexOf("values=") < 0 || body.indexOf("values_2=") < 0 || body.indexOf("values_3=") < 0) {
            throw new ServiceException("计算人体生物节律异常,请稍候再试");
        }
        String[] bodyInfo = body.split("&");
        String[] intelligenceInfo = bodyInfo[17].replaceAll("values=", "").split(",");
        String[] emotionInfo = bodyInfo[19].replaceAll("values_2=", "").split(",");
        String[] physicalInfo = bodyInfo[21].replaceAll("values_3=", "").split(",");
        List<String> daysOfMonth = TimeUtil.getDayListOfMonth(Integer.valueOf(month.substring(0, 4)), Integer.valueOf(month.substring(4, month.length())));
        List<BodyBiologicalDTO> result = new ArrayList<BodyBiologicalDTO>();
        for (int i = 0; i < daysOfMonth.size(); i++) {
            BodyBiologicalDTO bodyBiologicalDTO = new BodyBiologicalDTO(daysOfMonth.get(i), intelligenceInfo[i], physicalInfo[i], emotionInfo[i]);
            result.add(bodyBiologicalDTO);
        }
        return result;
    }

    /**
     * 计算指定日期的人体节律数据,暂时不使用该算法
     *
     * @param birthday
     * @param targetDate
     * @return
     */
    private static BodyBiologicalDTO getBodyBiological(String birthday, Date targetDate) {
        Date date1 = cn.hutool.core.date.DateUtil.parse(birthday, TimeUtil.YYYY_MM_DD);
        long betweenDay = cn.hutool.core.date.DateUtil.between(date1, targetDate, DateUnit.DAY) + 1;
        Long physicalDays = betweenDay % 23;
        Long emotionDays = betweenDay % 28;
        Long intelligenceDays = betweenDay % 33;
        return new BodyBiologicalDTO(cn.hutool.core.date.DateUtil.format(targetDate, TimeUtil.YYYY_MM_DD), String.valueOf(intelligenceDays), String.valueOf(physicalDays), String.valueOf(emotionDays));
    }

    /**
     * 根据personId获取微信用户信息
     *
     * @param personId
     * @return
     */
    @Override
    public WeChatUser getWechatUserByPersonId(Long personId) {
        PersonUserRel personUserRel = personUserRelService.getRelByUserOrPerson(null, personId);
        if (personUserRel == null || personUserRel.getId() == null) {
            return null;
        }
        R<WeChatUser> result = wechatUserClient.getWechatUserByUserId(personUserRel.getUserId());
        if (result != null && result.isSuccess()) {
            return result.getData();
        }
        return null;
    }

    /**
     * 获取人员绑定设备的状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public PersonDeviceStatusCountDTO getPersonDeviceStatusCount(String tenantId) {
        QueryWrapper<Person> queryWrapper = new QueryWrapper<Person>();
        queryWrapper.select("watch_device_status", "count(id) as count")
                .groupBy("watch_device_status")
                .eq("tenant_id", tenantId)
                .in("is_incumbency", PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        List<Map<String, Object>> result = baseMapper.selectMaps(queryWrapper);
        PersonDeviceStatusCountDTO personDeviceStatusCountDTO = new PersonDeviceStatusCountDTO();
        DeviceStatusCountDTO deviceStatusCountDTO = new DeviceStatusCountDTO();
        if (CollUtil.isNotEmpty(result)) {
            result.stream().forEach(deviceStatusResult -> {
                Integer watchDeviceStatus = Integer.valueOf(deviceStatusResult.get("watch_device_status").toString());
                Integer count = Integer.valueOf(deviceStatusResult.get("count").toString());
                switch (watchDeviceStatus.toString()) {
                    case DeviceConstant.DeviceStatus.NO:
                        deviceStatusCountDTO.setNoSingleCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.NO_DEV:
                        deviceStatusCountDTO.setUnBindCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.OFF:
                        deviceStatusCountDTO.setOffCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.ON:
                        deviceStatusCountDTO.setNormalCount(count);
                        break;
                    default:
                        deviceStatusCountDTO.setErrorOffCount(count);
                }
            });
            personDeviceStatusCountDTO.setPersonCount(deviceStatusCountDTO.getAllStatusCount());
            personDeviceStatusCountDTO.setOnPersonCount(deviceStatusCountDTO.getNormalCount());
            personDeviceStatusCountDTO.setOffPersonCount(deviceStatusCountDTO.getOffCount() + deviceStatusCountDTO.getErrorOffCount());
            personDeviceStatusCountDTO.setNodPersonCount(deviceStatusCountDTO.getUnBindCount() + deviceStatusCountDTO.getNoSingleCount());
        }
        personDeviceStatusCountDTO.setProjectCode(tenantId);
        return personDeviceStatusCountDTO;
    }

    /**
     * 根据员工姓名查询关联的帐号信息
     *
     * @param personName
     * @return
     */
    @Override
    public List<PersonAccountVO> listPersonAccount(String personName) {
        List<PersonAccountVO> list = baseMapper.listPersonAccount(personName);
        if (CollUtil.isNotEmpty(list)) {
            list.stream().forEach(personAccountVO -> {
                Long accountId = personAccountVO.getAccountId();
                User user = UserCache.getUser(accountId);
                if (user != null && user.getId() != null && user.getId() > 0L) {
                    personAccountVO.setAccount(user.getAccount());
                }
            });
        }
        return list;
    }

    /**
     * 批量获取人员绑定设备的状态统计
     *
     * @param projectCode
     * @return
     */
    @Override
    public List<PersonDeviceStatusCountDTO> listPersonDeviceStatusCount(String projectCode) {
        if (StringUtils.isEmpty(projectCode)) {
            throw new ServiceException("项目编号不能为空");
        }
        List<String> projectCodeList = Func.toStrList(projectCode);
        if (CollUtil.isNotEmpty(projectCodeList)) {
            List<PersonDeviceStatusCountDTO> list = projectCodeList.parallelStream().map(projectCodeStr -> {
                return this.getPersonDeviceStatusCount(projectCodeStr);
            }).collect(Collectors.toList());
            return list;
        }
        return null;
    }
}
