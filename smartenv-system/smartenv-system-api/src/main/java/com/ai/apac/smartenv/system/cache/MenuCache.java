package com.ai.apac.smartenv.system.cache;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.feign.ISysClient;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/22 3:40 下午
 **/
public class MenuCache {

    private static BladeRedisCache bladeRedisCache;

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    private static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    public static void reload() {
        //删除key
        getBladeRedisCache().del(MENU_MAP, MENU_CODE_MAP);
        R<List<Menu>> result = getSysClient().getAllMenu();
        if (result.isSuccess() && result.getData() != null) {
            List<Menu> menuList = result.getData();
            menuList.stream().forEach(menu -> {
                getBladeRedisCache().hSet(MENU_MAP, menu.getId(), menu);
                getBladeRedisCache().hSet(MENU_CODE_MAP, menu.getCode(), menu);
            });
        }
    }

    public static List<Menu> getAllWebMenu() {
        isReload();
        List<Menu> menuList = getBladeRedisCache().hVals(MENU_MAP);
        if (menuList != null && menuList.size() > 0) {
            return menuList.stream().filter(menu -> menu.getRemark() != null && menu.getRemark().equals("smartenv-web")).collect(Collectors.toList());
        }
        return null;
    }

    public static Menu getMenuByCode(String menuCode) {
        isReload();
        Menu menu = getBladeRedisCache().hGet(MENU_CODE_MAP, menuCode);
        return menu;
    }

    public static List<Menu> getWebChildrenMenu(Long parentId) {
        isReload();
        List<Menu> menuList = getBladeRedisCache().hVals(MENU_MAP);
        if (menuList != null && menuList.size() > 0) {
            return menuList.stream().filter(menu -> menu.getParentId().equals(parentId)
                    && menu.getRemark().equals("smartenv-web")).collect(Collectors.toList());
        }
        return null;
    }

    public static List<Menu> getAllAppMenu() {
        isReload();
        List<Menu> menuList = getBladeRedisCache().hVals(MENU_MAP);
        if (menuList != null && menuList.size() > 0) {
            return menuList.stream().filter(menu -> menu.getRemark() != null && menu.getRemark().equals("smartenv-mini-app")).collect(Collectors.toList());
        }
        return null;
    }

    public static Menu getMenuById(Long menuId) {
        isReload();
        if (menuId != null) {
            return getBladeRedisCache().hGet(MENU_MAP, menuId);
        }
        return null;
    }

    public static void isReload() {
        List<Menu> menuList = getBladeRedisCache().hVals(MENU_MAP);
        if (menuList == null || menuList.size() == 0) {
            reload();
        }
    }
}
