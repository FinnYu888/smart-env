package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.ai.apac.smartenv.workarea.service.IWorkareaRelService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaRelClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  21:40    panfeng          v1.0.0             修改原因
 */

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class WorkareaRelClient  implements IWorkareaRelClient{

    @Autowired
    private IWorkareaRelService workareaRelService;
    @Autowired
    private IWorkareaInfoService workareaInfoService;

    @Override
    @GetMapping(API_WORKAREA_ID)
    public R<List<WorkareaRel>> getByEntityIdAndType(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType) {
        WorkareaRel rel=new WorkareaRel();
        rel.setEntityId(entityId);
        rel.setEntityType(entityType);
        return R.data(workareaRelService.list(Condition.getQueryWrapper(rel)));
    }

    @Override
    @GetMapping(API_QUERY_BY_CONDITION)
    public R<List<WorkareaRel>> queryByCondition(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType,@RequestParam("startTime") Long startTime,@RequestParam("endTime") Long endTime,String tenantId) {
        // 先查询历史，如果历史有数据这说明当前查询为解绑的区域数据，否则为查询当前绑定的数据
        WorkareaRel rel = new WorkareaRel();
        rel.setEntityId(entityId);
        rel.setEntityType(entityType);
        rel.setTenantId(tenantId);
        rel.setIsDeleted(1);
        List<WorkareaRel> workareaRelList = workareaRelService.queryWorkareaRelHList(rel,TimeUtil.formDateToTimestamp(new Date(startTime)),TimeUtil.formDateToTimestamp(new Date(endTime)));


        if(CollectionUtil.isEmpty(workareaRelList)) {
            // 查询当前绑定的数据，1-查询的是历史时间区间（），2-查询当前当天时间区间
            QueryWrapper<WorkareaRel> wrapper = new QueryWrapper<WorkareaRel>();
            wrapper.lambda().eq(WorkareaRel::getEntityId,entityId);
            wrapper.lambda().eq(WorkareaRel::getEntityType, entityType);
//            wrapper.lambda().between(WorkareaRel::getCreateTime,TimeUtil.formDateToTimestamp(new Date(startTime)),TimeUtil.formDateToTimestamp(new Date(endTime)));
            workareaRelList = workareaRelService.list(wrapper);
        }
        return R.data(workareaRelList);
    }

    @Override
    @PostMapping(API_GET_BY_CONDITION)
    public R<List<WorkareaRel>> getByCondition(String workAreaId, Long entityType, List<Long> entityIds) {
        QueryWrapper<WorkareaRel> wrapper = new QueryWrapper<WorkareaRel>();
        wrapper.lambda().eq(WorkareaRel::getWorkareaId,workAreaId);
        wrapper.lambda().eq(WorkareaRel::getEntityType, entityType);
        wrapper.lambda().in(WorkareaRel::getEntityId,entityIds);
        return R.data(workareaRelService.list(wrapper));
    }

    @Override
    @PostMapping(API_GET_BY_ID_AND_TYPE)
    public R<List<WorkareaRel>> getByIdAndType(@RequestParam("workAreaId") String workAreaId,@RequestParam("entityType") Long entityType){
        QueryWrapper<WorkareaRel> wrapper = new QueryWrapper<WorkareaRel>();
        wrapper.lambda().eq(WorkareaRel::getWorkareaId,workAreaId);
        wrapper.lambda().eq(WorkareaRel::getEntityType, entityType);
        return R.data(workareaRelService.list(wrapper));
    }

	@Override
	@PostMapping(API_UNBIND_WORKAREA)
	public R<Boolean> unbindWorkarea(Long entityId, Long entityType) {
		return R.data(workareaInfoService.unbindWorkarea(entityId, entityType));
	}

    @Override
    @PostMapping(API_SYNC_DRIVER_WORKAREA)
    public R<Boolean> syncDriverWorkArea(Long entityId, Long personId, String flag, Long userId, String deptId,
			String tenantId) {
    	BladeUser bladeUser = new BladeUser();
    	bladeUser.setUserId(userId);
    	bladeUser.setDeptId(deptId);
    	bladeUser.setTenantId(tenantId);
        return R.data(workareaInfoService.syncDriverWorkArea(entityId,personId,flag,bladeUser));
    }

    @Override
    @PostMapping(GET_BY_WORKAREA_IDS)
    public R<List<WorkareaRel>> getByWorkareaIds(@RequestBody List<String> workareaIds, @RequestParam Long entityType){
        WorkareaRel workareaRel=new WorkareaRel();
        workareaRel.setEntityType(entityType);
        QueryWrapper<WorkareaRel> wrapper=new QueryWrapper<>(workareaRel);
        wrapper.in("workarea_id",workareaIds);
        return R.data(workareaRelService.list(wrapper));
    }


}
