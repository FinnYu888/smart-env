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
package com.ai.apac.smartenv.person.service;

import com.ai.apac.smartenv.person.dto.BodyBiologicalDTO;
import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.dto.PersonStatusStatDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 * 人员信息表 服务类
 *
 * @author Blade
 * @since 2020-02-14
 */
public interface IPersonService extends BaseService<Person> {

    /**
     * 自定义分页
     *
     * @param page
     * @param person
     * @return
     */
    IPage<PersonVO> selectPersonPage(IPage<PersonVO> page, PersonVO person);

    /**
     * @param person
     * @return
     * @Function: IPersonService::updatePersonInfoById
     * @Description: 修改，可置空
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月20日 下午2:45:48
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    Integer updatePersonInfoById(PersonVO person);

    /**
     * @param person
     * @param query
     * @return
     * @Function: IPersonService::page
     * @Description: 分页查询
     * @version: v1.0.0
     * @author: zhaoaj
     * @param isBindTerminal 
     * @date: 2020年2月22日 上午11:02:41
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    IPage<Person> page(PersonVO person, Query query,String deviceStatus, String isBindTerminal);

    /**
     * @param personVO
     * @return
     * @Function: IPersonService::savePersonInfo
     * @Description: 保存人员信息
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月22日 上午11:17:44
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    boolean savePersonInfo(PersonVO personVO);

    /**
     * @param person
     * @return
     * @Function: IPersonService::updatePersonInfo
     * @Description: 修改人员信息
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月22日 上午11:20:24
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    Integer updatePersonInfo(PersonVO person);

    List<PersonNode> treeByDept(String nodeName, String tenantId, Person person, List<Long> invalidEntityIdList);

    /**
     * @param person
     * @return
     * @Function: IPersonService::listAll
     * @Description: 查询所有
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月24日 下午4:12:00
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    List<Person> listAll(PersonVO person);

    /**
     * @param person
     * @param query
     * @param vehicleId
     * @return
     * @Function: IPersonVehicleRelService::pageForVehicle
     * @Description: 查询绑定驾驶员信息
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月25日 下午4:14:38
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    IPage<Person> pageForVehicle(PersonVO person, Query query, Long vehicleId);

    IPage<Person> pageForGroup(PersonVO person, Query query, Long groupId);


    boolean removePerson(List<Long> idList);

    /**
     * @param deptId
     * @return
     * @Function: IPersonService::getPersonByDeptId
     * @Description: 根据部门查人
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月7日 下午9:05:41
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    List<Person> getPersonByDeptId(Long deptId);

    /**
     * 获取人员树
     *
     * @param treeType 1-按职位分组，2-按部门分组
     * @param tenantId 租户ID
     * @return
     */
    List<PersonNode> getPersonTree(Integer treeType, String tenantId);

    /**
     * 获取人员当天出勤状态统计
     *
     * @param tenantId
     * @return
     */
    PersonStatusStatDTO getPersonStatusStatToday(String tenantId);

    /**
     * @param tenantId
     * @return
     * @Function: IPersonService::getActivePerson
     * @Description: 获取在职人员
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:29:40
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    List<Person> getActivePerson(String tenantId);

    /**
     * @param person
     * @return
     * @Function: IPersonService::countAll
     * @Description: 获取所有人员数量
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:29:51
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    int countAll(PersonVO person);

    /**
     * @param tenantId
     * @return
     * @Function: IPersonService::getActivePersonCount
     * @Description: 获取在职人员数量
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月23日 下午12:30:04
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    int getActivePersonCount(String tenantId);

	/**
	 * 根据查询条件查询用户
	 * @param queryCond
	 * @return
	 */
    List<Person> listPersonByCond(Person queryCond);

    /**
     * 按租户统计查询部门在职员工数量
     * @param tenantId
     * @return
     */
    List<DeptStaffCountDTO> getDeptStaffCount(String tenantId);

    Integer updatePersonWatchStateById(Long state, Long personId);

    /**
     * 根据人员ID、月份获取该月的人体节律数据
     * @param personId
     * @return
     */
    List<BodyBiologicalDTO> getBodyBiologicalInfo(Long personId,String month);

    /**
     * 根据personId获取微信用户信息
     * @param personId
     * @return
     */
    WeChatUser getWechatUserByPersonId(Long personId);

    /**
     * 获取人员绑定设备的状态统计
     * @param tenantId
     * @return
     */
    PersonDeviceStatusCountDTO getPersonDeviceStatusCount(String tenantId);

    /**
     * 批量获取人员绑定设备的状态统计
     * @param tenantId
     * @return
     */
    List<PersonDeviceStatusCountDTO> listPersonDeviceStatusCount(String tenantId);

    /**
     * 根据员工姓名查询关联的帐号信息
     * @param personName
     * @return
     */
    List<PersonAccountVO> listPersonAccount(String personName);
}
