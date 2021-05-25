package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.service.IEntityCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * EntityCategory 服务Feign实现类
 *
 * @author ZHANGLEI25
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class EntityCategoryClient implements IEntityCategoryClient{


    private IEntityCategoryService entityCategoryService;

    @Override
    @GetMapping(CATEGORY)
    public R<EntityCategory> getCategory(Long id) {
        return R.data(entityCategoryService.getById(id));
    }
    
    @Override
    @GetMapping(CATEGORY_NAME)
    public R<String> getCategoryName(Long id) {
    	EntityCategory entityCategory = entityCategoryService.getById(id);
    	String categoryName = "";
    	if (entityCategory != null) {
    		categoryName = entityCategory.getCategoryName();
		}
        return R.data(categoryName);
    }

    @Override
    @GetMapping(CATEGORY_CODE)
    public R<String> getCategoryCode(Long id) {
    	EntityCategory entityCategory = entityCategoryService.getById(id);
    	String categoryCode = "";
    	if (entityCategory != null) {
    		categoryCode = entityCategory.getCategoryCode();
		}
        return R.data(categoryCode);
    }


    @Override
    @GetMapping(CATEGORY_LIST_BY_TYPE)
    public R<List<EntityCategory>> getCategoryByType(String type ) {
        QueryWrapper<EntityCategory> queryWrapper = new QueryWrapper<EntityCategory>();

        queryWrapper.lambda().eq(EntityCategory::getEntityType,type);
        queryWrapper.lambda().orderByDesc(EntityCategory::getParentCategoryId);
        List<EntityCategory> list = entityCategoryService.list(queryWrapper);
        return R.data(list);
    }

    @Override
    @GetMapping(CATEGORY_LIST_BY_PARENT_CATEGORY_ID)
    public R<List<EntityCategory>> getCategoryByParentCategoryId(Long parentCategoryId) {
		EntityCategory wrapper = new EntityCategory();
		wrapper.setParentCategoryId(parentCategoryId);
		List<EntityCategory> list = entityCategoryService.list(Condition.getQueryWrapper(wrapper));
		return R.data(list);
    }

    @Override
    public R<List<Long>> getSubCategoryIdByParentCategoryId(Long parentCategoryId) {
        return R.data(entityCategoryService.getAllChildIdByParentId(parentCategoryId));
    }

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @Override
    public R<List<EntityCategory>> getAllCategory() {
        return R.data(entityCategoryService.list());
    }
}
