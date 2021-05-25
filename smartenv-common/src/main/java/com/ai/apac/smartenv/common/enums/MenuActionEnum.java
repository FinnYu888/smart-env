package com.ai.apac.smartenv.common.enums;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import lombok.Getter;

/**
 * Created by qianlong on 2020/2/24.
 */
public enum MenuActionEnum  {

    MENU("menu",0),
    ADD("add",1),
    EDIT("edit",2),
    DELETE("delete",3),
    VIEW("view",4),
    SETTING("setting",5),
    STATE("state",6),
    DETAIL("detail",7),
    EXPORT("export",8),
    IMPORT("import",9),
    BATCH_PROCESS("batch_process",10),
    FRAME("frame",11),
    LINK("link",12),
    PATH("path",13),
    ;

    @Getter
    private String menuAlias;

    @Getter
    private Integer action;

    private MenuActionEnum(String menuAlias, Integer action) {
        this.menuAlias = menuAlias;
        this.action = action;
    }

    public static Integer getDescByValue(String menuAlias) {
        for (MenuActionEnum objEnum : MenuActionEnum.values()) {
            if (objEnum.getMenuAlias().equals(menuAlias)) {
                return objEnum.getAction();
            }
        }
        return 0;
    }

}
