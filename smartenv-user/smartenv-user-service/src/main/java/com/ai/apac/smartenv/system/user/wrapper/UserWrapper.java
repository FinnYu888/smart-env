/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.system.user.wrapper;

import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.Objects;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Chill
 */
public class UserWrapper extends BaseEntityWrapper<User, UserVO> {

    public static UserWrapper build() {
        return new UserWrapper();
    }

    private static IOssClient ossClient = null;

    private IOssClient getOssClient(){
        if(ossClient == null){
            ossClient = SpringUtil.getBean(IOssClient.class);
        }
        return ossClient;
    }

    @Override
    public UserVO entityVO(User user) {
        UserVO userVO = Objects.requireNonNull(BeanUtil.copy(user, UserVO.class));
        userVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
        userVO.setDeptName(DeptCache.getDeptNames(user.getDeptId()));
        userVO.setDeptFullName(DeptCache.getDeptFullName(user.getDeptId()));
        String sex = DictCache.getValue("sex", Func.toInt(user.getSex()));
        userVO.setSexName(sex);
        String statusName = DictCache.getValue("user_status", Func.toInt(user.getStatus()));
        userVO.setStatusName(statusName);
        if(StringUtils.isNotBlank(user.getAvatar())){
            R<String> result = getOssClient().getObjectLink("smartenv",user.getAvatar());
            if(result != null && result.getData() != null){
                userVO.setAvatarLink(result.getData());
            }
        }
        String realName = PersonUserRelCache.getPersonNameByUser(user.getId());
        userVO.setRealName(realName);
        user.setRealName(realName);
        if(StringUtils.isBlank(user.getName())){
            userVO.setNickName(CommonUtil.getNickName(user.getRealName()));
        }else if(user.getName().length() >= 3){
            userVO.setNickName(CommonUtil.getNickName(user.getRealName()));
        }else{
            userVO.setNickName(user.getRealName());
        }
        if (StringUtils.isNotBlank(user.getRoleId())) {
            String[] roleIdList = Func.toStrArray(user.getRoleId());
            userVO.setRoleIdList(roleIdList);
        }
        return userVO;
    }

}
