package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClient
 * @Description:
 * @version: v1.0.0
 * @author: yupf3
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:24    yupf3          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class WorkareaNodeClient implements IWorkareaNodeClient {
    @Autowired
    private IWorkareaNodeService workareaNodeService;


    @Override
    @GetMapping(SAVE_NODE)
    public R<Boolean> saveWorkAreaNode(WorkareaNode node) {
        return R.data(workareaNodeService.save(node));
    }

    @Override
    @GetMapping(QUERY_NODES)
    public R<List<WorkareaNode>> queryRegionNodesList(@RequestParam("regionId") Long regionId) {
        return R.data(workareaNodeService.list(new QueryWrapper<WorkareaNode>().eq("region_id",regionId)));
    }

    @Override
    @GetMapping(DELETE_NODES)
    public R<Boolean> deleteWorkAreaNodes(@RequestParam("regionId") Long regionId) {
        return R.data(workareaNodeService.remove(new QueryWrapper<WorkareaNode>().eq("region_id",regionId)));
    }

    @Override
    @GetMapping(QUERY_BY_WORKAREA_ID)
    public R<List<WorkareaNode>> queryNodeByWorkareaId(Long workAreaId){
        WorkareaNode query=new WorkareaNode();
        query.setWorkareaId(workAreaId);
        return R.data(workareaNodeService.list(Condition.getQueryWrapper(query)));
    }

}
