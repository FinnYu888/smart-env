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
package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.mapper.DeptMapper;
import com.ai.apac.smartenv.system.service.IDeptService;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

    @Autowired
    private IUserClient userClient;

    @Autowired
    private IPersonClient personClient;

    @Autowired
    private IVehicleClient vehicleClient;

    @Override
    public List<DeptVO> lazyList(String tenantId, Long parentId, Map<String, Object> param) {
        if (AuthUtil.isAdministrator()) {
            tenantId = StringPool.EMPTY;
        }
        String paramTenantId = Func.toStr(param.get("tenantId"));
        if (Func.isNotEmpty(paramTenantId) && AuthUtil.isAdministrator()) {
            tenantId = paramTenantId;
        }
        if (Func.isEmpty(param.get("parentId"))) {
            parentId = null;
        }
        return baseMapper.lazyList(tenantId, parentId, param);
    }


    @Override
    public List<DeptVO> tree(String tenantId) {
        if (AuthUtil.isAdministrator()) {
            tenantId = StringPool.EMPTY;
        }
        return ForestNodeMerger.merge(baseMapper.tree(tenantId));
    }

    @Override
    public List<DeptVO> lazyTree(String tenantId, Long parentId) {
        if (AuthUtil.isAdministrator()) {
            tenantId = StringPool.EMPTY;
        }
        return ForestNodeMerger.merge(baseMapper.lazyTree(tenantId, parentId));
    }


    @Override
    public List<String> getDeptNames(String deptIds) {
        return baseMapper.getDeptNames(Func.toLongArray(deptIds));
    }

    /**
     * 获取部门全名
     *
     * @param deptIds
     * @return
     */
    @Override
    public List<String> getDeptFullNames(String deptIds) {
        return baseMapper.getDeptFullNames(Func.toLongArray(deptIds));
    }

    @Override
    public List<Dept> getDeptChild(Long deptId) {
        return baseMapper.selectList(Wrappers.<Dept>query().lambda().like(Dept::getAncestors, deptId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean removeDept(String ids) {
        Integer cnt = baseMapper.selectCount(Wrappers.<Dept>query().lambda().in(Dept::getParentId, Func.toLongList(ids)));
        if (cnt > 0) {
            throw new ServiceException("请先删除下级部门!");
        }
        List<Long> deptIdList = Func.toLongList(ids);
        //如果该部门已经与其他员工关联则不能被删除
        deptIdList.stream().forEach(deptId -> {
            R<List<User>> userListData = userClient.userInfoByDeptId(deptId);
            if (userListData.isSuccess() && userListData.getData() != null
                    && userListData.getData().size() > 0) {
                throw new ServiceException("该部门已经关联操作员,不能被删除");
            }

            //部门是否与人员关联
            R<Integer> personCountResult = personClient.getPersonCountByDeptId(deptId);
            if (personCountResult.isSuccess() && personCountResult.getData() != null
                    && personCountResult.getData() > 0) {
                throw new ServiceException("该部门已经关联员工,不能被删除");
            }

            //部门是否车辆关联
            R<Integer> vehicleCountResult = vehicleClient.vehicleCountByDeptId(deptId);
            if (vehicleCountResult.isSuccess() && vehicleCountResult.getData() != null
                    && vehicleCountResult.getData() > 0) {
                throw new ServiceException("该部门已经关联车辆,不能被删除");
            }
        });
        boolean result = removeByIds(deptIdList);
        if (result) {
            deptIdList.stream().forEach(deptId -> {
                Dept dept = getDeptById(Long.valueOf(deptId));
                DeptCache.saveOrUpdateDept(dept);
            });
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean submit(Dept dept) {
        if (Func.isEmpty(dept.getParentId())) {
            dept.setTenantId(AuthUtil.getTenantId());
            dept.setParentId(BladeConstant.TOP_PARENT_ID);
            dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
        }
        if (dept.getParentId() > 0) {
            Dept parent = getById(dept.getParentId());
            if (parent == null) {
                throw new ServiceException("父节点不存在!");
            }
            if (Func.toLong(dept.getParentId()) == Func.toLong(dept.getId())) {
                throw new ServiceException("父节点不可选择自身!");
            }
            dept.setTenantId(parent.getTenantId());
            String ancestors = parent.getAncestors() + StringPool.COMMA + dept.getParentId();
            dept.setAncestors(ancestors);
        }
        if (StringUtil.isNotBlank(dept.getDeptName()) && dept.getDeptName().length() > 45) {
            throw new ServiceException("部门名称不能超过22个中文字符或44个英文字符");
        }
        if (StringUtil.isNotBlank(dept.getFullName()) && dept.getFullName().length() > 45) {
            throw new ServiceException("部门全称不能超过22个中文字符或44个英文字符");
        }
        dept.setIsDeleted(BladeConstant.DB_NOT_DELETED);
        boolean result = saveOrUpdate(dept);
        DeptCache.saveOrUpdateDept(dept);
        return result;
    }

    /**
     * 根据条件查询部门
     *
     * @param dept
     * @return
     */
    @Override
    public List<Dept> selectDept(Dept dept) {
        return baseMapper.selectDept(dept);
    }

    /**
     * 根据主键查询部门,忽略逻辑删除状态
     *
     * @param deptId
     * @return
     */
    @Override
    public Dept getDeptById(Long deptId) {
        Dept dept = new Dept();
        dept.setId(deptId);
        List<Dept> deptList = baseMapper.selectDept(dept);
        if (deptList == null || deptList.size() == 0) {
            return null;
        }
        return deptList.get(0);
    }


	@Override
	public List<Long> getAllChildDepts(Long deptId, List<Long> deptIdList) {
		if (deptId != null) {
            deptIdList.add(deptId);
            List<Dept> deptChild = getDeptChild(deptId);
            if (deptChild != null && !deptChild.isEmpty()) {
                for (Dept dept : deptChild) {
                    if (dept.getIsDeleted() == null || dept.getIsDeleted() == 1) {
                        continue;
                    }
                    Long id = dept.getId();
                    deptIdList = getAllChildDepts(id, deptIdList);
                }
            }
        }
        return deptIdList;
	}
}
