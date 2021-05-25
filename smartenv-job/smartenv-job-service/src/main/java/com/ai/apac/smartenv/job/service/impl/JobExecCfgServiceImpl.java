package com.ai.apac.smartenv.job.service.impl;

import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.job.entity.JobExecCfg;
import com.ai.apac.smartenv.job.mapper.JobExecCfgMapper;
import com.ai.apac.smartenv.job.service.IJobExecCfgService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobExecCfgServiceImpl extends BaseServiceImpl<JobExecCfgMapper, JobExecCfg>  implements IJobExecCfgService {
}
