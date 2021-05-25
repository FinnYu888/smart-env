package com.ai.apac.smartenv.job.scheduler;

import com.ai.apac.smartenv.assessment.feign.IAssessmentClient;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName EndStaffKpiInsTask
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/26 14:41
 * @Version 1.0
 */
@Component
@AllArgsConstructor
public class EndStaffKpiInsTask {


    private IAssessmentClient assessmentClient;

    // 每天0点执行
    //@Scheduled(cron = "0 0 0 ? * *")
    public void syncScheduleWork() throws IOException {
        assessmentClient.endKpiTarget();
    }

}
