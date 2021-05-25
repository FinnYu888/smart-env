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
package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.system.entity.DictBiz;
import com.ai.apac.smartenv.system.mapper.DictBizMapper;
import com.ai.apac.smartenv.system.service.IDictBizService;
import com.ai.apac.smartenv.system.vo.DictBizVO;
import com.ai.apac.smartenv.system.wrapper.DictBizWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
public class DictBizServiceImpl extends ServiceImpl<DictBizMapper, DictBiz> implements IDictBizService {

	@Override
	public List<DictBizVO> tree() {
		return ForestNodeMerger.merge(baseMapper.tree());
	}

	@Override
	public List<DictBizVO> parentTree() {
		return ForestNodeMerger.merge(baseMapper.parentTree());
	}

	@Override
	public String getValue(String code, String dictKey) {
		return Func.toStr(baseMapper.getValue(code, dictKey), StringPool.EMPTY);
	}

	@Override
	public List<DictBiz> getList(String code) {
		return baseMapper.getList(code);
	}

	@Override
	public boolean submit(DictBiz dict) {
		CacheUtil.clear(DICT_CACHE);
		LambdaQueryWrapper<DictBiz> lqw = Wrappers.<DictBiz>query().lambda().eq(DictBiz::getCode, dict.getCode()).eq(DictBiz::getDictKey, dict.getDictKey()).eq(DictBiz::getTenantId, dict.getTenantId());
		Integer cnt = baseMapper.selectCount((Func.isEmpty(dict.getId())) ? lqw : lqw.notIn(DictBiz::getId, dict.getId()));
		if (cnt > 0) {
			throw new ServiceException("当前字典键值已存在!");
		}
		if (Func.isEmpty(dict.getParentId())) {
			dict.setParentId(BladeConstant.TOP_PARENT_ID);
		}
		dict.setIsDeleted(BladeConstant.DB_NOT_DELETED);
		return saveOrUpdate(dict);
	}

	@Override
	public boolean removeDict(String ids) {
		CacheUtil.clear(DICT_CACHE);
		Integer cnt = baseMapper.selectCount(Wrappers.<DictBiz>query().lambda().in(DictBiz::getParentId, Func.toLongList(ids)));
		if (cnt > 0) {
			throw new ServiceException("请先删除子节点!");
		}
		return removeByIds(Func.toLongList(ids));
	}

	@Override
	public IPage<DictBizVO> parentList(Map<String, Object> dict, Query query) {
		IPage<DictBiz> page = this.page(Condition.getPage(query), Condition.getQueryWrapper(dict, DictBiz.class).lambda().eq(DictBiz::getParentId, CommonConstant.TOP_PARENT_ID).orderByAsc(DictBiz::getSort));
		return DictBizWrapper.build().pageVO(page);
	}

	@Override
	public IPage<DictBizVO> childList(Map<String, Object> dict, Long parentId, Query query) {
		dict.remove("parentId");
		IPage<DictBiz> page = this.page(Condition.getPage(query), Condition.getQueryWrapper(dict, DictBiz.class).lambda().eq(DictBiz::getParentId, parentId).orderByAsc(DictBiz::getSort));
		return DictBizWrapper.build().pageVO(page);
	}
}
