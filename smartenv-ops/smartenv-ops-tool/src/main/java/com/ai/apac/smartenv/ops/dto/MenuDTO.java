package com.ai.apac.smartenv.ops.dto;

import lombok.Data;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/20 8:51 下午
 **/
@Data
public class MenuDTO {

    private String menuId;

    private String pMenuId;

    private String menuCode;

    private String menuName;

    private String alias;

    private String path;

    private Integer sort;

    private Integer category;

    private String action;
}
