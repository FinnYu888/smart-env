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
package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.MenuCache;
import com.ai.apac.smartenv.system.entity.BiScreenInfo;
import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.entity.TopMenu;
import com.ai.apac.smartenv.system.service.IMenuService;
import com.ai.apac.smartenv.system.service.ITopMenuService;
import com.ai.apac.smartenv.system.vo.CheckedTreeVO;
import com.ai.apac.smartenv.system.vo.GrantTreeVO;
import com.ai.apac.smartenv.system.vo.MenuVO;
import com.ai.apac.smartenv.system.wrapper.MenuWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.MENU_CACHE;


/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/menu")
@Api(value = "菜单", tags = "菜单")
public class MenuController extends BladeController {

    private IMenuService menuService;
    private ITopMenuService topMenuService;

    private IOssClient ossClient;

    /**
     * 详情
     */
    @GetMapping("/detail")
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入menu")
    @ApiLog(value = "查看菜单详情")
    public R<MenuVO> detail(Menu menu) {
        Menu detail = menuService.getOne(Condition.getQueryWrapper(menu));
        return R.data(MenuWrapper.build().entityVO(detail));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "菜单编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "菜单名称", paramType = "query", dataType = "string")
    })
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入menu")
    @ApiLog(value = "查询菜单列表")
    public R<List<MenuVO>> list(@ApiIgnore @RequestParam Map<String, Object> menu) {
        List<Menu> list = menuService.list(Condition.getQueryWrapper(menu, Menu.class).lambda().orderByAsc(Menu::getSort));
        return R.data(MenuWrapper.build().listNodeVO(list));
    }

    /**
     * 查询APP菜单列表
     */
    @GetMapping("/appMenus")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询APP菜单列表", notes = "传入menu")
    @ApiLog(value = "查询APP菜单列表")
    public R<List<MenuVO>> appMenuList(@RequestParam(required = false) String projectCode, BladeUser bladeUser) {
        List<MenuVO> menuList = menuService.getUserRoleMenu(bladeUser, SystemConstant.Channel.MINI_APP, projectCode);
        return R.data(menuList);
    }

    /**
     * 查询WEB菜单列表
     */
    @GetMapping("/webMenus")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询WEB菜单列表", notes = "传入menu")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "查询WEB端菜单列表")
    public R<List<MenuVO>> webMenuList(@RequestParam(required = false) String projectCode, BladeUser bladeUser) {
        List<MenuVO> menuList = menuService.getUserRoleMenu(bladeUser, SystemConstant.Channel.WEB, projectCode);
        return R.data(menuList);
    }

    /**
     * 查询大屏菜单列表
     */
    @GetMapping("/biScreenMenus")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "查询大屏菜单列表", notes = "传入menu")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "查询WEB端菜单列表")
    public R<List<MenuVO>> biScreenMenuList(@RequestParam(required = false) String projectCode, BladeUser bladeUser) {
        List<MenuVO> menuList = menuService.getUserRoleMenu(bladeUser, SystemConstant.Channel.BI_SCREEN, projectCode);
        return R.data(menuList);
    }

    /**
     * 懒加载列表
     */
    @GetMapping("/lazy-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "菜单编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "菜单名称", paramType = "query", dataType = "string")
    })
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "懒加载列表", notes = "传入menu")
    public R<List<MenuVO>> lazyList(Long parentId, @ApiIgnore @RequestParam Map<String, Object> menu) {
        List<MenuVO> list = menuService.lazyList(parentId, menu);
        return R.data(MenuWrapper.build().listNodeLazyVO(list));
    }

    /**
     * 菜单列表
     */
    @GetMapping("/menu-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "菜单编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "菜单名称", paramType = "query", dataType = "string")
    })
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "菜单列表", notes = "传入menu")
    public R<List<MenuVO>> menuList(@ApiIgnore @RequestParam Map<String, Object> menu) {
        List<Menu> list = menuService.list(Condition.getQueryWrapper(menu, Menu.class).lambda().eq(Menu::getCategory, 1).orderByAsc(Menu::getSort));
        return R.data(MenuWrapper.build().listNodeVO(list));
    }

    /**
     * 懒加载菜单列表
     */
    @GetMapping("/lazy-menu-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "菜单编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "菜单名称", paramType = "query", dataType = "string")
    })
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "懒加载菜单列表", notes = "传入menu")
    public R<List<MenuVO>> lazyMenuList(Long parentId, @ApiIgnore @RequestParam Map<String, Object> menu) {
        List<MenuVO> list = menuService.lazyMenuList(parentId, menu);
        return R.data(MenuWrapper.build().listNodeLazyVO(list));
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @CacheEvict(cacheNames = {MENU_CACHE}, allEntries = true)
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入menu")
    public R submit(@Valid @RequestBody Menu menu) {
        return R.status(menuService.submit(menu));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @CacheEvict(cacheNames = {MENU_CACHE}, allEntries = true)
//	@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(menuService.removeMenu(ids));
    }

    /**
     * 前端菜单数据
     */
    @GetMapping("/routes")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "前端菜单数据", notes = "前端菜单数据")
    public R<List<MenuVO>> routes(BladeUser user, Long topMenuId) {
        List<MenuVO> list = menuService.routes((user == null) ? null : user.getRoleId(), topMenuId);
        return R.data(list);
    }

    /**
     * 前端按钮数据
     */
    @GetMapping("/buttons")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "前端按钮数据", notes = "前端按钮数据")
    public R<List<MenuVO>> buttons(BladeUser user) {
        List<MenuVO> list = menuService.buttons(user.getRoleId());
        return R.data(list);
    }

    /**
     * 获取菜单树形结构
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    public R<List<MenuVO>> tree() {
        List<MenuVO> tree = menuService.tree();
        return R.data(tree);
    }

    /**
     * 获取权限分配树形结构
     */
    @GetMapping("/grant-tree")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "权限分配树形结构", notes = "权限分配树形结构")
    public R<GrantTreeVO> grantTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        vo.setMenu(menuService.grantTree(user));
        vo.setDataScope(menuService.grantDataScopeTree(user));
        vo.setApiScope(menuService.grantApiScopeTree(user));
        return R.data(vo);
    }

    /**
     * 获取权限分配树形结构
     */
    @GetMapping("/role-tree-keys")
    @ApiOperationSupport(order = 13)
    @ApiOperation(value = "角色所分配的树", notes = "角色所分配的树")
    public R<CheckedTreeVO> roleTreeKeys(String roleIds) {
        CheckedTreeVO vo = new CheckedTreeVO();
        vo.setMenu(menuService.roleTreeKeys(roleIds));
//        vo.setDataScope(menuService.dataScopeTreeKeys(roleIds));
//        vo.setApiScope(menuService.apiScopeTreeKeys(roleIds));
        return R.data(vo);
    }

    /**
     * 获取顶部菜单树形结构
     */
    @GetMapping("/grant-top-tree")
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = "顶部菜单树形结构", notes = "顶部菜单树形结构")
    public R<GrantTreeVO> grantTopTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        vo.setMenu(menuService.grantTopTree(user));
        return R.data(vo);
    }

    /**
     * 获取顶部菜单树形结构
     */
    @GetMapping("/top-tree-keys")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "顶部菜单所分配的树", notes = "顶部菜单所分配的树")
    public R<CheckedTreeVO> topTreeKeys(String topMenuIds) {
        CheckedTreeVO vo = new CheckedTreeVO();
        vo.setMenu(menuService.topTreeKeys(topMenuIds));
        return R.data(vo);
    }

    /**
     * 顶部菜单数据
     */
    @GetMapping("/top-menu")
    @ApiOperationSupport(order = 16)
    @ApiOperation(value = "顶部菜单数据", notes = "顶部菜单数据")
    public R<List<TopMenu>> topMenu(BladeUser user) {
        if (Func.isEmpty(user)) {
            return null;
        }
        List<TopMenu> list = topMenuService.list();
        return R.data(list);
    }

    /**
     * 获取配置的角色权限
     */
    @GetMapping("auth-routes")
    @ApiOperationSupport(order = 17)
    @ApiOperation(value = "菜单的角色权限")
    public R<List<Kv>> authRoutes(BladeUser user) {
        if (Func.isEmpty(user)) {
            return null;
        }
        return R.data(menuService.authRoutes(user));
    }

    /**
     * 获取WEB门户的权限分配树形结构
     */
    @GetMapping("/grant-web-tree")
    @ApiOperationSupport(order = 18)
    @ApiOperation(value = "WEB门户权限分配树形结构", notes = "WEB门户权限分配树形结构")
    @ApiLog(value = "获取WEB门户的权限分配树形结构")
    public R<GrantTreeVO> grantWebTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        List<MenuVO> menuList = menuService.grantTreeProject(user);
        if (menuList != null && menuList.size() > 0) {
            menuList = menuList.stream()
                    .filter(menuVO -> menuVO.getRemark() != null && menuVO.getCategory() != null && menuVO.getRemark().equals("smartenv-web"))
                    .collect(Collectors.toList());
        }
        vo.setMenu(menuList);
//        vo.setDataScope(menuService.grantDataScopeTree(user));
//        vo.setApiScope(menuService.grantApiScopeTree(user));
        return R.data(vo);
    }

    /**
     * 懒加载Web门户权限树
     */
    @GetMapping("/web-lazy-tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentMenuId", value = "父菜单ID", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "parentMenuCode", value = "父菜单Code", paramType = "query", dataType = "string"),
    })
    @ApiOperationSupport(order = 19)
    @ApiOperation(value = "懒加载Web门户权限树", notes = "懒加载Web门户权限树")
    @ApiLog(value = "懒加载Web门户权限树")
    public R<List<MenuVO>> lazyWebTree(@RequestParam(required = false) Long parentMenuId, @RequestParam(required = false) String parentMenuCode) {
        if (parentMenuId == null && StringUtils.isBlank(parentMenuCode)) {
            throw new ServiceException(ResultCode.PARAM_MISS);
        }
        Menu parentMenu = null;
        List<Menu> childrenMenu = null;
        if (parentMenuId != null) {
            childrenMenu = MenuCache.getWebChildrenMenu(parentMenuId);
        } else if (StringUtils.isNotBlank(parentMenuCode)) {
            parentMenu = MenuCache.getMenuByCode(parentMenuCode);
            if (parentMenu == null) {
                return R.data(new ArrayList<MenuVO>());
            }
            childrenMenu = MenuCache.getWebChildrenMenu(parentMenu.getId());
        }
        if (childrenMenu == null || childrenMenu.size() == 0) {
            return R.data(new ArrayList<MenuVO>());
        }
        childrenMenu = childrenMenu.stream().sorted(Comparator.comparing(Menu::getSort, (x, y) -> {
            if (x > y) {
                return 1;
            }
            if (x == y) {
                return 0;
            } else {
                return -1;
            }
        })).collect(Collectors.toList());
        return R.data(MenuWrapper.build().listNodeVO(childrenMenu));
    }

    /**
     * 获取APP门户的权限分配树形结构
     */
    @GetMapping("/grant-app-tree")
    @ApiOperationSupport(order = 20)
    @ApiOperation(value = "APP门户权限分配树形结构", notes = "APP门户权限分配树形结构")
    @ApiLog(value = "获取APP门户的权限分配树形结构")
    public R<GrantTreeVO> grantAppTree(BladeUser user) {
        GrantTreeVO vo = new GrantTreeVO();
        List<MenuVO> menuList = menuService.grantTreeProject(user);
        List<MenuVO> appMenu = null;
        if (menuList != null && menuList.size() > 0) {
            appMenu = new ArrayList<>();
            for (MenuVO menuVO : menuList) {
                if (menuVO.getRemark().equals("smartenv-mini-app")) {
                    String imgSource = menuVO.getSource();
                    if (StringUtils.isNotBlank(imgSource)) {
                        R<String> shareLink = ossClient.getObjectLink("smartenv", imgSource);
                        menuVO.setSource(shareLink.getData());
                        appMenu.add(menuVO);
                    }
                }
            }
//            menuList = menuList.stream()
//                    .filter(menuVO -> menuVO.getRemark().equals("smartenv-mini-app"))
//                    .collect(Collectors.toList());
        }
        vo.setMenu(appMenu);
        vo.setDataScope(menuService.grantDataScopeTree(user));
        vo.setApiScope(menuService.grantApiScopeTree(user));
        return R.data(vo);
    }


    @GetMapping("/biScreen")
    @ApiOperationSupport(order = 21)
    @ApiOperation(value = "查询可设置的大屏信息", notes = "查询可设置的大屏信息")
    public R<List<BiScreenInfo>> listMyBiScreen(BladeUser bladeUser) {
        return R.data(menuService.listMyBiScreen(bladeUser));
    }

    @PostMapping("/biScreen")
    @ApiOperationSupport(order = 22)
    @ApiOperation(value = "保存大屏设置信息", notes = "保存大屏设置信息")
    public R listMyBiScreen(@RequestBody BiScreenInfo biScreenInfo) {
        return R.data(menuService.saveBiScreen(biScreenInfo));
    }
}
