package com.ai.apac.smartenv.person.cache;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusImgEnum;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.smartenv.cache.util.SmartCache;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 人员缓存
 * @Date 2020/2/25 9:22 上午
 **/
public class PersonCache {

    private static IPersonClient personClient = null;

    private static BladeRedis bladeRedis = null;

    private static IOssClient ossClient = null;

    private static RedisConnectionFactory redisConnectionFactory;

    private static RedisTemplate redisTemplate;

    public static IPersonClient getPersonClient() {
        if (personClient == null) {
            personClient = SpringUtil.getBean(IPersonClient.class);
        }
        return personClient;
    }

    public static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    public static IOssClient getOssClient() {
        if (ossClient == null) {
            ossClient = SpringUtil.getBean(IOssClient.class);
        }
        return ossClient;
    }

    public static RedisConnectionFactory getRedisConnectionFactory() {
        if (redisConnectionFactory == null) {
            redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);
        }
        return redisConnectionFactory;
    }

    public static RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = SpringUtil.getBean("redisTemplate");
        }
        return redisTemplate;
    }


    /**
     * 加载所有用户数据到缓存
     */
    public static void reload() {
        //先获取租户数据
        List<Tenant> allTenant = TenantCache.getAllTenant();
        allTenant.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    /**
     * 加载指定租户的用户数据到缓存
     *
     * @param tenantId
     */
    public static void reload(String tenantId) {
        R<List<Person>> result = getPersonClient().getPersonByTenant(tenantId);
        delActivePersonCount(tenantId);
        if (result != null && result.getData() != null) {
            List<Person> personList = result.getData();
            String cacheName = PERSON_MAP + StringPool.COLON + tenantId;
            personList.stream().forEach(person -> {
                SmartCache.hset(cacheName, person.getId(), person);
                SmartCache.hset(PERSON_TENANT_MAP, person.getId(), person.getTenantId());
            });
        }
    }

    /**
     * 根据人员ID获取人员信息
     *
     * @param tenantId
     * @param personId
     * @return
     */
    public static Person getPersonById(String tenantId, Long personId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        Person person = null;
        if (StringUtil.isBlank(tenantId)) {
            person = getPersonClient().getPerson(personId).getData();
        } else {
            String cacheName = PERSON_MAP + StringPool.COLON + tenantId;
            person = SmartCache.hget(cacheName, String.valueOf(personId));
            if (person == null) {
                person = getPersonClient().getPerson(personId).getData();
                saveOrUpdatePerson(person);
            }
        }
        return person;
    }

    /**
     * 更新内存中数据
     *
     * @param person
     */
    public static void saveOrUpdatePerson(Person person) {
        if (person == null || person.getId() == null || StringUtil.isBlank(person.getJobNumber())) {
            return;
        }
        if (StringUtil.isBlank(person.getTenantId())) {
            person.setTenantId(AuthUtil.getTenantId());
        }
        String tenantId = person.getTenantId();
        String cacheName = PERSON_MAP + StringPool.COLON + tenantId;
        SmartCache.hset(cacheName, person.getId(), person);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param personId
     */
    public static void delPerson(String tenantId, Long personId) {
        if (personId == null) {
            return;
        }
        if (StringUtil.isBlank(tenantId)) {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                tenantId = user.getTenantId();
            } else {
                tenantId = TenantConstant.DEFAULT_TENANT_ID;
            }
        }
        String cacheName = PERSON_MAP + StringPool.COLON + tenantId;
        SmartCache.hdel(cacheName, personId);
        delActivePersonCount(tenantId);
    }

    /**
     * 用于人员监控时根据用户状态获取对应的图标地址
     *
     * @param status
     * @return
     */
    public static String getPersonStatusImg(Integer status) {
        String cacheName = PERSON_STATUS_IMG + StringPool.COLON + status;
        String imgLink = getBladeRedis().get(cacheName);
        if (StringUtil.isBlank(imgLink)) {
            String imgName = PersonStatusImgEnum.getDescByValue(status);
            R<String> shareLink = getOssClient().getObjectLink(PersonConstant.BUCKET, imgName);
            if (shareLink != null && shareLink.getData() != null) {
                getBladeRedis().setEx(cacheName, shareLink.getData(), CacheNames.ExpirationTime.EXPIRATION_TIME_24HOURS);
                return shareLink.getData();
            }
            return null;
        } else {
            return imgLink;
        }
    }

    /**
     * 不要列表查询，可用IPersonService中方法
     * 获取在职人员
     *
     * @param tenantId
     * @return
     */
    public static List<Person> getActivePerson(String tenantId) {
        String cacheName = PERSON_MAP + StringPool.COLON + tenantId;
        List<Person> personList = getBladeRedis().hVals(cacheName);
        return personList.stream()
                .filter(person -> person != null
                        && person.getIsIncumbency() != null
                        && person.getIsIncumbency().equals(PersonConstant.IncumbencyStatus.IN))
                .collect(Collectors.toList());
    }

    /**
     * 获取在职人员数量
     *
     * @param tenantId
     * @return
     */
    public static Integer getActivePersonCount(String tenantId) {
        Integer count = SmartCache.hget(PERSON_COUNT_MAP, tenantId, () -> {
            Integer personCount = getPersonClient().getPersonCountByTenant(tenantId).getData();
            return personCount;
        });
        return count;
    }

    /**
     * 删除缓存中员工统计数量
     *
     * @param tenantId
     */
    public static void delActivePersonCount(String tenantId) {
        SmartCache.hdel(PERSON_COUNT_MAP, tenantId);
    }

    /**
     * 获取人员手表状态统计
     *
     * @param tenantId
     * @return
     */
    public static PersonDeviceStatusCountDTO getPersonDeviceStatusCount(String tenantId) {
        PersonDeviceStatusCountDTO result = SmartCache.hget(PERSON_WATCH_STATUS_MAP, tenantId, () -> {
            PersonDeviceStatusCountDTO personDeviceStatusCountDTO = getPersonClient().getPersonDeviceStatusStat(tenantId).getData();
            return personDeviceStatusCountDTO;
        });
        return result;
    }

    /**
     * 删除人员手表状态统计
     *
     * @param tenantId
     * @return
     */
    public static void delPersonDeviceStatusCount(String tenantId) {
        SmartCache.hdel(PERSON_WATCH_STATUS_MAP, tenantId);
    }

	/*private static String getDefaultTenantId() {
		String tenantId = TenantConstant.DEFAULT_TENANT_ID;
		BladeUser user = AuthUtil.getUser();
		if (user != null) {
			tenantId = user.getTenantId();
		}
		return tenantId;
	}*/

    /**
     * 根据员工姓名查询员工帐号信息
     *
     * @param personName
     * @return
     */
    public static List<PersonAccountVO> getPersonAccount(String personName) {
        if (StringUtils.isBlank(personName)) {
            return null;
        }
        //先从缓存中获取
        List<PersonAccountVO> list = getBladeRedis().get(PERSON_ACCOUNT_MAP + StringPool.COLON + personName);
        if (CollUtil.isEmpty(list)) {
            R<List<PersonAccountVO>> result = getPersonClient().getPersonAccount(personName);
            if (result.isSuccess() && result.getData() != null && result.getData().size() > 0) {
                list = result.getData();
                //将查询结果缓存15秒后过期s
                getBladeRedis().setEx(PERSON_ACCOUNT_MAP + StringPool.COLON + personName, list, 15L);
            }
        }
        return list;
    }

}
