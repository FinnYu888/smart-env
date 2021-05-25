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
package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.MenuCache;
import com.ai.apac.smartenv.system.cache.SysCache;
import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.vo.MenuVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Chill
 */
public class MenuWrapper extends BaseEntityWrapper<Menu, MenuVO> {

    public static MenuWrapper build() {
        return new MenuWrapper();
    }

    @Override
    public MenuVO entityVO(Menu menu) {
        MenuVO menuVO = Objects.requireNonNull(BeanUtil.copy(menu, MenuVO.class));
        if (Func.equals(menu.getParentId(), BladeConstant.TOP_PARENT_ID)) {
            menuVO.setParentName(BladeConstant.TOP_PARENT_NAME);
        } else {
            Menu parent = MenuCache.getMenuById(menu.getParentId());
            menuVO.setParentName(parent.getName());
        }
        String category = DictCache.getValue("menu_category", Func.toInt(menuVO.getCategory()));
        String action = DictCache.getValue("button_func", Func.toInt(menuVO.getAction()));
        String open = DictCache.getValue("yes_no", Func.toInt(menuVO.getIsOpen()));
        menuVO.setCategoryName(category);
        menuVO.setActionName(action);
        menuVO.setIsOpenName(open);
        return menuVO;
    }

    public List<MenuVO> listNodeVO(List<Menu> list) {
        if (list == null) {
			return new ArrayList<MenuVO>();
        }
        List<MenuVO> collect = list.stream().map(menu -> BeanUtil.copy(menu, MenuVO.class)).collect(Collectors.toList());
        return ForestNodeMerger.merge(collect);
    }

    public List<MenuVO> listNodeLazyVO(List<MenuVO> list) {
        List<MenuVO> collect = list.stream().map(menu -> BeanUtil.copy(menu, MenuVO.class)).collect(Collectors.toList());
        return ForestNodeMerger.merge(collect);
    }

}
