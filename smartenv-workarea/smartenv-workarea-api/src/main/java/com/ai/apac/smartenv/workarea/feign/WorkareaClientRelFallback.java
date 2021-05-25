package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IWorkareaClientRelFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  19:07    panfeng          v1.0.0             修改原因
 */
public class WorkareaClientRelFallback implements IWorkareaRelClient {

    @Override
    public R<List<WorkareaRel>> getByEntityIdAndType(Long entityId, Long entityType) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaRel>> getByCondition(String workAreaId, Long entityType, List<Long> entityIds) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaRel>> queryByCondition(Long entityId, Long entityType, Long startTime, Long endTime,String tenantId){
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaRel>> getByIdAndType(String workAreaId, Long entityType){
        return R.fail("接收数据失败");
    }


    @Override
	public R<Boolean> unbindWorkarea(Long entityId, Long entityType) {
		return R.fail("接收数据失败");
	}

    @Override
	public R<Boolean> syncDriverWorkArea(Long entityId, Long personId, String flag, Long uerId, String deptId,
			String tenantId) {
		return R.fail("接收数据失败");
	}

    @Override
    public R<List<WorkareaRel>> getByWorkareaIds(List<String> workareaIds, Long entityType) {
        return R.fail("接收数据失败");
    }

}
