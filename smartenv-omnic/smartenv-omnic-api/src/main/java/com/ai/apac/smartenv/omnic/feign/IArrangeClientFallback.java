package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;

import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: IArrangeClientFallback.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月22日 下午5:08:59 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月22日     zhaoaj           v1.0.0               修改原因
 */
public class IArrangeClientFallback implements IArrangeClient{

	@Override
	public R<List<QScheduleObject>> listArrange(QScheduleObjectVO qScheduleObject) {
		return R.fail("接收数据失败");
	}

	@Override
	public R<Integer> countArrange(QScheduleObjectVO qScheduleObject) {
		return R.fail("接收数据失败");
	}
}
