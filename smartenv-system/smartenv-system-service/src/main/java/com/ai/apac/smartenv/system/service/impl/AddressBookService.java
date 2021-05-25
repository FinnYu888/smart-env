package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.mapper.DeptMapper;
import com.ai.apac.smartenv.system.service.IAddressBookService;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.ai.apac.smartenv.system.wrapper.DeptWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/16 11:56 上午
 **/
@Service
@Slf4j
public class AddressBookService implements IAddressBookService {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private IPersonClient personClient;

    /**
     * 获取部门树形结构
     *
     * @param tenantId
     * @return
     */
    @Override
    public DeptVO deptTree(String tenantId) {
        Tenant tenant = TenantCache.getTenantById(tenantId);
        DeptVO root = new DeptVO();
        root.setId(0L);
        root.setParentId(-1L);
        root.setDeptName(tenant.getTenantName());
        root.setFullName(tenant.getTenantName());
        root.setTenantId(tenantId);
        root.setDeptCategory(1);
        List<Dept> deptList = deptMapper.selectList(new LambdaQueryWrapper<Dept>().eq(Dept::getTenantId, tenantId));
        List<INode> children = DeptWrapper.build().listNodeVO(deptList);
        root.setChildren(children);
        List<Person> data = personClient.getPersonByTenant(tenantId).getData();
        root.setDeptPersonCount(data.size());
        return root;
    }

    /**
     * 查询联系人
     *
     * @param tenantId
     * @param deptId
     * @param name
     * @return
     */
    @Override
    public List<Person> getContactPerson(String tenantId, Long deptId, String name) {
        if (deptId != null && deptId == 0L) {
            return new ArrayList<Person>();
        }
        PersonVO queryCond = new PersonVO();
        queryCond.setTenantId(tenantId);
        queryCond.setPersonDeptId(deptId);
        queryCond.setPersonName(name);
        queryCond.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        R<List<Person>> result = personClient.listPerson(queryCond);
        return result.getData();
    }
}
