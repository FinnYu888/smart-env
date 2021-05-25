package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.vo.ContactPersonVO;
import com.ai.apac.smartenv.system.vo.DeptVO;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/16 11:42 上午
 **/
public interface IAddressBookService {

    /**
     * 获取部门树形结构
     *
     * @param tenantId
     * @return
     */
    DeptVO deptTree(String tenantId);

    /**
     * 查询联系人
     *
     * @param deptId
     * @return
     */
    List<Person> getContactPerson(String tenantId, Long deptId, String name);

}
