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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.oss.cache.OssCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.dto.MenuDTO;
import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.mapper.MenuMapper;
import com.ai.apac.smartenv.system.service.*;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.MenuVO;
import com.ai.apac.smartenv.system.wrapper.MenuWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.constant.CommonConstant.API_SCOPE_CATEGORY;
import static com.ai.apac.smartenv.common.constant.CommonConstant.DATA_SCOPE_CATEGORY;
import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMIN;
import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMINISTRATOR;
import static org.springblade.core.cache.constant.CacheConstant.MENU_CACHE;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    private IRoleMenuService roleMenuService;
    private IRoleScopeService roleScopeService;
    private ITopMenuSettingService topMenuSettingService;
    private IUserClient userClient;
    private IRoleService roleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<MenuVO> lazyList(Long parentId, Map<String, Object> param) {
        if (Func.isEmpty(Func.toStr(param.get("parentId")))) {
            parentId = null;
        }
        return baseMapper.lazyList(parentId, param);
    }

    @Override
    public List<MenuVO> lazyMenuList(Long parentId, Map<String, Object> param) {
        if (Func.isEmpty(Func.toStr(param.get("parentId")))) {
            parentId = null;
        }
        return baseMapper.lazyMenuList(parentId, param);
    }


    @Override
    public List<MenuVO> routes(String roleId, Long topMenuId) {
        if (StringUtil.isBlank(roleId)) {
            return null;
        }
        List<Menu> allMenus = baseMapper.allMenu();
        List<Menu> roleMenus = (SecureUtil.isAdministrator() && Func.isEmpty(topMenuId)) ? allMenus : baseMapper.roleMenu(Func.toLongList(roleId), topMenuId);
        return buildRoutes(allMenus, roleMenus);
    }

    private List<MenuVO> buildRoutes(List<Menu> allMenus, List<Menu> roleMenus) {
        List<Menu> routes = new LinkedList<>(roleMenus);
        roleMenus.forEach(roleMenu -> recursion(allMenus, routes, roleMenu));
        routes.sort(Comparator.comparing(Menu::getSort));
        MenuWrapper menuWrapper = new MenuWrapper();
        List<Menu> collect = routes.stream().filter(x -> Func.equals(x.getCategory(), 1)).collect(Collectors.toList());
        return menuWrapper.listNodeVO(collect);
    }

    private void recursion(List<Menu> allMenus, List<Menu> routes, Menu roleMenu) {
        Optional<Menu> menu = allMenus.stream().filter(x -> Func.equals(x.getId(), roleMenu.getParentId())).findFirst();
        if (menu.isPresent() && !routes.contains(menu.get())) {
            routes.add(menu.get());
            recursion(allMenus, routes, menu.get());
        }
    }

    @Override
    public List<MenuVO> buttons(String roleId) {
        List<Menu> buttons = (SecureUtil.isAdministrator()) ? baseMapper.allButtons() : baseMapper.buttons(Func.toLongList(roleId));
        MenuWrapper menuWrapper = new MenuWrapper();
        return menuWrapper.listNodeVO(buttons);
    }

    @Override
    public List<MenuVO> tree() {
        return ForestNodeMerger.merge(baseMapper.tree());
    }

    @Override
    public List<MenuVO> grantTree(BladeUser user) {
        return ForestNodeMerger.merge(user.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID) ? baseMapper.grantTree() : baseMapper.grantTreeByRole(Func.toLongList(user.getRoleId())));
    }

    /**
     * 授权树形结构
     *
     * @param bladeUser
     * @return
     */
    @Override
    public List<MenuVO> grantTreeProject(BladeUser bladeUser) {
        List<MenuVO> menuVOList = new ArrayList<MenuVO>();
        //获取用户信息
        User user = UserCache.getUser(bladeUser.getUserId());
        //由于目前一个帐号可以关联多个项目,色色也不同，因此需要根据当前登录用户过滤
        if (user.getId().equals(1123598821738675201L)) {
            menuVOList = baseMapper.grantTreeProject();
        } else {
            List<Long> roleIdList = Func.toLongList(user.getRoleId());
            String tenantId = bladeUser.getTenantId();
            List<Long> finalRoleIdList = new ArrayList<Long>();
            for (Long roleId : roleIdList) {
                Role role = RoleCache.getRole(String.valueOf(roleId));
                if (role != null && role.getId() != null && role.getTenantId().equals(tenantId)) {
                    finalRoleIdList.add(roleId);
                }
            }
            if (CollUtil.isEmpty(finalRoleIdList)) {
                log.error("用户[{}]没有操作权限", bladeUser.getUserName());
                return menuVOList;
            }
            menuVOList = baseMapper.grantTreeByRoleProject(finalRoleIdList);
        }
        menuVOList = ForestNodeMerger.merge(menuVOList);
        return menuVOList;
    }

    @Override
    public List<MenuVO> grantTopTree(BladeUser user) {
        return ForestNodeMerger.merge(user.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID) ? baseMapper.grantTopTree() : baseMapper.grantTopTreeByRole(Func.toLongList(user.getRoleId())));
    }

    @Override
    public List<MenuVO> grantDataScopeTree(BladeUser user) {
        return ForestNodeMerger.merge(user.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID) ? baseMapper.grantDataScopeTree() : baseMapper.grantDataScopeTreeByRole(Func.toLongList(user.getRoleId())));
    }

    @Override
    public List<MenuVO> grantApiScopeTree(BladeUser user) {
        return ForestNodeMerger.merge(user.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID) ? baseMapper.grantApiScopeTree() : baseMapper.grantApiScopeTreeByRole(Func.toLongList(user.getRoleId())));
    }

    @Override
    public List<String> roleTreeKeys(String roleIds) {
        List<RoleMenu> roleMenus = roleMenuService.list(Wrappers.<RoleMenu>query().lambda().in(RoleMenu::getRoleId, Func.toLongList(roleIds)));
        return roleMenus.stream().map(roleMenu -> Func.toStr(roleMenu.getMenuId())).collect(Collectors.toList());
    }

    @Override
    public List<String> topTreeKeys(String topMenuIds) {
        List<TopMenuSetting> settings = topMenuSettingService.list(Wrappers.<TopMenuSetting>query().lambda().in(TopMenuSetting::getTopMenuId, Func.toLongList(topMenuIds)));
        return settings.stream().map(setting -> Func.toStr(setting.getMenuId())).collect(Collectors.toList());
    }

    @Override
    public List<String> dataScopeTreeKeys(String roleIds) {
        List<RoleScope> roleScopes = roleScopeService.list(Wrappers.<RoleScope>query().lambda().eq(RoleScope::getScopeCategory, DATA_SCOPE_CATEGORY).in(RoleScope::getRoleId, Func.toLongList(roleIds)));
        return roleScopes.stream().map(roleScope -> Func.toStr(roleScope.getScopeId())).collect(Collectors.toList());
    }

    @Override
    public List<String> apiScopeTreeKeys(String roleIds) {
        List<RoleScope> roleScopes = roleScopeService.list(Wrappers.<RoleScope>query().lambda().eq(RoleScope::getScopeCategory, API_SCOPE_CATEGORY).in(RoleScope::getRoleId, Func.toLongList(roleIds)));
        return roleScopes.stream().map(roleScope -> Func.toStr(roleScope.getScopeId())).collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = MENU_CACHE, key = "'auth:routes:' + #user.roleId")
    public List<Kv> authRoutes(BladeUser user) {
        List<MenuDTO> routes = baseMapper.authRoutes(Func.toLongList(user.getRoleId()));
        List<Kv> list = new ArrayList<>();
        routes.forEach(route -> list.add(Kv.create().set(route.getPath(), Kv.create().set("authority", Func.toStrArray(route.getAlias())))));
        return list;
    }

    @Override
    public boolean removeMenu(String ids) {
        Integer cnt = baseMapper.selectCount(Wrappers.<Menu>query().lambda().in(Menu::getParentId, Func.toLongList(ids)));
        if (cnt > 0) {
            throw new ServiceException("请先删除子节点!");
        }
        return removeByIds(Func.toLongList(ids));
    }

    @Override
    public boolean submit(Menu menu) {
        if (menu.getParentId() == null && menu.getId() == null) {
            menu.setParentId(BladeConstant.TOP_PARENT_ID);
        }
        menu.setIsDeleted(BladeConstant.DB_NOT_DELETED);
        return saveOrUpdate(menu);
    }

    /**
     * 获取当前用户可以看到的所有菜单
     *
     * @param bladeUser 用户信息
     * @param channel   渠道 1-Web 2-APP 3-BI大屏
     * @return
     */
    @Override
    public List<MenuVO> getUserRoleMenu(BladeUser bladeUser, final String channel, String projectCode) {
        List<MenuVO> menuList = listMenuVOByUser(bladeUser);
        if (menuList != null && menuList.size() > 0) {
            menuList = menuList.stream()
                    .filter(menuVO -> menuVO.getRemark() != null && menuVO.getRemark().equals(channel)
                            && menuVO.getCategory().equals(1))
                    .collect(Collectors.toList());
        }
        return menuList;
    }

    /**
     * @param user
     * @return
     */
    private List<MenuVO> listMenuVOByUser(BladeUser user) {
        List<MenuVO> menuVOList = null;
        if (user.getUserId().equals(1123598821738675201L)) {
            menuVOList = baseMapper.grantTreeProject();
        } else if (user.getRoleGroup().equalsIgnoreCase(ADMIN) || user.getRoleGroup().equalsIgnoreCase(ADMINISTRATOR)) {
            //传入projectCode代表是查询该项目的管理员角色的菜单数据
            List<Long> finalRoleIdList = new ArrayList<Long>();
            List<Role> adminRoleList = roleService.list(new LambdaQueryWrapper<Role>().eq(Role::getTenantId, user.getTenantId()).eq(Role::getRoleAlias, ADMIN));
            if (CollUtil.isNotEmpty(adminRoleList)) {
                finalRoleIdList = adminRoleList.stream().map(role -> {
                    return role.getId();
                }).collect(Collectors.toList());
            }
            if (CollUtil.isNotEmpty(finalRoleIdList)) {
                menuVOList = baseMapper.grantTreeByRoleProject(finalRoleIdList);
            } else {
                return null;
            }
        } else {
            return grantTreeProject(user);
        }
        menuVOList = ForestNodeMerger.merge(menuVOList);
        return menuVOList;
    }

    /**
     * 获取可以设置的大屏信息
     *
     * @param bladeUser
     * @return
     */
    @Override
    public List<BiScreenInfo> listMyBiScreen(BladeUser bladeUser) {
        String projectCode = bladeUser.getTenantId();
        List<MenuVO> allBiScreenMenu = getUserRoleMenu(bladeUser, SystemConstant.Channel.BI_SCREEN, projectCode);
        if (CollUtil.isNotEmpty(allBiScreenMenu)) {
            List<BiScreenInfo> biScreenInfoList = allBiScreenMenu.stream().map(menuVO -> {
                BiScreenInfo biScreenInfo = getBiScreenInfoByMenuId(menuVO.getId(), projectCode);
                if (biScreenInfo == null) {
                    biScreenInfo = new BiScreenInfo();
                    biScreenInfo.setMenuId(menuVO.getId());
                    biScreenInfo.setCreateTime(new Date());
                    biScreenInfo.setUpdateTime(new Date());
                    biScreenInfo.setPath(menuVO.getPath());
                    biScreenInfo.setScreenCode(menuVO.getCode());
                    biScreenInfo.setScreenName(menuVO.getName());
                    biScreenInfo.setTitle(menuVO.getName());
                    biScreenInfo.setProjectCode(projectCode);
                    biScreenInfo.setSort(menuVO.getSort());
                }
                String previewFileName = DictCache.getValue("bi_screen_preview", menuVO.getCode());
                String previewPath = OssCache.getLink("smartenv", previewFileName);
                biScreenInfo.setPriviewPath(previewPath);
                return biScreenInfo;
            }).collect(Collectors.toList());
            return biScreenInfoList;
        }
        return null;
    }

    /**
     * 保存大屏信息
     *
     * @param biScreenInfoList
     * @return
     */
    @Override
    public boolean batchSaveBiScreen(List<BiScreenInfo> biScreenInfoList) {
        return false;
    }

    /**
     * 保存大屏信息
     *
     * @param biScreenInfo
     * @return
     */
    @Override
    public boolean saveBiScreen(BiScreenInfo biScreenInfo) {
        if (StringUtils.isEmpty(biScreenInfo.getTitle())) {
            throw new ServiceException("大屏标题不能为空");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("menu_id").is(biScreenInfo.getMenuId()).and("project_code").is(biScreenInfo.getProjectCode()));
        List<BiScreenInfo> biScreenInfoList = mongoTemplate.find(query, BiScreenInfo.class);
        if (CollUtil.isEmpty(biScreenInfoList)) {
            biScreenInfo.setId(IdUtil.objectId());
            mongoTemplate.save(biScreenInfo);
        } else {
            biScreenInfoList.stream().forEach(biScreenInfoTmp -> {
                biScreenInfo.setId(IdUtil.objectId());
                biScreenInfoTmp.setUpdateTime(new Date());
                biScreenInfoTmp.setSort(biScreenInfo.getSort());
                biScreenInfoTmp.setTitle(biScreenInfo.getTitle());
                Update update = new Update();
                update.set("title", biScreenInfo.getTitle());
                update.set("sort", biScreenInfo.getSort());
                mongoTemplate.updateFirst(query, update, BiScreenInfo.class);
            });
        }
        return true;
    }

    /**
     * 根据菜单ID获取大屏设置信息
     *
     * @param menuId
     * @param projectCode
     * @return
     */
    @Override
    public BiScreenInfo getBiScreenInfoByMenuId(Long menuId, String projectCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("menu_id").is(menuId).and("project_code").is(projectCode));
        List<BiScreenInfo> biScreenInfoList = mongoTemplate.find(query, BiScreenInfo.class);
        if (CollUtil.isNotEmpty(biScreenInfoList)) {
            return biScreenInfoList.get(0);
        }
        return null;
    }
}
