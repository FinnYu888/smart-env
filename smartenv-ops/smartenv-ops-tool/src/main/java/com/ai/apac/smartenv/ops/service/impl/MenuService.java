package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.ai.apac.smartenv.common.enums.MenuActionEnum;

import java.util.List;
import java.util.Map;

/**
 * @author qianlong
 * @description 读取excel并输出对应的sql
 * @Date 2020/2/20 8:28 下午
 **/
public class MenuService {

    public static String menuSql = "insert into blade_menu(id,parent_id,code,name,alias,path,sort,category,action,is_open,remark,is_deleted) " +
            "values (#id,#parent_id,'#code','#name','#alias','#path',#sort,#category,#action,1,'smartenv-web',0);";

    public static void generateMenuSql(String filePath) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(filePath);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.stream().forEach(obj -> {
//            System.out.println(JSONUtil.toJsonStr(obj));
            Object categoryObj = obj.get("菜单类型");
            if (categoryObj == null) {
                return;
            }
            String category = obj.get("菜单类型").toString();
            if (category != null && (category.equalsIgnoreCase("1")
                    || category.equalsIgnoreCase("2") || category.equalsIgnoreCase("3"))) {
                String menuName = obj.get("菜单名称").toString();
                String menuId = obj.get("菜单ID").toString();
                String pMenuId = obj.get("父菜单ID").toString();
                String menuCode = obj.get("菜单code").toString();
                String alias = obj.get("菜单别名") == null ? "" : obj.get("菜单别名").toString();
                String path = obj.get("请求路由") == null ? "" : obj.get("请求路由").toString();
                String sort = obj.get("排序") == null ? "" : obj.get("排序").toString();
                Integer action = MenuActionEnum.getDescByValue(alias);
                String sql = menuSql.replaceAll("#id", menuId)
                        .replaceAll("#parent_id", pMenuId)
                        .replaceAll("#code", menuCode)
                        .replaceAll("#name", menuName)
                        .replaceAll("#alias", alias)
                        .replaceAll("#path", path)
                        .replaceAll("#sort", sort)
                        .replaceAll("#category", category)
                        .replaceAll("#action", String.valueOf(action));
                System.out.println(sql);
            }
        });
    }

//    public static void main(String[] args) throws Exception {
//        generateMenuSql("/SmartEnv/doc/16产品设计/01需求收集/菜单整理.xlsx");
//    }
}
