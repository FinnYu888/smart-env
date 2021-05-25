package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: IToiletClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/18     zhaidx           v1.0.0               修改原因
 */
public class IToiletClientFallback implements IToiletClient{
    @Override
    public R<ToiletInfoVO> getToilet(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Integer> countAllToilet(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<ToiletInfoVO>> listToiletVOByCondition(ToiletQueryDTO queryDTO) {
        return R.fail("获取数据失败");
    }
    
    @Override
	public R<List<ToiletInfoVO>> listToiletAll() {
		return R.fail("获取数据失败");
	}
}
