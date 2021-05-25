package com.ai.apac.smartenv.job.dto;

import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.job.entity.JobExecCfg;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobExecCfgDTO extends JobExecCfg {
    private static final long serialVersionUID = 1L;

}
