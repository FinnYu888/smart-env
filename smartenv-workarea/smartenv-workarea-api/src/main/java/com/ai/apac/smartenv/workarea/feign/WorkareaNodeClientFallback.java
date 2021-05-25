package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: yupf3
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:12    yupf3          v1.0.0             修改原因
 */
public class WorkareaNodeClientFallback implements IWorkareaNodeClient {
    @Override
    public R<Boolean> saveWorkAreaNode(WorkareaNode node) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaNode>> queryRegionNodesList(Long regionId){
        return R.fail("接收数据失败");
    }

    @Override
    public R<Boolean> deleteWorkAreaNodes(Long regionId){
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<WorkareaNode>> queryNodeByWorkareaId(Long workAreaId) {
        return R.fail("接收数据失败");
    }

}
