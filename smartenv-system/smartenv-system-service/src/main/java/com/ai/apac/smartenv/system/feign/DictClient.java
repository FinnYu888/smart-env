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
package com.ai.apac.smartenv.system.feign;


import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.service.IDictService;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 字典服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class DictClient implements IDictClient {

	private IDictService service;

	@Override
	@GetMapping(GET_BY_ID)
	public R<Dict> getById(Long id) {
		return R.data(service.getById(id));
	}

	@Override
	@GetMapping(GET_VALUE)
	public R<String> getValue(String code, String dictKey) {
		return R.data(service.getValue(code, dictKey));
	}

	@Override
	@GetMapping(GET_LIST)
	public R<List<Dict>> getList(String code) {
		return R.data(service.getList(code));
	}

	/**
	 * 获取字典表中所有数据
	 *
	 * @return
	 */
	@Override
	@GetMapping(GET_ALL_DICT)
	public R<List<Dict>> getAllDict() {
		return R.data(service.list());
	}
	@Override
	@GetMapping(GET_MAP_DICT)
	public R<Map<String,Object>>  getMap(String code) {
		Map<String,Object> map = new HashMap<>();
		List<Dict> dictList = service.getList(code);
		dictList.forEach(dict -> {
			map.put(dict.getDictKey(),dict.getDictValue());
		});
		return R.data(map);
	}
}
