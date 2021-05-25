package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.*;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IEntityCategoryClientFallback implements IEntityCategoryClient {

    @Override
    public R<EntityCategory> getCategory(Long id) {
        return R.fail("获取数据失败");
    }
    
    @Override
    public R<String> getCategoryName(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getCategoryCode(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<EntityCategory>> getCategoryByType(String code) {
         return R.fail("获取数据失败");
    }

	@Override
	public R<List<EntityCategory>> getCategoryByParentCategoryId(Long parentCategoryId) {
		return R.fail("获取数据失败");
	}

    @Override
    public R<List<Long>> getSubCategoryIdByParentCategoryId(Long parentCategoryId) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @Override
    public R<List<EntityCategory>> getAllCategory() {
        return R.fail("获取数据失败");
    }
}
