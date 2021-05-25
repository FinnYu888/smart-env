package com.ai.apac.smartenv.person.cache;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.smartenv.cache.util.SmartCache;

import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

public class PersonUserRelCache {

    private static String REL_KEY = "relId";
    private static String PERSON_KEY = "personId";
    private static String USER_KEY = "userId";
    private static IPersonUserRelClient personUserRelClient = null;

    public static IPersonUserRelClient getPersonUserRelClient() {
        if (personUserRelClient == null) {
            personUserRelClient = SpringUtil.getBean(IPersonUserRelClient.class);
        }
        return personUserRelClient;
    }

    public static PersonUserRel getRelByUserId(Long userId) {
        String cacheName = PERSON_USER_REL_MAP + StringPool.COLON + USER_KEY;
        PersonUserRel rel = SmartCache.hget(cacheName, String.valueOf(userId));
        if (rel == null || rel.getId() == null) {
            rel = getPersonUserRelClient().getRelByUserId(userId).getData();
            saveOrUpdateRel(rel);
        }
        return rel;
    }

    public static PersonUserRel getRelByPersonId(Long personId) {
        String cacheName = PERSON_USER_REL_MAP + StringPool.COLON + PERSON_KEY;
        PersonUserRel rel = SmartCache.hget(cacheName, String.valueOf(personId));
        if (rel == null || rel.getId() == null) {
            rel = getPersonUserRelClient().getRelByPersonId(personId).getData();
            saveOrUpdateRel(rel);
        }
        return rel;
    }

    /**
     * 根据UserId获取绑定的员工姓名
     *
     * @param userId
     * @return
     */
    public static String getPersonNameByUser(Long userId) {
        PersonUserRel personUserRel = getRelByUserId(userId);
        if (personUserRel != null && personUserRel.getId() != null) {
            Person person = PersonCache.getPersonById(personUserRel.getTenantId(), personUserRel.getPersonId());
            if (person != null) {
                return person.getPersonName();
            }
            return null;
        }
        return null;
    }

    public static PersonUserRel getRelByRelId(Long relId) {
        String cacheName = PERSON_USER_REL_MAP + StringPool.COLON + REL_KEY;
        PersonUserRel rel = SmartCache.hget(cacheName, String.valueOf(relId));
        if (rel == null || rel.getId() == null) {
            rel = getPersonUserRelClient().getRelById(relId).getData();
            saveOrUpdateRel(rel);
        }
        return rel;
    }

    /**
     * 更新内存中数据
     *
     * @param personUserRel
     */
    public static void saveOrUpdateRel(PersonUserRel personUserRel) {
        if (personUserRel == null || personUserRel.getId() == null || personUserRel.getPersonId() == null
                || personUserRel.getUserId() == null) {
            return;
        }
        String cacheName = PERSON_USER_REL_MAP + StringPool.COLON + REL_KEY;
        SmartCache.hset(cacheName, personUserRel.getId(), personUserRel);
        cacheName = PERSON_USER_REL_MAP + StringPool.COLON + PERSON_KEY;
        SmartCache.hset(cacheName, personUserRel.getPersonId(), personUserRel);
        cacheName = PERSON_USER_REL_MAP + StringPool.COLON + USER_KEY;
        SmartCache.hset(cacheName, personUserRel.getUserId(), personUserRel);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param personUserRel
     */
    public static void delRel(PersonUserRel personUserRel) {
        if (personUserRel == null || personUserRel.getId() == null || personUserRel.getPersonId() == null
                || personUserRel.getUserId() == null) {
            return;
        }
        String cacheName = PERSON_USER_REL_MAP + StringPool.COLON + REL_KEY;
        SmartCache.hdel(cacheName, personUserRel.getId());
        cacheName = PERSON_USER_REL_MAP + StringPool.COLON + PERSON_KEY;
        SmartCache.hdel(cacheName, personUserRel.getPersonId());
        cacheName = PERSON_USER_REL_MAP + StringPool.COLON + USER_KEY;
        SmartCache.hdel(cacheName, personUserRel.getUserId());
    }
}
