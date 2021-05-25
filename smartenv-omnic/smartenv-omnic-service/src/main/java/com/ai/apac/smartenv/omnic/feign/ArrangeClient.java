package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.service.IArrangeService;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: ArrangeClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月22日 下午5:10:31 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月22日     zhaoaj           v1.0.0               修改原因
 */
//@ApiIgnore
@RestController
@RequiredArgsConstructor
public class ArrangeClient implements IArrangeClient{

	@Autowired
    private IArrangeService arrangeService;
	
	@Override
	@GetMapping(LIST_ARRANGE)
	public R<List<QScheduleObject>> listArrange(QScheduleObjectVO qScheduleObject) {
		return R.data(arrangeService.listArrange(qScheduleObject, qScheduleObject.getStart(), qScheduleObject.getSize(),
				qScheduleObject.isHistoryFlag()));
	}

	@Override
	@GetMapping(COUNT_ARRANGE)
	public R<Integer> countArrange(QScheduleObjectVO qScheduleObject) {
		return R.data(arrangeService.countArrange(qScheduleObject, qScheduleObject.isHistoryFlag()));
	}














}
