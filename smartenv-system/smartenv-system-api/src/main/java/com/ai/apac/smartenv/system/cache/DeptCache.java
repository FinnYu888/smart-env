package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/12 8:07 上午
 **/
public class DeptCache {

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    /**
     * 重新加载所有部门数据到内存中
     */
    public static void reload() {
        //先删除key
        SmartCache.clear(DEPT_FULL_NAME_MAP, DEPT_MAP);
        R<List<Dept>> deptListResult = getSysClient().getAllDept();
        reloadData(deptListResult);
    }

    /**
     * 重新加载指定部门数据到内存中
     */
    public static void reload(String tenantId) {
        R<List<Dept>> deptListResult = getSysClient().getTenantDept(tenantId);
        reloadData(deptListResult);
    }

    private static void reloadData(R<List<Dept>> deptListResult) {
        if (deptListResult.isSuccess()
                && deptListResult.getData() != null
                && deptListResult.getData().size() > 0) {
            List<Dept> deptList = deptListResult.getData();
            deptList.stream().forEach(dept -> {
                SmartCache.hset(DEPT_MAP, dept.getId(), dept);
                SmartCache.hset(DEPT_FULL_NAME_MAP, dept.getId(), dept.getFullName());
                SmartCache.hset(DEPT_NAME_MAP, dept.getId(), dept.getDeptName());
            });
        }
    }


    /**
     * 更新内存中数据
     *
     * @param dept
     */
    public static void saveOrUpdateDept(Dept dept) {
        if(dept == null){
            return;
        }
        SmartCache.hset(DEPT_MAP, dept.getId(), dept);
        SmartCache.hset(DEPT_FULL_NAME_MAP, dept.getId(), dept.getFullName());
        SmartCache.hset(DEPT_NAME_MAP, dept.getId(), dept.getDeptName());
    }

    /**
     * 从内存中删除某条记录
     *
     * @param deptId
     */
    public static void delDept(String deptId) {
        SmartCache.hdel(DEPT_MAP, deptId);
        SmartCache.hdel(DEPT_FULL_NAME_MAP, deptId);
        SmartCache.hdel(DEPT_NAME_MAP, deptId);
    }

    /**
     * 根据部门编码获取部门信息
     *
     * @param deptId
     * @return
     */
    public static Dept getDept(Long deptId) {
        if (deptId == null) {
            return null;
        }
        return SmartCache.hget(DEPT_MAP, deptId, () -> {
            R<Dept> result = getSysClient().getDept(deptId);
            if(result != null && result.getData() != null){
                return result.getData();
            }else{
                return null;
            }
        });
//        Object deptObj = getBladeRedisCache().hmGet(DEPT_MAP, deptId);
//        if (deptObj != null) {
//            return (Dept) ((ArrayList) deptObj).get(0);
//        }
//        return null;
    }

    /**
     * 根据部门编码获取部门名称
     *
     * @param deptId
     * @return
     */
    public static String getDeptName(String deptId) {
        if (StringUtil.isBlank(deptId)) {
            return null;
        }
        return SmartCache.hget(DEPT_NAME_MAP, deptId, () -> {
            R<Dept> result = getSysClient().getDept(Long.parseLong(deptId));
            return result.getData().getDeptName();
        });
//        Object deptNameObj = getBladeRedisCache().hmGet(DEPT_NAME_MAP, deptId);
//        if (deptNameObj != null) {
//            return (String) ((ArrayList) deptNameObj).get(0);
//        } else {
//            R<String> deptName = getSysClient().getDeptName(Long.parseLong(deptId));
//            if (deptName.isSuccess()) {
//                return deptName.getData();
//            }
//        }
//        return null;
    }

    /**
     * 根据部门编码集合获取部门名称集合
     *
     * @param deptIds
     * @return
     */
    public static String getDeptNames(String deptIds) {
        if (StringUtil.isBlank(deptIds)) {
            return null;
        }
        String[] deptIdArray = Func.toStrArray(deptIds);
        List<String> deptNameList = new ArrayList<String>();
        for (String deptId : deptIdArray) {
            String deptName = getDeptName(deptId);
            if (StringUtil.isNotBlank(deptName)) {
                deptNameList.add(deptName);
            }
        }
        return Func.join(deptNameList);
    }

    /**
     * 根据部门编码获取部门全名
     *
     * @param deptId
     * @return
     */
    public static String getDeptFullName(String deptId) {
        if (StringUtil.isBlank(deptId)) {
            return null;
        }
        return SmartCache.hget(DEPT_FULL_NAME_MAP, deptId, () -> {
            R<Dept> result = getSysClient().getDept(Long.parseLong(deptId));
            return result.getData().getDeptName();
        });
//        Object deptNameObj = getBladeRedisCache().hmGet(DEPT_FULL_NAME_MAP, deptId);
//        if (deptNameObj != null) {
//            return (String) ((ArrayList) deptNameObj).get(0);
//        } else {
//            R<Dept> dept = getSysClient().getDept(Long.parseLong(deptId));
//            if (dept.isSuccess()) {
//                return dept.getData().getFullName();
//            }
//        }
//        return null;
    }

    /**
     * 根据部门编码集合获取部门全名集合
     *
     * @param deptIds
     * @return
     */
    public static String getDeptFullNames(String deptIds) {
        if (StringUtil.isBlank(deptIds)) {
            return null;
        }
        String[] deptIdArray = Func.toStrArray(deptIds);
        List<String> fullNameList = new ArrayList<String>();
        for (String deptId : deptIdArray) {
            String deptFullName = getDeptFullName(deptId);
            if (StringUtil.isNotBlank(deptFullName)) {
                fullNameList.add(deptFullName);
            }
        }
        return Func.join(fullNameList);
    }
    public static Map<Long,Dept> getDeptName() {
        Map<Long,Dept> deptMap = new HashMap<>();
        try {
            List<Dept> deptList = getSysClient().getAllDept().getData();
            deptList.stream().forEach(dept -> {
                deptMap.put(dept.getId(),dept);
            });
        }catch (Exception e) {

        }
        return deptMap;
    }

}
