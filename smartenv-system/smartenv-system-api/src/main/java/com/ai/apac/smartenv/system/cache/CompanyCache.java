package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.entity.City;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.feign.ICompanyClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 公司信息cache
 * @Date 2020/11/26 20:24 下午
 **/
public class CompanyCache {

    public static ICompanyClient companyClient = null;

    private static BladeRedis bladeRedis = null;

    private static ICompanyClient getCompanyClient() {
        if (companyClient == null) {
            companyClient = SpringUtil.getBean(ICompanyClient.class);
        }
        return companyClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    public static void reload() {
        getBladeRedis().del(COMPANY_MAP);
        R<List<Company>> allCompanyResult = getCompanyClient().getAllCompany();
        if (allCompanyResult.isSuccess() && allCompanyResult.getData() != null) {
            allCompanyResult.getData().stream().forEach(company -> {
                saveCompany(company);
            });
        }
    }

    /**
     * 获取公司信息
     *
     * @param companyId
     */
    public static Company getCompany(Long companyId) {
        return SmartCache.hget(COMPANY_MAP, companyId, () -> {
            return getCompanyClient().getCompanyById(companyId).getData();
        });
    }

    /**
     * 新增公司信息
     *
     * @param company
     */
    public static void saveCompany(Company company) {
        SmartCache.hset(COMPANY_MAP, company.getId(), company);
    }

    /**
     * 删除公司信息
     *
     * @param companyId
     */
    public static void delCompany(Long companyId) {
        SmartCache.hdel(COMPANY_MAP, companyId);
    }
}
