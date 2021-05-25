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
package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.BiScreenInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.support.Kv;
import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.vo.MenuVO;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 懒加载列表
     *
     * @param parentId
     * @param param
     * @return
     */
    List<MenuVO> lazyList(Long parentId, Map<String, Object> param);

    /**
     * 懒加载菜单列表
     *
     * @param parentId
     * @param param
     * @return
     */
    List<MenuVO> lazyMenuList(Long parentId, Map<String, Object> param);

    /**
     * 菜单树形结构
     *
     * @param roleId
     * @param topMenuId
     * @return
     */
    List<MenuVO> routes(String roleId, Long topMenuId);

    /**
     * 按钮树形结构
     *
     * @param roleId
     * @return
     */
    List<MenuVO> buttons(String roleId);

    /**
     * 树形结构
     *
     * @return
     */
    List<MenuVO> tree();

    /**
     * 授权树形结构
     *
     * @param user
     * @return
     */
    List<MenuVO> grantTree(BladeUser user);

    /**
     * 授权树形结构
     *
     * @param user
     * @return
     */
    List<MenuVO> grantTreeProject(BladeUser user);

    /**
     * 顶部菜单树形结构
     *
     * @param user
     * @return
     */
    List<MenuVO> grantTopTree(BladeUser user);

    /**
     * 数据权限授权树形结构
     *
     * @param user
     * @return
     */
    List<MenuVO> grantDataScopeTree(BladeUser user);

    /**
     * 接口权限授权树形结构
     *
     * @param user
     * @return
     */
    List<MenuVO> grantApiScopeTree(BladeUser user);

    /**
     * 默认选中节点
     *
     * @param roleIds
     * @return
     */
    List<String> roleTreeKeys(String roleIds);

    /**
     * 默认选中节点
     *
     * @param topMenuIds
     * @return
     */
    List<String> topTreeKeys(String topMenuIds);

    /**
     * 默认选中节点
     *
     * @param roleIds
     * @return
     */
    List<String> dataScopeTreeKeys(String roleIds);

    /**
     * 默认选中节点
     *
     * @param roleIds
     * @return
     */
    List<String> apiScopeTreeKeys(String roleIds);

    /**
     * 获取配置的角色权限
     *
     * @param user
     * @return
     */
    List<Kv> authRoutes(BladeUser user);

    /**
     * 删除菜单
     *
     * @param ids
     * @return
     */
    boolean removeMenu(String ids);

    /**
     * 提交
     *
     * @param menu
     * @return
     */
    boolean submit(Menu menu);

    /**
     * 获取当前用户可以看到的所有菜单
     *
     * @param bladeUser   用户信息
     * @param channel     渠道 1-Web 2-APP
     * @param projectCode 项目编码
     * @return
     */
    List<MenuVO> getUserRoleMenu(BladeUser bladeUser, final String channel, String projectCode);

    /**
     * 获取可以设置的大屏信息
     *
     * @param bladeUser
     * @return
     */
    List<BiScreenInfo> listMyBiScreen(BladeUser bladeUser);

    /**
     * 保存大屏信息
     *
     * @param biScreenInfoList
     * @return
     */
    boolean batchSaveBiScreen(List<BiScreenInfo> biScreenInfoList);

    /**
     * 保存大屏信息
     *
     * @param biScreenInfo
     * @return
     */
    boolean saveBiScreen(BiScreenInfo biScreenInfo);

    /**
     * 根据菜单ID获取大屏设置信息
     *
     * @param menuId
     * @param projectCode
     * @return
     */
    BiScreenInfo getBiScreenInfoByMenuId(Long menuId, String projectCode);
}
