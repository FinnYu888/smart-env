package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.entity.RoleMenu;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.smartenv.cache.util.SmartCache;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.ROLE_MAP;
import static com.ai.apac.smartenv.common.cache.CacheNames.ROLE_NAME_MAP;
import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMINISTRATOR;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/11 9:59 下午
 **/
public class RoleCache {

    private static BladeRedis bladeRedis;

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }


    /**
     * 重新加载所有数据到内存中
     */
    public static void reload() {
        //先删除key
        SmartCache.clear(ROLE_NAME_MAP, ROLE_MAP);
        R<List<Role>> roleListResult = getSysClient().getAllRole();
        reloadData(roleListResult);
    }

    /**
     * 重新加载指定租户的数据到内存中
     */
    public static void reload(String tenantId) {
        R<List<Role>> roleListResult = getSysClient().getTenantRole(tenantId);
        reloadData(roleListResult);
    }

    private static void reloadData(R<List<Role>> roleListResult) {
        if (roleListResult.isSuccess()
                && roleListResult.getData() != null
                && roleListResult.getData().size() > 0) {
            List<Role> roleList = roleListResult.getData();
            roleList.stream().forEach(role -> {
                SmartCache.hset(ROLE_MAP, role.getId(), role);
                SmartCache.hset(ROLE_NAME_MAP, role.getId(), role.getRoleName());
            });
        }
    }

    /**
     * 更新内存中某条数据
     *
     * @param role
     */
    public static void saveOrUpdateRole(Role role) {
        if (role != null) {
            SmartCache.hset(ROLE_MAP, role.getId(), role);
            SmartCache.hset(ROLE_NAME_MAP, role.getId(), role.getRoleName());
        }
    }

    /**
     * 从内存中删除某条数据
     *
     * @param roleId
     */
    public static void delRole(String roleId) {
        SmartCache.hdel(ROLE_MAP, roleId);
        SmartCache.hdel(ROLE_NAME_MAP, roleId);
    }

    /**
     * 根据角色编码获取角色信息
     *
     * @param roleId
     * @return
     */
    public static Role getRole(String roleId) {
        return SmartCache.hget(ROLE_MAP, roleId, () -> {
            R<Role> role = getSysClient().getRole(Long.parseLong(roleId));
            return role.getData();
        });
//        Object roleObj = getBladeRedisCache().hmGet(ROLE_MAP, roleId);
//        if (roleObj != null) {
//            return (Role) ((ArrayList) roleObj).get(0);
//        } else {
//            R<Role> role = getSysClient().getRole(Long.parseLong(roleId));
//            if (role.isSuccess()) {
//                return role.getData();
//            }
//        }
//        return null;
    }

    /**
     * 根据角色编码获取角色名称
     *
     * @param roleId
     * @return
     */
    public static String getRoleName(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return null;
        }
        return SmartCache.hget(ROLE_NAME_MAP, roleId, () -> {
            R<Role> role = getSysClient().getRole(Long.parseLong(roleId));
            return role.getData() == null ? null : role.getData().getRoleName();
        });
//        Object roleObj = getBladeRedisCache().hmGet(ROLE_NAME_MAP, roleId);
//        if (roleObj != null) {
//            return (String) ((ArrayList) roleObj).get(0);
//        } else {
//            R<Role> role = getSysClient().getRole(Long.parseLong(roleId));
//            if (role.isSuccess()) {
//                return role.getData().getRoleName();
//            }
//        }
//        return null;
    }

    /**
     * 根据roleId集合获取角色名称
     *
     * @param roleIds
     * @return
     */
    public static String roleNames(String roleIds) {
        if (StringUtil.isBlank(roleIds)) {
            return null;
        }
        String[] roleIdArray = Func.toStrArray(roleIds);
        List<String> roleNameList = new ArrayList<String>();
        for (String roleId : roleIdArray) {
            String roleName = getRoleName(roleId);
            if (StringUtil.isNotBlank(roleName)) {
                roleNameList.add(roleName);
            }
        }
        return Func.join(roleNameList);
    }

    /**
     * 根据租户ID获取角色数据
     *
     * @param tenantId
     * @return
     */
    public static List<Role> getRoleByTenant(String tenantId) {
        Map<Long, Role> roleMap = getBladeRedis().hGetAll(ROLE_MAP);
        List<Role> roleList = roleMap.values().stream().filter(role -> role.getTenantId() != null && tenantId.equalsIgnoreCase(role.getTenantId()))
                .collect(Collectors.toList());
        return roleList;
    }

    public static List<RoleMenu> getMenuByRoleId(Long roleId) {
        R<List<RoleMenu>> result = getSysClient().getRoleMenuByRoleId(roleId);
        if (result != null && result.isSuccess() && result.getData() != null) {
            return result.getData();
        }
        return null;
    }

    ;
}
