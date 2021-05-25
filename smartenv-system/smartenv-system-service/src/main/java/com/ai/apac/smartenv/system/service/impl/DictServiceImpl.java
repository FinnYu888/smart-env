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
import com.ai.apac.smartenv.system.dto.DictDTO;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.mapper.DictMapper;
import com.ai.apac.smartenv.system.service.IDictService;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.system.wrapper.DictWrapper;
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
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;
import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;


/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

	@Override
	public IPage<DictVO> selectDictPage(IPage<DictVO> page, DictVO dict) {
		return page.setRecords(baseMapper.selectDictPage(page, dict));
	}

	@Override
	public List<DictVO> tree() {
		return ForestNodeMerger.merge(baseMapper.tree());
	}

	@Override
	public List<DictVO> parentTree() {
		return ForestNodeMerger.merge(baseMapper.parentTree());
	}

	@Override
	public String getValue(String code, String dictKey) {
		return Func.toStr(baseMapper.getValue(code, dictKey), StringPool.EMPTY);
	}

	@Override
	public List<Dict> getList(String code) {
		return baseMapper.getList(code);
	}

	@Override
	public boolean submit(Dict dict) {
		LambdaQueryWrapper<Dict> lqw = Wrappers.<Dict>query().lambda().eq(Dict::getCode, dict.getCode()).eq(Dict::getDictKey, dict.getDictKey());
		Integer cnt = baseMapper.selectCount((Func.isEmpty(dict.getId())) ? lqw : lqw.notIn(Dict::getId, dict.getId()));
		if (cnt > 0) {
			throw new ServiceException("当前字典键值已存在!");
		}
		if (Func.isEmpty(dict.getParentId())) {
			dict.setParentId(BladeConstant.TOP_PARENT_ID);
		}

		dict.setIsDeleted(BladeConstant.DB_NOT_DELETED);
		CacheUtil.clear(DICT_CACHE);
		return saveOrUpdate(dict);
	}

	@Override
	public boolean removeDict(String ids) {
		Integer cnt = baseMapper.selectCount(Wrappers.<Dict>query().lambda().in(Dict::getParentId, Func.toLongList(ids)));
		if (cnt > 0) {
			throw new ServiceException("请先删除子节点!");
		}
		return removeByIds(Func.toLongList(ids));
	}

	@Override
	public IPage<DictVO> parentList(Map<String, Object> dict, Query query) {
		IPage<Dict> page = this.page(Condition.getPage(query), Condition.getQueryWrapper(dict, Dict.class).lambda().eq(Dict::getParentId, CommonConstant.TOP_PARENT_ID).orderByAsc(Dict::getSort));
		return DictWrapper.build().pageVO(page);
	}

	@Override
	public IPage<DictVO> childList(Map<String, Object> dict, Long parentId, Query query) {
		dict.remove("parentId");
		IPage<Dict> page = this.page(Condition.getPage(query), Condition.getQueryWrapper(dict, Dict.class).lambda().eq(Dict::getParentId, parentId).orderByAsc(Dict::getSort));
		return DictWrapper.build().pageVO(page);
	}

	@Override
	public boolean managementModifyDict(DictDTO dictDTO){

		List<DictDTO> dictDTOList = dictDTO.getDictDTOList();
		List<Dict> copy = BeanUtil.copy(dictDTOList, Dict.class);
		copy.forEach(dict1 -> {
			dict1.setParentId(dictDTO.getId());
			dict1.setCode(dictDTO.getCode());
		});
		if (CollectionUtil.isEmpty(dictDTOList)){
			boolean b1 = updateById(BeanUtil.copy(dictDTO, Dict.class));
			return b1;
		}else {
			boolean b = saveOrUpdateBatch(copy);
			return b;
		}
	}

}
