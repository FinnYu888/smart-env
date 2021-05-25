package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.websocket.service.IWorkAreaService;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName WorkAreaService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 18:45
 * @Version 1.0
 */
@Slf4j
@Service
public class WorkAreaService implements IWorkAreaService {

    @Autowired
    private IWorkareaRelClient workareaRelClient;

    @Autowired
    private IWorkareaClient workareaClient;


    @Autowired
    private ISysClient sysClient;


    @Override
    public Future<List<Region>> listRegionByEntityId(Long entityId, Long entityType, String tenantId) {
        List<Region> regionList = new ArrayList<Region>();
        List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(entityId,entityType).getData();
        if(ObjectUtils.isEmpty(workareaRelList)){
            return null;
        }
        if(workareaRelList.size() > 0){
            for(WorkareaRel workareaRel:workareaRelList){
                WorkareaInfo workareaInfo =  workareaClient.getWorkInfoById(workareaRel.getWorkareaId()).getData();
                if(!ObjectUtils.isEmpty(workareaInfo)){
                    Region region = sysClient.getRegion(workareaInfo.getRegionId()).getData();
                    if(!ObjectUtils.isEmpty(region)){
                        regionList.add(region);
                    }
                }
            }
        }

        return new AsyncResult<List<Region>>(regionList);
    }
}
