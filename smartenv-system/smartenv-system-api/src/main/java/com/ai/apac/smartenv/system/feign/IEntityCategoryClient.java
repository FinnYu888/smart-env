package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.*;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * EntityCategory Feign接口类
 *
 * @author ZHANGLEI25
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = IEntityCategoryClientFallback.class
)
public interface IEntityCategoryClient {


    String API_PREFIX = "/client";
    String CATEGORY = API_PREFIX + "/category";
    String GET_ALL_CATEGORY = API_PREFIX + "/get-all-category";
    String CATEGORY_NAME = API_PREFIX + "/category-name";
    String CATEGORY_CODE = API_PREFIX + "/category-code";
    String CATEGORY_LIST_BY_TYPE = API_PREFIX + "/category-list-by-type";
    String CATEGORY_LIST_BY_PARENT_CATEGORY_ID = API_PREFIX + "/category-list-by-parent-category-id";

    String SUB_CATEGORYID_BY_PARENT_CATEGORY_ID = API_PREFIX + "/subCategoryId-by-parent-category-id";


    /**
     * 根据类别ID获取类别
     *
     * @param id 主键
     * @return Menu
     */
    @GetMapping(CATEGORY)
    R<EntityCategory> getCategory(@RequestParam("id")  Long id);

    /**
     * 获取所有类别
     *
     * @return Menu
     */
    @GetMapping(GET_ALL_CATEGORY)
    R<List<EntityCategory>> getAllCategory();
    
    /**
     * 根据类别ID获取类别名称
     *
     * @param id 主键
     * @return Menu
     */
    @GetMapping(CATEGORY_NAME)
    R<String> getCategoryName(@RequestParam("id")  Long id);

    /**
     * 根据类别ID获取类别CODE
     *
     * @param id 主键
     * @return 
     */
    @GetMapping(CATEGORY_CODE)
    R<String> getCategoryCode(@RequestParam("id")  Long id);

    @GetMapping(CATEGORY_LIST_BY_TYPE)
    R<List<EntityCategory>> getCategoryByType(@RequestParam("code") String code);

    @GetMapping(CATEGORY_LIST_BY_PARENT_CATEGORY_ID)
	R<List<EntityCategory>> getCategoryByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId);

    @GetMapping(SUB_CATEGORYID_BY_PARENT_CATEGORY_ID)
    R<List<Long>> getSubCategoryIdByParentCategoryId(@RequestParam("parentCategoryId") Long parentCategoryId);
}